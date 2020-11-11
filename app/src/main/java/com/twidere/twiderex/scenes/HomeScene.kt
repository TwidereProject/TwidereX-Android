/*
 *  Twidere X
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
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

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.AmbientContentColor
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.AmbientEmphasisLevels
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.navigate
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.IconTabsComponent
import com.twidere.twiderex.component.foundation.TopAppBarElevation
import com.twidere.twiderex.component.home.HomeNavigationItem
import com.twidere.twiderex.component.home.HomeTimelineItem
import com.twidere.twiderex.component.home.MeItem
import com.twidere.twiderex.component.home.MentionItem
import com.twidere.twiderex.component.home.SearchItem
import com.twidere.twiderex.component.status.UserAvatar
import com.twidere.twiderex.extensions.withElevation
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.model.ui.UiUser.Companion.toUi
import com.twidere.twiderex.settings.AmbientTabPosition
import com.twidere.twiderex.settings.TabPosition
import com.twidere.twiderex.ui.AmbientActiveAccount
import com.twidere.twiderex.ui.AmbientNavController
import com.twidere.twiderex.ui.TwidereXTheme

@Composable
fun HomeScene() {
    val (selectedItem, setSelectedItem) = savedInstanceState { 0 }
    val tabPosition = AmbientTabPosition.current
    val menus = listOf(
        HomeTimelineItem(),
        MentionItem(),
        SearchItem(),
        MeItem(),
    )
    val scaffoldState = rememberScaffoldState()
    TwidereXTheme {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                if (tabPosition == TabPosition.Bottom) {
                    if (menus[selectedItem].withAppBar) {
                        AppBar(
                            backgroundColor = MaterialTheme.colors.surface.withElevation(),
                            title = {
                                Text(text = menus[selectedItem].name)
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        if (scaffoldState.drawerState.isOpen) {
                                            scaffoldState.drawerState.close()
                                        } else {
                                            scaffoldState.drawerState.open()
                                        }
                                    }
                                ) {
                                    Icon(asset = Icons.Default.Menu)
                                }
                            },
                            elevation = if (menus[selectedItem].withAppBar) {
                                TopAppBarElevation
                            } else {
                                0.dp
                            }
                        )
                    }
                } else {
                    Surface(
                        elevation = if (menus[selectedItem].withAppBar) {
                            TopAppBarElevation
                        } else {
                            0.dp
                        }
                    ) {
                        IconTabsComponent(
                            items = menus.map { it.icon },
                            selectedItem = selectedItem,
                            onItemSelected = {
                                setSelectedItem(it)
                            },
                        )
                    }
                }
            },
            bottomBar = {
                if (tabPosition == TabPosition.Bottom) {
                    HomeBottomNavigation(menus, selectedItem) {
                        setSelectedItem(it)
                    }
                }
            },
            drawerContent = {
                HomeDrawer(scaffoldState)
            }
        ) {
            Box(
                modifier = Modifier.padding(
                    start = it.start,
                    bottom = it.bottom,
                    end = it.end,
                    top = it.top,
                )
            ) {
                Crossfade(
                    current = selectedItem,
                ) {
                    menus[it].onCompose()
                }
            }
        }
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
                unselectedContentColor = AmbientEmphasisLevels.current.medium.applyEmphasis(
                    AmbientContentColor.current
                ),
                icon = { Icon(item.icon) },
                selected = selectedItem == index,
                onClick = { onItemSelected.invoke(index) }
            )
        }
    }
}

@OptIn(ExperimentalLazyDsl::class)
@Composable
private fun HomeDrawer(scaffoldState: ScaffoldState) {

    Column {
        Spacer(modifier = Modifier.height(16.dp))

        val account = AmbientActiveAccount.current
        val user = account?.user?.toUi()
        val navController = AmbientNavController.current
        DrawerUserHeader(user)

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = user?.friendsCount.toString())
                Text(text = "Following")
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = user?.followersCount.toString())
                Text(text = "Followers")
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = user?.listedCount.toString())
                Text(text = "Listed")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Divider()

        LazyColumn(
            modifier = Modifier
                .weight(1f)
        ) {
            for (it in (0 until 10)) {
                item {
                    ListItem(
                        icon = {
                            Icon(asset = vectorResource(id = R.drawable.ic_adjustments_horizontal))
                        },
                        text = {
                            Text(text = "Settings")
                        }
                    )
                }
            }
        }

        Divider()
        ListItem(
            modifier = Modifier.clickable(
                onClick = {
                    scaffoldState.drawerState.close {
                        navController.navigate("settings")
                    }
                }
            ),
            icon = {
                Icon(asset = vectorResource(id = R.drawable.ic_adjustments_horizontal))
            },
            text = {
                Text(text = "Settings")
            }
        )
    }
}

@Composable
private fun DrawerUserHeader(user: UiUser?) {
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
                }
            ) {
                Icon(asset = Icons.Default.ArrowDropDown)
            }
        }
    )
}
