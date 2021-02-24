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
package com.twidere.twiderex.scenes.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.foundation.SwipeToRefreshLayout
import com.twidere.twiderex.component.foundation.TextTabsComponent
import com.twidere.twiderex.component.foundation.TopAppBarElevation
import com.twidere.twiderex.component.lazy.collectAsLazyPagingItems
import com.twidere.twiderex.component.lazy.items
import com.twidere.twiderex.component.lazy.itemsPagingGridIndexed
import com.twidere.twiderex.component.lazy.loadState
import com.twidere.twiderex.component.lazy.statuses
import com.twidere.twiderex.component.navigation.LocalNavigator
import com.twidere.twiderex.component.status.StatusDivider
import com.twidere.twiderex.component.status.StatusMediaPreviewItem
import com.twidere.twiderex.component.status.TimelineStatusComponent
import com.twidere.twiderex.component.status.UserAvatar
import com.twidere.twiderex.di.assisted.assistedViewModel
import com.twidere.twiderex.extensions.refreshOrRetry
import com.twidere.twiderex.extensions.viewModel
import com.twidere.twiderex.preferences.proto.DisplayPreferences
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.LocalVideoPlayback
import com.twidere.twiderex.ui.TwidereXTheme
import com.twidere.twiderex.ui.standardPadding
import com.twidere.twiderex.viewmodel.twitter.search.TwitterSearchMediaViewModel
import com.twidere.twiderex.viewmodel.twitter.search.TwitterSearchTweetsViewModel
import com.twidere.twiderex.viewmodel.twitter.search.TwitterSearchUserViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchScene(keyword: String) {
    val account = LocalActiveAccount.current ?: return
    val tweetsViewModel =
        assistedViewModel<TwitterSearchTweetsViewModel.AssistedFactory, TwitterSearchTweetsViewModel> {
            it.create(account, keyword)
        }
    val mediaViewModel =
        assistedViewModel<TwitterSearchMediaViewModel.AssistedFactory, TwitterSearchMediaViewModel> {
            it.create(account, keyword)
        }
    val usersViewModel = viewModel {
        TwitterSearchUserViewModel(account, keyword)
    }
    var selectedTab by rememberSaveable(
        // FIXME: 2021/2/18 Workaround for https://issuetracker.google.com/issues/180513115
        saver = Saver(
            save = {
                it.value
            },
            restore = {
                mutableStateOf(it)
            },
        )
    ) { mutableStateOf(0) }
    val navigator = LocalNavigator.current

    TwidereXTheme {
        InAppNotificationScaffold {
            Column {
                Surface(
                    elevation = TopAppBarElevation,
                ) {
                    Column {
                        AppBar(
                            navigationIcon = {
                                AppBarNavigationButton()
                            },
                            elevation = 0.dp,
                            title = {
                                ProvideTextStyle(value = MaterialTheme.typography.body1) {
                                    Row {
                                        Text(
                                            modifier = Modifier
                                                .clickable(
                                                    onClick = {
                                                        navigator.searchInput(keyword)
                                                    },
                                                    indication = null,
                                                    interactionSource = remember { MutableInteractionSource() }
                                                )
                                                .align(Alignment.CenterVertically)
                                                .weight(1F),
                                            text = keyword,
                                            maxLines = 1,
                                            textAlign = TextAlign.Start,
                                        )
                                        IconButton(
                                            onClick = {
                                            }
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_device_floppy),
                                                contentDescription = stringResource(
                                                    id = R.string.accessibility_scene_search_save
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        )
                        TextTabsComponent(
                            items = listOf(
                                stringResource(id = R.string.scene_search_tabs_tweets),
                                stringResource(id = R.string.scene_search_tabs_media),
                                stringResource(id = R.string.scene_search_tabs_users),
                            ),
                            selectedItem = selectedTab,
                            onItemSelected = {
                                selectedTab = it
                            },
                        )
                    }
                }
                Box(
                    modifier = Modifier.weight(1F),
                ) {
                    when (selectedTab) {
                        0 -> SearchTweetsContent(tweetsViewModel)
                        1 -> SearchMediasContent(mediaViewModel)
                        2 -> SearchUsersContent(usersViewModel)
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchTweetsContent(viewModel: TwitterSearchTweetsViewModel) {
    val source = viewModel.source.collectAsLazyPagingItems()
    SwipeToRefreshLayout(
        refreshingState = source.loadState.refresh is LoadState.Loading,
        onRefresh = {
            source.refreshOrRetry()
        }
    ) {
        if (source.itemCount > 0) {
            LazyColumn {
                statuses(source) { item ->
                    item?.let {
                        TimelineStatusComponent(
                            it,
                        )
                        StatusDivider()
                    }
                }
                loadState(source.loadState.append) {
                    source.retry()
                }
            }
        }
    }
}

@Composable
private fun SearchMediasContent(viewModel: TwitterSearchMediaViewModel) {
    val source = viewModel.source.collectAsLazyPagingItems()
    CompositionLocalProvider(
        LocalVideoPlayback provides DisplayPreferences.AutoPlayback.Off
    ) {
        SwipeToRefreshLayout(
            refreshingState = source.loadState.refresh is LoadState.Loading,
            onRefresh = {
                source.refreshOrRetry()
            }
        ) {
            if (source.itemCount > 0) {
                LazyColumn {
                    item {
                        Box(modifier = Modifier.height(standardPadding))
                    }
                    itemsPagingGridIndexed(
                        source,
                        rowSize = 2,
                        spacing = standardPadding,
                        padding = standardPadding
                    ) { index, pair ->
                        pair?.let { item ->
                            val navigator = LocalNavigator.current
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
                    item {
                        Box(modifier = Modifier.height(standardPadding))
                    }
                    loadState(source.loadState.append) {
                        source.retry()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SearchUsersContent(viewModel: TwitterSearchUserViewModel) {
    val source = viewModel.source.collectAsLazyPagingItems()
    val navigator = LocalNavigator.current
    SwipeToRefreshLayout(
        refreshingState = source.loadState.refresh is LoadState.Loading,
        onRefresh = {
            source.refreshOrRetry()
        }
    ) {
        if (source.itemCount > 0) {
            LazyColumn {
                items(source) { item ->
                    item?.let {
                        ListItem(
                            modifier = Modifier.clickable(
                                onClick = {
                                    navigator.user(item)
                                }
                            ),
                            icon = {
                                UserAvatar(user = item)
                            },
                            text = {
                                Row {
                                    Text(
                                        text = item.name,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colors.primary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    CompositionLocalProvider(
                                        LocalContentAlpha provides ContentAlpha.medium
                                    ) {
                                        Text(
                                            text = "@${item.screenName}",
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }
                                }
                            },
                            secondaryText = {
                                Text(text = item.rawDesc, maxLines = 1)
                            },
                        )
                    }
                }
                loadState(source.loadState.append) {
                    source.retry()
                }
            }
        }
    }
}
