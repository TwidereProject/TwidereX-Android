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
package com.twidere.twiderex.component

import androidx.compose.foundation.InteractionState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.navigation.compose.navigate
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.IconTabsComponent
import com.twidere.twiderex.component.foundation.NetworkImage
import com.twidere.twiderex.component.foundation.Pager
import com.twidere.twiderex.component.foundation.SwipeToRefreshLayout
import com.twidere.twiderex.component.foundation.TabScaffold
import com.twidere.twiderex.component.foundation.rememberPagerState
import com.twidere.twiderex.component.lazy.collectAsLazyPagingItems
import com.twidere.twiderex.component.lazy.itemsPaging
import com.twidere.twiderex.component.lazy.itemsPagingGridIndexed
import com.twidere.twiderex.component.navigation.LocalNavigator
import com.twidere.twiderex.component.status.StatusDivider
import com.twidere.twiderex.component.status.StatusMediaPreviewItem
import com.twidere.twiderex.component.status.TimelineStatusComponent
import com.twidere.twiderex.component.status.UserAvatar
import com.twidere.twiderex.component.status.withAvatarClip
import com.twidere.twiderex.di.assisted.assistedViewModel
import com.twidere.twiderex.extensions.withElevation
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.navigation.Route
import com.twidere.twiderex.preferences.proto.DisplayPreferences
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.LocalNavController
import com.twidere.twiderex.ui.LocalVideoPlayback
import com.twidere.twiderex.ui.standardPadding
import com.twidere.twiderex.viewmodel.user.UserFavouriteTimelineViewModel
import com.twidere.twiderex.viewmodel.user.UserMediaTimelineViewModel
import com.twidere.twiderex.viewmodel.user.UserTimelineViewModel
import com.twidere.twiderex.viewmodel.user.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun UserComponent(
    userKey: MicroBlogKey,
    initialData: UiUser? = null,
) {
    val account = LocalActiveAccount.current ?: return
    val viewModel = assistedViewModel<UserViewModel.AssistedFactory, UserViewModel>(
        account,
        userKey,
    ) {
        it.create(account, userKey)
    }
    val user by viewModel.user.observeAsState(initial = initialData)
    val tabs = listOf(
        UserTabComponent(
            painterResource(id = R.drawable.ic_float_left),
            stringResource(id = R.string.accessibility_scene_user_tab_status)
        ) {
            UserStatusTimeline(userKey = userKey)
        },
        UserTabComponent(
            painterResource(id = R.drawable.ic_photo),
            stringResource(id = R.string.accessibility_scene_user_tab_media)
        ) {
            UserMediaTimeline(userKey = userKey)
        },
    ).let {
        if (viewModel.isMe || userKey.host == MicroBlogKey.TwitterHost) {
            it + UserTabComponent(
                painterResource(id = R.drawable.ic_heart),
                stringResource(id = R.string.accessibility_scene_user_tab_favourite)
            ) {
                UserFavouriteTimeline(userKey = userKey)
            }
        } else {
            it
        }
    }
    val refreshing by viewModel.refreshing.observeAsState(initial = false)
    SwipeToRefreshLayout(
        refreshingState = refreshing,
        onRefresh = {
            viewModel.refresh()
        },
    ) {
        TabScaffold(
            header = {
                user?.let {
                    UserInfo(user = it, viewModel = viewModel)
                }
            },
            content = {
                val state = rememberPagerState(maxPage = tabs.lastIndex)
                Column {
                    val scope = rememberCoroutineScope()
                    IconTabsComponent(
                        items = tabs.map { it.icon to it.title },
                        selectedItem = state.currentPage,
                        onItemSelected = {
                            scope.launch {
                                state.selectPage { state.currentPage = it }
                            }
                        },
                    )
                    Pager(
                        modifier = Modifier.weight(1f),
                        state = state,
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.TopCenter,
                        ) {
                            tabs[page].compose.invoke()
                        }
                    }
                }
            }
        )
    }
}

data class UserTabComponent(
    val icon: Painter,
    val title: String,
    val compose: @Composable () -> Unit,
)

