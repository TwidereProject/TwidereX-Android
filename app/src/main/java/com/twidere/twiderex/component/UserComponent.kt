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
package com.twidere.twiderex.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.WithConstraints
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.zIndex
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.twidere.twiderex.R
import com.twidere.twiderex.annotations.IncomingComposeUpdate
import com.twidere.twiderex.component.foundation.IconTabsComponent
import com.twidere.twiderex.component.foundation.LoadingProgress
import com.twidere.twiderex.component.foundation.NetworkImage
import com.twidere.twiderex.component.foundation.SwipeToRefreshLayout
import com.twidere.twiderex.component.foundation.TopAppBarElevation
import com.twidere.twiderex.component.lazy.itemsPaging
import com.twidere.twiderex.component.navigation.AmbientNavigator
import com.twidere.twiderex.component.status.StatusDivider
import com.twidere.twiderex.component.status.TimelineStatusComponent
import com.twidere.twiderex.component.status.UserAvatar
import com.twidere.twiderex.component.status.withAvatarClip
import com.twidere.twiderex.di.assisted.assistedViewModel
import com.twidere.twiderex.extensions.withElevation
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.ui.AmbientActiveAccount
import com.twidere.twiderex.ui.standardPadding
import com.twidere.twiderex.viewmodel.user.UserFavouriteTimelineViewModel
import com.twidere.twiderex.viewmodel.user.UserMediaTimelineViewModel
import com.twidere.twiderex.viewmodel.user.UserTimelineViewModel
import com.twidere.twiderex.viewmodel.user.UserViewModel

