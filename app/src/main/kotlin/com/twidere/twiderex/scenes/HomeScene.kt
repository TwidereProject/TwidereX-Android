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
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.twidere.twiderex.R
import com.twidere.twiderex.component.UserMetrics
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarDefaults
import com.twidere.twiderex.component.foundation.AppBottomNavigation
import com.twidere.twiderex.component.foundation.EdgePadding
import com.twidere.twiderex.component.foundation.EdgeToEdgeBox
import com.twidere.twiderex.component.foundation.IconTabsComponent
import com.twidere.twiderex.component.foundation.Pager
import com.twidere.twiderex.component.foundation.PagerState
import com.twidere.twiderex.component.foundation.rememberPagerState
import com.twidere.twiderex.component.lazy.LazyListController
import com.twidere.twiderex.component.lazy.LocalLazyListController
import com.twidere.twiderex.component.lazy.itemDivider
import com.twidere.twiderex.component.status.UserAvatar
import com.twidere.twiderex.component.status.UserName
import com.twidere.twiderex.component.status.UserScreenName
import com.twidere.twiderex.extensions.withElevation
import com.twidere.twiderex.model.PlatformType
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.navigation.Route
import com.twidere.twiderex.preferences.LocalAppearancePreferences
import com.twidere.twiderex.preferences.proto.AppearancePreferences
import com.twidere.twiderex.scenes.home.HomeNavigationItem
import com.twidere.twiderex.scenes.home.HomeTimelineItem
import com.twidere.twiderex.scenes.home.MastodonNotificationItem
import com.twidere.twiderex.scenes.home.MeItem
import com.twidere.twiderex.scenes.home.MentionItem
import com.twidere.twiderex.scenes.home.SearchItem
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.LocalActiveAccountViewModel
import com.twidere.twiderex.ui.LocalIsActiveEdgeToEdge
import com.twidere.twiderex.ui.LocalNavController
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.ui.mediumEmphasisContentContentColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeScene() {
    val account = LocalActiveAccount.current ?: return
    val scope = rememberCoroutineScope()
    val timelineController = remember {
        LazyListController()
    }
    val tabPosition = LocalAppearancePreferences.current.tapPosition
    val menus = remember(account.type) {
        listOf(
            HomeTimelineItem(),
        ).let {
            it + when (account.type) {
                PlatformType.Twitter -> MentionItem()
                PlatformType.StatusNet -> TODO()
                PlatformType.Fanfou -> TODO()
                PlatformType.Mastodon -> MastodonNotificationItem()
            }
        }.let {
            it + listOf(
                SearchItem(),
                MeItem(),
            )
        }
    }
    val pagerState = rememberPagerState(
        maxPage = menus.lastIndex
    )
    val scaffoldState = rememberScaffoldState()
    if (scaffoldState.drawerState.isOpen) {
        BackHandler {
            scope.launch {
                scaffoldState.drawerState.close()
            }
        }
    }
    TwidereScene {
        Scaffold(
            scaffoldState = scaffoldState,
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
                Pager(state = pagerState) {
                    CompositionLocalProvider(
                        *if (page == currentPage) {
                            arrayOf(LocalLazyListController provides timelineController)
                        } else {
                            emptyArray()
                        }
                    ) {
                        NestedScrollConnectionAppBars(
                            tabPosition = tabPosition,
                            menus = menus,
                            pagerState = pagerState,
                            scaffoldState = scaffoldState,
                            timelineController = timelineController,
                            scope = scope
                        ) {
                            menus[page].content()
                        }
                    }
                }
            }
        }
    }
}
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NestedScrollConnectionAppBars(
    tabPosition: AppearancePreferences.TabPosition,
    menus: List<HomeNavigationItem>,
    pagerState: PagerState,
    scaffoldState: ScaffoldState,
    timelineController: LazyListController,
    scope: CoroutineScope,
    content: @Composable () -> Unit
) {
    val isEdgeToEdgeActive = LocalIsActiveEdgeToEdge.current
    val statusTop = with(LocalDensity.current) {
        if (isEdgeToEdgeActive) LocalWindowInsets.current.statusBars.top.toDp() else 0.dp
    }
    val toolbarHeight = AppBarDefaults.AppBarHeight + statusTop
    val toolbarHeightPx = with(LocalDensity.current) { toolbarHeight.roundToPx().toFloat() }
    val toolbarOffsetHeightPx = remember { mutableStateOf(0f) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newOffset = toolbarOffsetHeightPx.value + delta
                toolbarOffsetHeightPx.value = newOffset.coerceIn(-toolbarHeightPx, 0f)
                return Offset.Zero
            }
        }
    }
    Box(
        Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        // todo add content padding to timeline
        content.invoke()
        if (tabPosition == AppearancePreferences.TabPosition.Bottom) {
            AnimatedVisibility(
                visible = menus[pagerState.currentPage].withAppBar,
                enter = expandVertically(clip = false),
                exit = shrinkVertically(clip = false),
                modifier = if (isEdgeToEdgeActive) Modifier
                    .offset { IntOffset(x = 0, y = toolbarOffsetHeightPx.value.roundToInt()) } else Modifier,
            ) {
                AppBar(
                    backgroundColor = MaterialTheme.colors.surface.withElevation(),
                    title = {
                        Text(text = menus[pagerState.currentPage].name())
                    },
                    navigationIcon = {
                        MenuAvatar(scaffoldState)
                    },
                    elevation = if (menus[pagerState.currentPage].withAppBar) {
                        AppBarDefaults.TopAppBarElevation
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
                    AppBarDefaults.TopAppBarElevation
                } else {
                    0.dp
                }
            }
            Surface(
                elevation = elevation,
                modifier = if (isEdgeToEdgeActive) Modifier
                    .offset { IntOffset(x = 0, y = toolbarOffsetHeightPx.value.roundToInt()) } else Modifier
            ) {
                EdgeToEdgeBox(edgePadding = EdgePadding(bottom = false)) {
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
    items: List<HomeNavigationItem>,
    selectedItem: Int,
    onItemSelected: (Int) -> Unit,
) {
    AppBottomNavigation(
        backgroundColor = MaterialTheme.colors.background,
        modifier = if (LocalIsActiveEdgeToEdge.current) Modifier.navigationBarsPadding() else Modifier
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

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
private fun HomeDrawer(scaffoldState: ScaffoldState) {
    var showAccounts by remember { mutableStateOf(false) }
    EdgeToEdgeBox(edgePadding = EdgePadding(end = false)) {
        Column {
            Spacer(modifier = Modifier.height(16.dp))

            val account = LocalActiveAccount.current
            val currentUser = account?.toUi()
            val navController = LocalNavController.current
            DrawerUserHeader(
                currentUser,
                showAccounts,
            ) {
                showAccounts = !showAccounts
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (currentUser != null) {
                UserMetrics(user = currentUser)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Divider()
            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                val activeAccountViewModel = LocalActiveAccountViewModel.current
                val accounts by activeAccountViewModel.allAccounts.observeAsState(initial = emptyList())
                val allAccounts = accounts.filter { it.accountKey != account?.accountKey }
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
                }
                androidx.compose.animation.AnimatedVisibility(
                    visible = !showAccounts,
                    enter = fadeIn() + expandVertically(),
                    exit = shrinkVertically() + fadeOut(),
                ) {
                    LazyColumn {
                        item {
                            DrawerMenuItem(
                                onClick = {
                                    navController.navigate(Route.Draft.List)
                                },
                                title = R.string.scene_drafts_title,
                                icon = R.drawable.ic_note,
                            )
                        }
                        item {
                            DrawerMenuItem(
                                onClick = {
                                    navController.navigate(Route.Lists.Home)
                                },
                                title = R.string.scene_lists_title,
                                icon = R.drawable.ic_lists,
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
}
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DrawerMenuItem(
    onClick: () -> Unit,
    @StringRes title: Int,
    @DrawableRes icon: Int,
    @StringRes iconDescription: Int = title
) {
    ListItem(
        modifier = Modifier.clickable(
            onClick = {
                onClick.invoke()
            }
        ),
        text = {
            Text(text = stringResource(id = title))
        },
        icon = {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = stringResource(
                    id = iconDescription
                )
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
                        id = R.string.accessibility_scene_home_drawer_account_dropdown
                    )
                )
            }
        }
    )
}