@Composable
fun UserStatusTimeline(
    userKey: MicroBlogKey,
) {
    val account = LocalActiveAccount.current ?: return
    val timelineViewModel =
        assistedViewModel<UserTimelineViewModel.AssistedFactory, UserTimelineViewModel>(
            account,
            userKey,
        ) {
            it.create(account, userKey = userKey)
        }
    val timelineSource = timelineViewModel.source.collectAsLazyPagingItems()
    // FIXME: 2021/2/20 Recover the scroll position require visiting the loadState once, have no idea why
    @Suppress("UNUSED_VARIABLE")
    timelineSource.loadState
    if (timelineSource.itemCount > 0) {
        LazyColumn {
            itemsPaging(
                timelineSource,
                key = { timelineSource[it]!!.statusKey.hashCode() },
            ) { item ->
                item?.let {
                    Column {
                        TimelineStatusComponent(it)
                        StatusDivider()
                    }
                }
            }
        }
    }
}

@Composable
fun UserMediaTimeline(
    userKey: MicroBlogKey,
) {
    val account = LocalActiveAccount.current ?: return
    val mediaViewModel =
        assistedViewModel<UserMediaTimelineViewModel.AssistedFactory, UserMediaTimelineViewModel>(
            account,
            userKey,
        ) {
            it.create(account, userKey = userKey)
        }
    val mediaSource = mediaViewModel.source.collectAsLazyPagingItems()
    // FIXME: 2021/2/20 Recover the scroll position require visiting the loadState once, have no idea why
    @Suppress("UNUSED_VARIABLE")
    mediaSource.loadState
    if (mediaSource.itemCount > 0) {
        LazyColumn {
            item {
                Box(modifier = Modifier.height(standardPadding))
            }
            itemsPagingGridIndexed(
                mediaSource,
                rowSize = 2,
                spacing = standardPadding,
                padding = standardPadding
            ) { index, pair ->
                pair?.let { item ->
                    val navigator = LocalNavigator.current
                    Providers(
                        LocalVideoPlayback provides DisplayPreferences.AutoPlayback.Off,
                    ) {
                        StatusMediaPreviewItem(
                            item.first,
                            modifier = Modifier
                                .aspectRatio(1F)
                                .clip(
                                    MaterialTheme.shapes.medium
                                ),
                            onClick = {
                                navigator.media(item.second.statusKey, index)
                            }
                        )
                    }
                }
            }
            item {
                Box(modifier = Modifier.height(standardPadding))
            }
        }
    }
}

