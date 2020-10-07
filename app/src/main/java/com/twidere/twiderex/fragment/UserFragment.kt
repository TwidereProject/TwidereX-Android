package com.twidere.twiderex.fragment

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.contentColor
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.TabConstants.defaultTabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.launchInComposition
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.WithConstraints
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.compose.ui.zIndex
import androidx.navigation.fragment.navArgs
import com.twidere.twiderex.annotations.IncomingComposeUpdate
import com.twidere.twiderex.component.AppBar
import com.twidere.twiderex.component.AppBarNavigationButton
import com.twidere.twiderex.component.NetworkImage
import com.twidere.twiderex.component.UserAvatar
import com.twidere.twiderex.component.user.UserGalleryItem
import com.twidere.twiderex.component.user.UserLikeItem
import com.twidere.twiderex.component.user.UserTabItem
import com.twidere.twiderex.component.user.UserTimelineItem
import com.twidere.twiderex.ui.standardPadding
import com.twidere.twiderex.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserFragment : JetFragment() {
    private val args by navArgs<UserFragmentArgs>()

    @OptIn(ExperimentalLazyDsl::class, IncomingComposeUpdate::class)
    @Composable
    @IncomingComposeUpdate
    override fun onCompose() {
        val viewModel = viewModel<UserViewModel>()
        val user by viewModel.user.observeAsState(initial = args.user)
        val relationship by viewModel.relationship.observeAsState()
        val loaded by viewModel.loaded.observeAsState(initial = false)
        val tabs = listOf(
            UserTimelineItem(),
            UserGalleryItem(),
            UserLikeItem(),
        )
        val (selectedItem, setSelectedItem) = savedInstanceState { 0 }
        launchInComposition {
            viewModel.init(args.user)
        }
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = {}) {
                    Icon(asset = Icons.Default.Reply)
                }
            }
        ) {
            Box {
                AppBar(
                    modifier = Modifier.zIndex(1f),
                    navigationIcon = {
                        AppBarNavigationButton()
                    },
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(asset = Icons.Default.Mail)
                        }
                        IconButton(onClick = {}) {
                            Icon(asset = Icons.Default.MoreVert)
                        }
                    }
                )

                val listState = rememberLazyListState()
                //TODO: background color
                //TODO: header paddings
                LazyColumn(
                    state = listState
                ) {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxWidth()
                        ) {
                            //TODO: parallax effect
                            user.profileBackgroundImage?.let {
                                NetworkImage(
                                    url = it,
                                    modifier = Modifier
                                        .aspectRatio(320f / 160f)
                                )
                            }
                            Column {
                                WithConstraints {
                                    Spacer(modifier = Modifier.height(maxWidth * 160f / 320f - 72.dp / 2))
                                }

                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    alignment = Alignment.Center
                                ) {
                                    Spacer(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(CircleShape)
                                            .clipToBounds()
                                            .background(Color.White)
                                    )
                                    UserAvatar(
                                        user = user,
                                        size = 72.dp,
                                    )
                                }
                                Spacer(modifier = Modifier.height(standardPadding * 2))
                                Row (
                                    modifier = Modifier.padding(horizontal = standardPadding * 2)
                                ) {
                                    Column(
                                        modifier = Modifier.weight(1f),
                                    ) {
                                        Text(
                                            text = user.name,
                                            style = MaterialTheme.typography.h6,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                        Text(
                                            text = "@${user.screenName}",
                                        )
                                    }
                                    relationship?.let {
                                        Column(
                                            horizontalAlignment = Alignment.End
                                        ) {
                                            Text(
                                                text = if (it.followedBy) "Following" else "Follow",
                                                style = MaterialTheme.typography.h6,
                                                color = MaterialTheme.colors.primary,
                                            )
                                            if (it.following) {
                                                Text(
                                                    text = "Follows you",
                                                    style = MaterialTheme.typography.caption,
                                                )
                                            }
                                        }
                                    } ?: run {
                                        if (!loaded) {
                                            CircularProgressIndicator()
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(standardPadding * 2))
                                ListItem(
                                    text = {
                                        Text(text = user.desc)
                                    }
                                )
                                user.website?.let {
                                    ListItem(
                                        icon = {
                                            Icon(asset = Icons.Default.Link)
                                        },
                                        text = {
                                            Text(
                                                text = it,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                            )
                                        }
                                    )
                                }
                                user.location?.let {
                                    ListItem(
                                        icon = {
                                            Icon(asset = Icons.Default.MyLocation)
                                        },
                                        text = {
                                            Text(
                                                text = it,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                            )
                                        }
                                    )
                                }
                                Spacer(modifier = Modifier.height(standardPadding * 2))
                                Row {
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ) {
                                        Text(text = user.friendsCount.toString())
                                        Text(text = "Following")
                                    }
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ) {
                                        Text(text = user.followersCount.toString())
                                        Text(text = "Followers")
                                    }
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ) {
                                        Text(text = user.listedCount.toString())
                                        Text(text = "Listed")
                                    }
                                }
                                Spacer(modifier = Modifier.height(standardPadding * 2))
                                UserTabs(
                                    items = tabs,
                                    selectedItem = selectedItem,
                                    onItemSelected = {
                                        setSelectedItem(it)
                                    },
                                )
                            }
                        }
                    }
                    item {

                    }
                }
            }
        }
    }
}

@Composable
private fun UserTabs(
    items: List<UserTabItem>,
    selectedItem: Int,
    onItemSelected: (Int) -> Unit,
) {
    TabRow(
        selectedTabIndex = selectedItem,
        backgroundColor = MaterialTheme.colors.background,
        indicator = { tabPositions ->
            TabConstants.DefaultIndicator(
                modifier = Modifier.defaultTabIndicatorOffset(tabPositions[selectedItem]),
                color = MaterialTheme.colors.primary,
            )
        }
    ) {
        for (i in 0 until items.count()) {
            Tab(
                selected = selectedItem == i,
                onClick = {
                    onItemSelected(i)
                },
                selectedContentColor = MaterialTheme.colors.primary,
                unselectedContentColor = EmphasisAmbient.current.medium.applyEmphasis(
                    contentColor()
                ),
            ) {
                Box(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(asset = items[i].icon)
                }
            }
        }
    }
}