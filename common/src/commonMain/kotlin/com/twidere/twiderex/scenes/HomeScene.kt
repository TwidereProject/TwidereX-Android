/*
 *  Twidere X
 *
 *  Copyright (C) TwidereProject and Contributors
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

// import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.component.UserMetrics
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarDefaults
import com.twidere.twiderex.component.foundation.ApplyNotification
import com.twidere.twiderex.component.foundation.IconTabsComponent
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.foundation.NestedScrollScaffold
import com.twidere.twiderex.component.foundation.Pager
import com.twidere.twiderex.component.foundation.PagerState
import com.twidere.twiderex.component.foundation.rememberPagerState
import com.twidere.twiderex.component.lazy.divider
import com.twidere.twiderex.component.painterResource
import com.twidere.twiderex.component.status.UserAvatar
import com.twidere.twiderex.component.status.UserName
import com.twidere.twiderex.component.status.UserScreenName
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.extensions.observeAsState
import com.twidere.twiderex.extensions.withElevation
import com.twidere.twiderex.model.HomeMenus
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.navigation.Root
import com.twidere.twiderex.preferences.LocalAppearancePreferences
import com.twidere.twiderex.preferences.model.AppearancePreferences
import com.twidere.twiderex.scenes.home.item
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.LocalActiveAccountViewModel
import com.twidere.twiderex.ui.LocalNavController
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.ui.mediumEmphasisContentContentColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.BackHandler

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeScene() {
    val account = LocalActiveAccount.current ?: return
    val scope = rememberCoroutineScope()
    val tabPosition = LocalAppearancePreferences.current.tabPosition
    val hideTab = LocalAppearancePreferences.current.hideTabBarWhenScroll
    val hideFab = LocalAppearancePreferences.current.hideFabWhenScroll
    val hideAppBar = LocalAppearancePreferences.current.hideAppBarWhenScroll
    val menuOrder by account.preferences.homeMenuOrder.observeAsState(
        initial = HomeMenus.values().map { it to it.showDefault }
    )
    val menus = remember(menuOrder) {
        menuOrder.filter { it.second && it.first.supportedPlatformType.contains(account.type) }
            .map { it.first }
    }
    val pagerState = rememberPagerState(
        pageCount = menus.size,
    )
    val scaffoldState = rememberScaffoldState()
    if (scaffoldState.drawerState.isOpen) {
        BackHandler {
            scope.launch {
                scaffoldState.drawerState.close()
            }
        }
    }
    ApplyNotification(scaffoldState.snackbarHostState)
    TwidereScene(
        navigationBarColorProvider = {
            if (tabPosition == AppearancePreferences.TabPosition.Bottom) {
                MaterialTheme.colors.surface.withElevation()
            } else {
                MaterialTheme.colors.surface
            }
        },
    ) {
        if (!menus.any()) {
            EmptyColumnHomeContent(scaffoldState)
        } else {
            NestedScrollScaffold(
                scaffoldState = scaffoldState,
                enableBottomBarNestedScroll = hideTab,
                bottomBar = {
                    if (tabPosition == AppearancePreferences.TabPosition.Bottom) {
                        HomeBottomNavigation(
                            items = menus,
                            selectedItem = pagerState.currentPage,
                        ) {
                            if (pagerState.currentPage == it) {
                                scope.launch {
                                    menus[it].item.lazyListController.scrollToTop()
                                }
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
                },
                floatingActionButton = {
                    menus[pagerState.currentPage].item.Fab()
                },
                floatingActionButtonPosition = menus[pagerState.currentPage].item.floatingActionButtonPosition,
                enableFloatingActionButtonNestedScroll = hideFab,
                topBar = {
                    HomeAppBar(
                        tabPosition = tabPosition,
                        menus = menus,
                        pagerState = pagerState,
                        scaffoldState = scaffoldState,
                        scope = scope,
                    )
                },
                enableTopBarNestedScroll = hideAppBar
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    Pager(
                        state = pagerState,
                    ) {
                        menus[page].item.Content()
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyColumnHomeContent(scaffoldState: ScaffoldState) {
    InAppNotificationScaffold(
        scaffoldState = scaffoldState,
        topBar = {
            AppBar(
                backgroundColor = MaterialTheme.colors.surface.withElevation(),
                navigationIcon = {
                    MenuAvatar(scaffoldState)
                },
            )
        },
        drawerContent = {
            HomeDrawer(scaffoldState = scaffoldState)
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                painter = painterResource(res = com.twidere.twiderex.MR.files.ic_empty_column),
                contentDescription = null,
            )
            Spacer(modifier = Modifier.height(EmptyColumnHomeContentDefaults.VerticalPadding))
            CompositionLocalProvider(
                LocalContentAlpha provides ContentAlpha.disabled
            ) {
                Text(
                    text = "Modify the layout settings",
                    style = MaterialTheme.typography.h6,
                )
            }
        }
    }
}

private object EmptyColumnHomeContentDefaults {
    val VerticalPadding = 48.dp
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeAppBar(
    modifier: Modifier = Modifier,
    tabPosition: AppearancePreferences.TabPosition,
    menus: List<HomeMenus>,
    pagerState: PagerState,
    scaffoldState: ScaffoldState,
    scope: CoroutineScope,
) {
    if (tabPosition == AppearancePreferences.TabPosition.Bottom) {
        AnimatedVisibility(
            visible = menus[pagerState.currentPage].item.withAppBar,
            enter = expandVertically(clip = false),
            exit = shrinkVertically(clip = false),
            modifier = modifier
        ) {
            AppBar(
                backgroundColor = MaterialTheme.colors.surface.withElevation(),
                title = {
                    Text(text = menus[pagerState.currentPage].item.name())
                },
                navigationIcon = {
                    MenuAvatar(scaffoldState)
                },
                elevation = if (menus[pagerState.currentPage].item.withAppBar) {
                    AppBarDefaults.TopAppBarElevation
                } else {
                    0.dp
                },
            )
        }
    } else {
        val transition = updateTransition(
            targetState = menus[pagerState.currentPage].item.withAppBar,
        )
        val elevation by transition.animateDp {
            if (it) {
                AppBarDefaults.TopAppBarElevation
            } else {
                0.dp
            }
        }
        Surface(
            elevation = elevation,
            modifier = modifier
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                MenuAvatar(scaffoldState)
                IconTabsComponent(
                    modifier = Modifier.weight(1f),
                    items = menus.map { it.item.icon() to it.item.name() },
                    selectedItem = pagerState.currentPage,
                    divider = {
                        TabRowDefaults.Divider(thickness = 0.dp)
                    },
                    onItemSelected = {
                        if (pagerState.currentPage == it) {
                            scope.launch {
                                menus[it].item.lazyListController.scrollToTop()
                            }
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
}

@Composable
private fun MenuAvatar(scaffoldState: ScaffoldState) {
    val scope = rememberCoroutineScope()
    LocalActiveAccount.current?.let { account ->
        val user = remember(account) {
            account.toUi()
        }
        UserAvatar(
            modifier = Modifier.padding(MenuAvatarDefaults.AvatarPadding),
            size = MenuAvatarDefaults.Size,
            user = user,
            onClick = {
                scope.launch {
                    if (scaffoldState.drawerState.isOpen) {
                        scaffoldState.drawerState.close()
                    } else {
                        scaffoldState.drawerState.open()
                    }
                }
            }
        )
    }
}

private object MenuAvatarDefaults {
    val AvatarPadding = PaddingValues(
        horizontal = 16.dp,
        vertical = 0.dp
    )
    val Size = 32.dp
}

@Composable
fun HomeBottomNavigation(
    modifier: Modifier = Modifier,
    items: List<HomeMenus>,
    selectedItem: Int,
    onItemSelected: (Int) -> Unit,
) {
    val pureDark = LocalAppearancePreferences.current.isDarkModePureBlack
    val isLight = MaterialTheme.colors.isLight
    Column {
        if (pureDark && !isLight) {
            Divider()
        }
        BottomNavigation(
            backgroundColor = MaterialTheme.colors.surface.withElevation(),
            modifier = modifier
        ) {
            items.forEachIndexed { index, item ->
                BottomNavigationItem(
                    selectedContentColor = MaterialTheme.colors.primary,
                    unselectedContentColor = mediumEmphasisContentContentColor,
                    icon = {
                        Icon(
                            painter = item.item.icon(),
                            contentDescription = item.item.name()
                        )
                    },
                    selected = selectedItem == index,
                    onClick = { onItemSelected.invoke(index) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
private fun HomeDrawer(scaffoldState: ScaffoldState) {
    var showAccounts by remember { mutableStateOf(false) }

    Column {
        Spacer(modifier = Modifier.height(16.dp))

        val account = LocalActiveAccount.current ?: return
        val currentUser = account.toUi()
        val navController = LocalNavController.current
        DrawerUserHeader(
            currentUser,
            showAccounts,
        ) {
            showAccounts = !showAccounts
        }

        Spacer(modifier = Modifier.height(16.dp))

        UserMetrics(user = currentUser)

        Spacer(modifier = Modifier.height(24.dp))

        Divider()
        Box(
            modifier = Modifier
                .weight(1f)
        ) {
            val activeAccountViewModel = LocalActiveAccountViewModel.current
            val accounts by activeAccountViewModel.allAccounts.observeAsState(initial = emptyList())
            val allAccounts = accounts.filter { it.accountKey != account.accountKey }
            androidx.compose.animation.AnimatedVisibility(
                visible = showAccounts,
                enter = fadeIn() + expandVertically(),
                exit = shrinkVertically() + fadeOut(),
            ) {
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
                                    withPlatformIcon = true,
                                    onClick = {
                                        activeAccountViewModel.setActiveAccount(it)
                                    }
                                )
                            },
                            text = {
                                UserName(user = user)
                            },
                            secondaryText = {
                                UserScreenName(user = user)
                            },
                        )
                    }
                    if (allAccounts.any()) {
                        divider()
                    }
                    item {
                        ListItem(
                            modifier = Modifier.clickable(
                                onClick = {
                                    navController.navigate(Root.SignIn.General)
                                }
                            ),
                            text = {
                                Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_drawer_sign_in))
                            }
                        )
                    }
                    divider()
                    item {
                        ListItem(
                            modifier = Modifier.clickable(
                                onClick = {
                                    navController.navigate(Root.Settings.AccountManagement)
                                }
                            ),
                            text = {
                                Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_manage_accounts_title))
                            }
                        )
                    }
                }
            }
            androidx.compose.animation.AnimatedVisibility(
                visible = !showAccounts,
                enter = fadeIn() + expandVertically(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                val menuOrder by account.preferences.homeMenuOrder.observeAsState(
                    initial = HomeMenus.values().map { it to it.showDefault }
                )
                LazyColumn {
                    items(
                        menuOrder.filter {
                            !it.second && it.first.supportedPlatformType.contains(
                                account.type
                            )
                        }.map { it.first }
                    ) {
                        DrawerMenuItem(
                            onClick = {
                                navController.navigate(it.item.route)
                            },
                            title = it.item.name(),
                            icon = it.item.icon(),
                        )
                    }
                }
            }
        }

        Divider()
        val scope = rememberCoroutineScope()
        ListItem(
            modifier = Modifier.clickable(
                onClick = {
                    scope.launch {
                        scaffoldState.drawerState.close()
                        navController.navigate(Root.Settings.Home)
                    }
                }
            ),
            icon = {
                Icon(
                    painter = painterResource(res = com.twidere.twiderex.MR.files.ic_adjustments_horizontal),
                    contentDescription = stringResource(
                        res = com.twidere.twiderex.MR.strings.scene_settings_title
                    )
                )
            },
            text = {
                Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_title))
            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DrawerMenuItem(
    onClick: () -> Unit,
    title: String,
    icon: Painter,
    iconDescription: String = title
) {
    ListItem(
        modifier = Modifier.clickable(
            onClick = {
                onClick.invoke()
            }
        ),
        text = {
            Text(text = title)
        },
        icon = {
            Icon(
                painter = icon,
                contentDescription = iconDescription
            )
        },
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DrawerUserHeader(
    user: UiUser?,
    showAccounts: Boolean,
    onTrailingClicked: () -> Unit = {},
) {
    ListItem(
        icon = {
            user?.let {
                UserAvatar(
                    user = it,
                    withPlatformIcon = true,
                )
            }
        },
        text = {
            if (user != null) {
                UserName(user = user)
            }
        },
        secondaryText = {
            if (user != null) {
                UserScreenName(user = user)
            }
        },
        trailing = {
            val transition = updateTransition(targetState = showAccounts)
            val rotate by transition.animateFloat {
                if (it) {
                    180f
                } else {
                    0f
                }
            }
            IconButton(
                onClick = {
                    onTrailingClicked.invoke()
                }
            ) {
                Icon(
                    modifier = Modifier.rotate(rotate),
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = stringResource(
                        res = com.twidere.twiderex.MR.strings.accessibility_scene_home_drawer_account_dropdown
                    )
                )
            }
        }
    )
}
