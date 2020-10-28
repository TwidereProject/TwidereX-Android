/*
 *  TwidereX
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of TwidereX.
 * 
 *  TwidereX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  TwidereX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with TwidereX. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.fragment

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.AmbientContentColor
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AmbientEmphasisLevels
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import com.twidere.twiderex.R
import com.twidere.twiderex.component.AppBar
import com.twidere.twiderex.component.NetworkImage
import com.twidere.twiderex.component.TopAppBarElevation
import com.twidere.twiderex.component.home.HomeNavigationItem
import com.twidere.twiderex.component.home.HomeTimelineItem
import com.twidere.twiderex.component.home.MeItem
import com.twidere.twiderex.component.home.MentionItem
import com.twidere.twiderex.component.home.SearchItem
import com.twidere.twiderex.extensions.NavControllerAmbient
import com.twidere.twiderex.extensions.withElevation
import com.twidere.twiderex.ui.profileImageSize
import com.twidere.twiderex.viewmodel.ActiveAccountViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : JetFragment() {

    @Composable
    fun BottomNavigation(
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
                    label = { Text(item.name) },
                    selected = selectedItem == index,
                    onClick = { onItemSelected.invoke(index) }
                )
            }
        }
    }

    @Composable
    override fun onCompose() {
        val (selectedItem, setSelectedItem) = savedInstanceState { 0 }
        val menus = listOf(
            HomeTimelineItem(),
            MentionItem(),
            SearchItem(),
            MeItem(),
        )
        val scaffoldState = rememberScaffoldState()
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
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
            },
            bottomBar = {
                BottomNavigation(menus, selectedItem) {
                    setSelectedItem(it)
                }
            },
            drawerContent = {
                HomeDrawer()
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

@OptIn(ExperimentalLazyDsl::class)
@Composable
private fun HomeDrawer() {
    val viewModel = viewModel<ActiveAccountViewModel>()
    val account by viewModel.account.observeAsState()
    val navController = NavControllerAmbient.current

    Column {
        Spacer(modifier = Modifier.height(16.dp))

        ListItem(
            icon = {
                account?.let {
                    NetworkImage(
                        url = it.user.profileImage,
                        modifier = Modifier
                            .clip(CircleShape)
                            .width(profileImageSize)
                            .height(profileImageSize)
                    )
                }
            },
            text = {
                Text(
                    text = account?.user?.name ?: "",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            secondaryText = {
                Text(
                    text = "@${account?.user?.screenName}",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            trailing = {
                IconButton(onClick = {}) {
                    Icon(asset = Icons.Default.ArrowDropDown)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = account?.user?.friendsCount.toString())
                Text(text = "Following")
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = account?.user?.followersCount.toString())
                Text(text = "Followers")
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = account?.user?.listedCount.toString())
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
                            Icon(asset = Icons.Default.Settings)
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
            modifier = Modifier.clickable(onClick = {
                navController.navigate(R.id.settings_fragment)
            }),
            icon = {
                Icon(asset = Icons.Default.Settings)
            },
            text = {
                Text(text = "Settings")
            }
        )
    }
}
