package com.twidere.twiderex.fragment

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import com.twidere.twiderex.component.NetworkImage
import com.twidere.twiderex.component.home.*
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
                    unselectedContentColor = EmphasisAmbient.current.medium.applyEmphasis(
                        contentColor()
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
                com.twidere.twiderex.component.AppBar(
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
                    }
                )
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
                paddingBottom = it.bottom,
                paddingTop = it.top,
                paddingStart = it.start,
                paddingEnd = it.end,
            ) {
                Crossfade(current = selectedItem) {
                    menus[it].onCompose()
                }
            }
        }
    }
}

@Composable
private fun HomeDrawer() {
    val viewModel = viewModel<ActiveAccountViewModel>()
    Column {
        Spacer(modifier = Modifier.height(16.dp))

        ListItem(
            icon = {
                NetworkImage(
                    url = viewModel.account.user.profileImage,
                    modifier = Modifier
                        .clip(CircleShape)
                        .width(profileImageSize)
                        .height(profileImageSize)
                )
            },
            text = {
                Text(
                    text = viewModel.account.user.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            secondaryText = {
                Text(
                    text = "@${viewModel.account.user.screenName}",
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
                Text(text = viewModel.account.user.friendsCount.toString())
                Text(text = "Following")
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = viewModel.account.user.followersCount.toString())
                Text(text = "Followers")
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = viewModel.account.user.listedCount.toString())
                Text(text = "Listed")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Divider()

        ScrollableColumn(
            modifier = Modifier
                .weight(1f)
        ) {
            for (it in (0 until 10)) {
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

        Divider()

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