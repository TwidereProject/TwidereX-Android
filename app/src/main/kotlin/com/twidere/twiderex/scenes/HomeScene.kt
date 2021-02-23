/*
 *  Twidere X
 *
 *  Copyright (C) 2020-2021 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of Twidere X.
 * 
 *  Twidere X is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Twidere X is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Twidere X. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.scenes

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.navigate
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.IconTabsComponent
import com.twidere.twiderex.component.foundation.Pager
import com.twidere.twiderex.component.foundation.TopAppBarElevation
import com.twidere.twiderex.component.foundation.rememberPagerState
import com.twidere.twiderex.component.lazy.LazyListController
import com.twidere.twiderex.component.lazy.LocalLazyListController
import com.twidere.twiderex.component.lazy.itemDivider
import com.twidere.twiderex.component.status.UserAvatar
import com.twidere.twiderex.extensions.withElevation
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.navigation.Route
import com.twidere.twiderex.preferences.LocalAppearancePreferences
import com.twidere.twiderex.preferences.proto.AppearancePreferences
import com.twidere.twiderex.scenes.home.HomeNavigationItem
import com.twidere.twiderex.scenes.home.HomeTimelineItem
import com.twidere.twiderex.scenes.home.MeItem
import com.twidere.twiderex.scenes.home.MentionItem
import com.twidere.twiderex.scenes.home.SearchItem
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.LocalActiveAccountViewModel
import com.twidere.twiderex.ui.LocalNavController
import com.twidere.twiderex.ui.TwidereXTheme
import com.twidere.twiderex.ui.mediumEmphasisContentContentColor
import com.twidere.twiderex.ui.standardPadding
import kotlinx.coroutines.launch

@Composable
fun HomeScene() {
    val scope = rememberCoroutineScope()
    val timelineController = remember {
        LazyListController()
    }
    val tabPosition = LocalAppearancePreferences.current.tapPosition
    val menus = listOf(
        HomeTimelineItem(),
        MentionItem(),
        SearchItem(),
        MeItem(),
    )
    val pagerState = rememberPagerState(
        maxPage = menus.lastIndex
    )
    val scaffoldState = rememberScaffoldState()
    if (scaffoldState.drawerState.isOpen) {
        BackHandler {
            scaffoldState.drawerState.close()
        }
    }
    TwidereXTheme {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                if (tabPosition == AppearancePreferences.TabPosition.Bottom) {
                    if (menus[pagerState.currentPage].withAppBar) {
                        AppBar(
                            backgroundColor = MaterialTheme.colors.surface.withElevation(),
                            title = {
                                Text(text = menus[pagerState.currentPage].name())
                            },
                            navigationIcon = {
                                MenuAvatar(scaffoldState)
                            },
                            elevation = if (menus[pagerState.currentPage].withAppBar) {
                                TopAppBarElevation
                            } else {
                                0.dp
                            }
                        )
                    }
                } else {
                    val transition = updateTransition(
                        targetState = menus[pagerState.currentPage].withAppBar,
                    )
                    val elevation by transition.animateDp {
                        if (it) {
                            TopAppBarElevation
                        } else {
                            0.dp
                        }
                    }
                    Surface(
                        elevation = elevation
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MenuAvatar(scaffoldState)
                            IconTabsComponent(
                                modifier = Modifier.weight(1f),
                                items = menus.map { it.icon() to it.name() },
                                selectedItem = pagerState.currentPage,
                                divider = {
                                    TabRowDefaults.Divider(thickness = 0.dp)
                                },
                                onItemSelected = {
                                    if (pagerState.currentPage == it) {
                                        timelineController.scrollToTop()
                                    }
                                    scope.launch {
                                        pagerState.selectPage {
                                            pagerState.currentPage = it
                                        }
                                    }
                                },
                            )
                        }
                    }
                }
            },
            bottomBar = {
                if (tabPosition == AppearancePreferences.TabPosition.Bottom) {
                    HomeBottomNavigation(menus, pagerState.currentPage) {
                        if (pagerState.currentPage == it) {
                            timelineController.scrollToTop()
                        }
                        scope.launch {
                            pagerState.selectPage {
                                pagerState.currentPage = it
                            }
                        }
                    }
                }
            },
            drawerContent = {
                HomeDrawer(scaffoldState)
            }
        ) {
            Box(
                modifier = Modifier.padding(it)
            ) {
                Providers(
                    LocalLazyListController provides timelineController
                ) {
                    Pager(state = pagerState) {
                        menus[page].content()
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuAvatar(scaffoldState: ScaffoldState) {
    LocalActiveAccount.current?.let { account ->
        val user = remember(account) {
            account.toUi()
        }
        UserAvatar(
            modifier = Modifier.padding(horizontal = standardPadding * 2),
            size = 32.dp,
            user = user,
            onClick = {
                if (scaffoldState.drawerState.isOpen) {
                    scaffoldState.drawerState.close()
                } else {
                    scaffoldState.drawerState.open()
                }
            }
        )
    }
}

@Composable
fun HomeBottomNavigation(
    items: List<HomeNavigationItem>,
    selectedItem: Int,
    onItemSelected: (Int) -> Unit,
) {
    BottomNavigation(
        backgroundColor = MaterialTheme.colors.background
    ) {
        items.forEachIndexed { index, item ->
            BottomNavigationItem(
                selectedContentColor = MaterialTheme.colors.primary,
                unselectedContentColor = mediumEmphasisContentContentColor,
                icon = { Icon(painter = item.icon(), contentDescription = item.name()) },
                selected = selectedItem == index,
                onClick = { onItemSelected.invoke(index) }
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun HomeDrawer(scaffoldState: ScaffoldState) {
    var showAccounts by remember { mutableStateOf(false) }

    Column {
        Spacer(modifier = Modifier.height(16.dp))

        val account = LocalActiveAccount.current
        val currentUser = account?.toUi()
        val navController = LocalNavController.current
        DrawerUserHeader(
            currentUser
        ) {
            showAccounts = !showAccounts
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = currentUser?.friendsCount.toString())
                Text(text = stringResource(id = R.string.common_controls_profile_dashboard_following))
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = currentUser?.followersCount.toString())
                Text(text = stringResource(id = R.string.common_controls_profile_dashboard_followers))
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = currentUser?.listedCount.toString())
                Text(text = stringResource(id = R.string.common_controls_profile_dashboard_listed))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Divider()
        Box(
            modifier = Modifier
                .weight(1f)
        ) {
            if (showAccounts) {
                val activeAccountViewModel = LocalActiveAccountViewModel.current
                val accounts by activeAccountViewModel.allAccounts.observeAsState(initial = emptyList())
                val allAccounts = accounts.filter { it.accountKey != account?.accountKey }
                LazyColumn {
                    items(allAccounts) {
                        val user = it.toUi()
                        ListItem(
                            modifier = Modifier.clickable(
                                onClick = {
                                    activeAccountViewModel.setActiveAccount(it)
                                }
                            ),
                            icon = {
                                UserAvatar(
                                    user = user,
                                )
                            },
                            text = {
                                Text(
                                    text = user.name,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            },
                            secondaryText = {
                                Text(
                                    text = "@${user.screenName}",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            },
                        )
                    }
                    if (allAccounts.any()) {
                        itemDivider()
                    }
                    item {
                        ListItem(
                            modifier = Modifier.clickable(
                                onClick = {
                                    navController.navigate(Route.SignIn.Default)
                                }
                            ),
                            text = {
                                Text(text = stringResource(id = R.string.scene_drawer_sign_in))
                            }
                        )
                    }
                    itemDivider()
                    item {
                        ListItem(
                            modifier = Modifier.clickable(
                                onClick = {
                                    navController.navigate(Route.Settings.AccountManagement)
                                }
                            ),
                            text = {
                                Text(text = stringResource(id = R.string.scene_manage_accounts_title))
                            }
                        )
                    }
                }
            } else {
                LazyColumn {
                    item {
                        ListItem(
                            modifier = Modifier.clickable(
                                onClick = {
                                    navController.navigate(Route.Draft.List)
                                }
                            ),
                            text = {
                                Text(text = stringResource(id = R.string.scene_drafts_title))
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_note),
                                    contentDescription = stringResource(
                                        id = R.string.scene_drafts_title
                                    )
                                )
                            },
                        )
                    }
                }
            }
        }

        Divider()
        ListItem(
            modifier = Modifier.clickable(
                onClick = {
                    scaffoldState.drawerState.close {
                        navController.navigate(Route.Settings.Home)
                    }
                }
            ),
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_adjustments_horizontal),
                    contentDescription = stringResource(
                        id = R.string.scene_settings_title
                    )
                )
            },
            text = {
                Text(text = stringResource(id = R.string.scene_settings_title))
            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DrawerUserHeader(
    user: UiUser?,
    onTrailingClicked: () -> Unit = {},
) {
    ListItem(
        icon = {
            user?.let {
                UserAvatar(
                    user = it,
                )
            }
        },
        text = {
            Text(
                text = user?.name ?: "",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        secondaryText = {
            Text(
                text = "@${user?.screenName}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        trailing = {
            IconButton(
                onClick = {
                    onTrailingClicked.invoke()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = stringResource(
                        id = R.string.accessibility_scene_home_drawer_account_dropdown
                    )
                )
            }
        }
    )
}