@IncomingComposeUpdate
@Composable
fun UserComponent(
    screenName: String,
    initialData: UiUser? = null,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    val account = AmbientActiveAccount.current ?: return
    val viewModel = assistedViewModel<UserViewModel.AssistedFactory, UserViewModel>(
        account,
        screenName,
    ) {
        it.create(account, screenName)
    }
    val timelineViewModel =
        assistedViewModel<UserTimelineViewModel.AssistedFactory, UserTimelineViewModel>(
            account,
            screenName,
        ) {
            it.create(account, screenName)
        }
    val mediaViewModel =
        assistedViewModel<UserMediaTimelineViewModel.AssistedFactory, UserMediaTimelineViewModel>(
            account,
            screenName
        ) {
            it.create(account, screenName)
        }
    val favouriteViewModel =
        assistedViewModel<UserFavouriteTimelineViewModel.AssistedFactory, UserFavouriteTimelineViewModel>(
            account,
            screenName,
        ) {
            it.create(account, screenName)
        }

    val timelineSource = timelineViewModel.source.collectAsLazyPagingItems()
    val mediaSource = mediaViewModel.source.collectAsLazyPagingItems()
    val favouriteSource = favouriteViewModel.source.collectAsLazyPagingItems()
    val refreshing by viewModel.refreshing.observeAsState(initial = false)
    val viewModelUser by viewModel.user.observeAsState(initial = initialData)
    val tabs = listOf(
        vectorResource(id = R.drawable.ic_float_left),
        vectorResource(id = R.drawable.ic_photo),
        vectorResource(id = R.drawable.ic_heart),
    )
    val (selectedItem, setSelectedItem) = savedInstanceState { 0 }

    viewModelUser?.let { user ->
        Box {
            val shouldStickyHeaderShown = lazyListState.firstVisibleItemIndex >= 1
            Surface(
                modifier = Modifier.zIndex(1f),
                elevation = if (shouldStickyHeaderShown) TopAppBarElevation else 0.dp
            ) {
                if (shouldStickyHeaderShown) {
                    IconTabsComponent(
                        items = tabs,
                        selectedItem = selectedItem,
                        onItemSelected = {
                            setSelectedItem(it)
                        },
                    )
                }
            }

            SwipeToRefreshLayout(
                refreshingState = refreshing ||
                        selectedItem == 0 && timelineSource.loadState.refresh == LoadState.Loading ||
                        selectedItem == 1 && mediaSource.loadState.refresh == LoadState.Loading ||
                        selectedItem == 2 && favouriteSource.loadState.refresh == LoadState.Loading,
                onRefresh = {
                    viewModel.refresh()
                    when (selectedItem) {
                        0 -> timelineSource.refresh()
                        1 -> mediaSource.refresh()
                        2 -> favouriteSource.refresh()
                    }
                },
            ) {
                // TODO: not work if the user not posting anything
                if (
                    selectedItem == 0 && timelineSource.itemCount > 0 ||
                    selectedItem == 1 && mediaSource.itemCount > 0 ||
                    selectedItem == 2 && favouriteSource.itemCount > 0
                ) {
                    LazyColumn(
                        state = lazyListState,
                    ) {
                        item {
                            UserInfo(user = user, viewModel = viewModel)
                        }

                        item {
                            IconTabsComponent(
                                items = tabs,
                                selectedItem = selectedItem,
                                onItemSelected = {
                                    setSelectedItem(it)
                                },
                            )
                        }

                        when (selectedItem) {
                            0 -> {
                                itemsPaging(timelineSource) { item ->
                                    item?.let {
                                        Column {
                                            TimelineStatusComponent(it)
                                            StatusDivider()
                                        }
                                    }
                                }
                            }
                            1 -> {
                                itemsPaging(mediaSource) { item ->
                                    item?.let {
                                        Column {
                                            TimelineStatusComponent(it)
                                            StatusDivider()
                                        }
                                    }
                                }
                            }
                            2 -> {
                                itemsPaging(favouriteSource) { item ->
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
                }
            }
        }
    } ?: run {
        Column(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoadingProgress()
        }
    }
}

@Composable
private fun UserInfo(user: UiUser, viewModel: UserViewModel) {
    val isMe by viewModel.isMe.observeAsState(initial = false)
    val relationship by viewModel.relationship.observeAsState(initial = null)
    val loadingRelationship by viewModel.loadingRelationship.observeAsState(initial = false)
    val maxBannerSize = 200.dp
    Box(
        modifier = Modifier
            .background(MaterialTheme.colors.surface.withElevation())
    ) {
        // TODO: parallax effect
        user.profileBackgroundImage?.let {
            Box(
                modifier = Modifier
                    .heightIn(max = maxBannerSize)
            ) {
                NetworkImage(
                    url = it,
                )
            }
        }
        Column {
            WithConstraints {
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
                alignment = Alignment.Center
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
                    size = 72.dp,
                )
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
                if (isMe) {
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
                                        stringResource(id = R.string.action_unfollow)
                                    } else {
                                        stringResource(id = R.string.action_follow)
                                    },
                                    style = MaterialTheme.typography.h6,
                                    color = MaterialTheme.colors.primary,
                                )
                            }
                            if (it.following) {
                                Text(
                                    text = stringResource(id = R.string.following_you),
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
                val navigator = AmbientNavigator.current
                Column(
                    modifier = Modifier
                        .clickable(onClick = {
                            navigator.openLink(it)
                        })
                        .padding(horizontal = standardPadding * 2)
                        .fillMaxWidth(),
                ) {
                    Spacer(modifier = Modifier.height(standardPadding))
                    Row {
                        Icon(asset = vectorResource(id = R.drawable.ic_globe))
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
                    Icon(asset = vectorResource(id = R.drawable.ic_map_pin))
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
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = user.friendsCount.toString())
                    Text(text = stringResource(id = R.string.title_following))
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = user.followersCount.toString())
                    Text(text = stringResource(id = R.string.title_followers))
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = user.listedCount.toString())
                    Text(text = stringResource(id = R.string.title_listed))
                }
            }
            Spacer(modifier = Modifier.height(standardPadding))
        }
    }
}