@Composable
fun UserFavouriteTimeline(
    userKey: MicroBlogKey,
) {
    val account = LocalActiveAccount.current ?: return
    val timelineViewModel =
        assistedViewModel<UserFavouriteTimelineViewModel.AssistedFactory, UserFavouriteTimelineViewModel>(
            account,
            userKey,
        ) {
            it.create(account, userKey = userKey)
        }
    val timelineSource = timelineViewModel.source.collectAsLazyPagingItems()
    // FIXME: 2021/2/20 Recover the scroll position require visiting the loadState once, have no idea why
    @Suppress("UNUSED_VARIABLE")
    timelineSource.loadState
    if (timelineSource.itemCount > 0) {
        LazyColumn {
            itemsPaging(
                timelineSource,
                key = { timelineSource[it]!!.statusKey.hashCode() },
            ) { item ->
                item?.let {
                    Column {
                        TimelineStatusComponent(it)
                        StatusDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun UserInfo(user: UiUser, viewModel: UserViewModel) {
    val relationship by viewModel.relationship.observeAsState(initial = null)
    val loadingRelationship by viewModel.loadingRelationship.observeAsState(initial = false)
    val maxBannerSize = 200.dp
    val navController = LocalNavController.current
    Box(
        modifier = Modifier
            .background(MaterialTheme.colors.surface.withElevation())
    ) {
        // TODO: parallax effect
        user.profileBackgroundImage?.let {
            Box(
                modifier = Modifier
                    .heightIn(max = maxBannerSize)
                    .clickable(
                        onClick = {
                            navController.navigate(Route.Media.Raw(it))
                        },
                        indication = null,
                        interactionState = remember { InteractionState() }
                    )
            ) {
                NetworkImage(
                    data = it,
                )
            }
        }
        Column {
            BoxWithConstraints {
                Spacer(
                    modifier = Modifier.height(
                        min(
                            maxWidth * 160f / 320f - 72.dp / 2,
                            maxBannerSize - 72.dp / 2
                        )
                    )
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Spacer(
                    modifier = Modifier
                        .size(80.dp)
                        .withAvatarClip()
                        .clipToBounds()
                        .background(MaterialTheme.colors.surface.withElevation())
                )
                UserAvatar(
                    user = user,
                    size = 72.dp
                ) {
                    if (user.profileImage is String) {
                        navController.navigate(Route.Media.Raw(user.profileImage))
                    }
                }
            }
            Spacer(modifier = Modifier.height(standardPadding))
            Row(
                modifier = Modifier
                    .padding(horizontal = standardPadding * 2)
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
                if (viewModel.isMe) {
                    // TODO: edit button
//                    Text(
//                        style = MaterialTheme.typography.h6,
//                        color = MaterialTheme.colors.primary,
//                        text = "Edit",
//                    )
                } else {
                    relationship?.takeIf { !loadingRelationship }?.let {
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            TextButton(
                                onClick = {
                                    if (it.followedBy) {
                                        viewModel.unfollow()
                                    } else {
                                        viewModel.follow()
                                    }
                                }
                            ) {
                                Text(
                                    text = if (it.followedBy) {
                                        stringResource(id = R.string.common_controls_friendship_actions_unfollow)
                                    } else {
                                        stringResource(id = R.string.common_controls_friendship_actions_follow)
                                    },
                                    style = MaterialTheme.typography.h6,
                                    color = MaterialTheme.colors.primary,
                                )
                            }
                            if (it.following) {
                                Text(
                                    text = stringResource(id = R.string.common_controls_friendship_follows_you),
                                    style = MaterialTheme.typography.caption,
                                )
                            }
                        }
                    } ?: run {
                        CircularProgressIndicator()
                    }
                }
            }
            Spacer(modifier = Modifier.height(standardPadding))
            Text(
                modifier = Modifier.padding(horizontal = standardPadding * 2),
                text = user.desc,
            )
            user.website?.let {
                val navigator = LocalNavigator.current
                Column(
                    modifier = Modifier
                        .clickable(
                            onClick = {
                                navigator.openLink(it)
                            }
                        )
                        .padding(horizontal = standardPadding * 2)
                        .fillMaxWidth(),
                ) {
                    Spacer(modifier = Modifier.height(standardPadding))
                    Row {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_globe),
                            contentDescription = stringResource(
                                id = R.string.accessibility_scene_user_website
                            )
                        )
                        Spacer(modifier = Modifier.width(standardPadding))
                        Text(
                            text = it,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colors.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(standardPadding))
                }
            }
            user.location?.takeIf { it.isNotEmpty() }?.let {
                Spacer(modifier = Modifier.height(standardPadding))
                Row(
                    modifier = Modifier
                        .padding(horizontal = standardPadding * 2),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_map_pin),
                        contentDescription = stringResource(
                            id = R.string.accessibility_scene_user_location
                        )
                    )
                    Spacer(modifier = Modifier.width(standardPadding))
                    Text(
                        text = it,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(modifier = Modifier.height(standardPadding))
            }
            Spacer(modifier = Modifier.height(standardPadding))
            Row {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            if (user.userKey.host == MicroBlogKey.TwitterHost) {
                                navController.navigate(Route.Twitter.User.Following(user.userKey))
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = user.friendsCount.toString())
                    Text(text = stringResource(id = R.string.common_controls_profile_dashboard_following))
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            if (user.userKey.host == MicroBlogKey.TwitterHost) {
                                navController.navigate(Route.Twitter.User.Followers(user.userKey))
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = user.followersCount.toString())
                    Text(text = stringResource(id = R.string.common_controls_profile_dashboard_followers))
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = user.listedCount.toString())
                    Text(text = stringResource(id = R.string.common_controls_profile_dashboard_listed))
                }
            }
            Spacer(modifier = Modifier.height(standardPadding))
        }
    }
}
