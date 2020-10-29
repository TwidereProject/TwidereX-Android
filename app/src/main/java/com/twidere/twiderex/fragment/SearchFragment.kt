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

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ProvideTextStyle
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumnForIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AmbientEmphasisLevels
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideEmphasis
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedTask
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.onActive
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.navigation.fragment.navArgs
import com.twidere.twiderex.R
import com.twidere.twiderex.annotations.IncomingComposeUpdate
import com.twidere.twiderex.component.AppBar
import com.twidere.twiderex.component.AppBarNavigationButton
import com.twidere.twiderex.component.LoadingProgress
import com.twidere.twiderex.component.StatusDivider
import com.twidere.twiderex.component.StatusMediaPreviewItem
import com.twidere.twiderex.component.SwipeToRefreshLayout
import com.twidere.twiderex.component.TabsComponent
import com.twidere.twiderex.component.TextInput
import com.twidere.twiderex.component.TimelineStatusComponent
import com.twidere.twiderex.component.TopAppBarElevation
import com.twidere.twiderex.component.UserAvatar
import com.twidere.twiderex.component.lazy.LazyGridForIndexed
import com.twidere.twiderex.extensions.AmbientNavController
import com.twidere.twiderex.ui.standardPadding
import com.twidere.twiderex.viewmodel.twitter.search.TwitterSearchMediasViewModel
import com.twidere.twiderex.viewmodel.twitter.search.TwitterSearchTweetsViewModel
import com.twidere.twiderex.viewmodel.twitter.search.TwitterSearchUserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : JetFragment() {
    private val args by navArgs<SearchFragmentArgs>()

    @OptIn(ExperimentalFoundationApi::class, ExperimentalFocus::class)
    @Composable
    override fun onCompose() {
        val tweetsViewModel = viewModel<TwitterSearchTweetsViewModel>()
        val mediasViewModel = viewModel<TwitterSearchMediasViewModel>()
        val usersViewModel = viewModel<TwitterSearchUserViewModel>()
        val (text, setText) = remember { mutableStateOf(args.keyword) }
        var selectedTab by savedInstanceState { 0 }

        onActive {
            tweetsViewModel.reset(args.keyword)
            mediasViewModel.reset(args.keyword)
            usersViewModel.reset(args.keyword)
        }

        Scaffold {
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
                                        TextInput(
                                            modifier = Modifier
                                                .align(Alignment.CenterVertically)
                                                .weight(1F),
                                            value = text,
                                            onValueChange = {
                                                setText(it)
                                            },
                                            placeholder = {
                                                Text(text = "Tap to search...")
                                            },
                                            onImeActionPerformed = { _, _ ->
                                                usersViewModel.reset(text)
                                                tweetsViewModel.reset(text)
                                                mediasViewModel.reset(text)
                                            },
                                            imeAction = ImeAction.Search,
                                            alignment = Alignment.CenterStart,
                                        )
                                        IconButton(onClick = {}) {
                                            Icon(asset = Icons.Default.Save)
                                        }
                                    }
                                }
                            }
                        )
                        TabsComponent(
                            items = listOf(
                                Icons.Default.List,
                                Icons.Default.Image,
                                Icons.Default.AccountBox,
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
                        0 -> SearchTweetsContent()
                        1 -> SearchMediasContent()
                        2 -> SearchUsersContent()
                    }
                }
            }
        }
    }
}

@OptIn(IncomingComposeUpdate::class)
@Composable
private fun SearchTweetsContent() {
    val viewModel = viewModel<TwitterSearchTweetsViewModel>()
    val refreshing by viewModel.refreshing.observeAsState(initial = false)
    val loadingMore by viewModel.loadingMore.observeAsState(initial = false)
    val items by viewModel.source.observeAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    LaunchedTask(items) {
        if (items.isEmpty()) {
            viewModel.refresh()
        }
    }
    SwipeToRefreshLayout(
        refreshingState = refreshing,
        onRefresh = {
            scope.launch {
                viewModel.refresh()
            }
        }
    ) {
        LazyColumnForIndexed(items = items) { index, item ->
            Column {
                TimelineStatusComponent(
                    item,
                )
                if (loadingMore && index == items.lastIndex) {
                    StatusDivider()
                    LoadingProgress()
                }
            }
            if (index == items.lastIndex && !loadingMore) {
                scope.launch {
                    viewModel.loadMore()
                }
            }
        }
    }
}

@OptIn(IncomingComposeUpdate::class)
@Composable
private fun SearchMediasContent() {
    val viewModel = viewModel<TwitterSearchMediasViewModel>()
    val refreshing by viewModel.refreshing.observeAsState(initial = false)
    val loadingMore by viewModel.loadingMore.observeAsState(initial = false)
    val source by viewModel.source.observeAsState(initial = emptyList())
    val items = source.filter { it.hasMedia }.flatMap { it.media.map { media -> media to it } }
    val scope = rememberCoroutineScope()
    LaunchedTask(items) {
        if (items.isEmpty()) {
            viewModel.refresh()
        }
    }

    SwipeToRefreshLayout(
        refreshingState = refreshing,
        onRefresh = {
            scope.launch {
                viewModel.refresh()
            }
        }
    ) {
        LazyGridForIndexed(
            contentPadding = PaddingValues(top = standardPadding * 2),
            data = items,
            rowSize = 2,
            spacing = standardPadding * 2,
            padding = standardPadding * 2,
        ) { index, item ->
            val navController = AmbientNavController.current
            if (!loadingMore && index == items.lastIndex) {
                scope.launch {
                    viewModel.loadMore()
                }
            }
            StatusMediaPreviewItem(
                item.first,
                modifier = Modifier
                    .aspectRatio(1F)
                    .clip(
                        RoundedCornerShape(8.dp)
                    ),
                onClick = {
                    navController.navigate(
                        R.id.media_fragment,
                        MediaFragmentArgs(
                            item.second,
                            item.second.media.indexOf(item.first)
                        ).toBundle()
                    )
                }
            )
        }
    }
}

@OptIn(IncomingComposeUpdate::class)
@Composable
private fun SearchUsersContent() {
    val viewModel = viewModel<TwitterSearchUserViewModel>()
    val refreshing by viewModel.refreshing.observeAsState(initial = false)
    val loadingMore by viewModel.loadingMore.observeAsState(initial = false)
    val items by viewModel.source.observeAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    LaunchedTask(items) {
        if (items.isEmpty()) {
            viewModel.refresh()
        }
    }
    SwipeToRefreshLayout(
        refreshingState = refreshing,
        onRefresh = {
            scope.launch {
                viewModel.refresh()
            }
        }
    ) {
        LazyColumnForIndexed(items = items) { index, item ->
            ListItem(
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
                        ProvideEmphasis(emphasis = AmbientEmphasisLevels.current.medium) {
                            Text(
                                text = "@${item.screenName}",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                },
                secondaryText = {
                    Text(text = item.desc, maxLines = 1)
                },
                trailing = {
                    IconButton(onClick = {}) {
                        Icon(asset = Icons.Default.MoreVert)
                    }
                }
            )
            if (index == items.lastIndex && !loadingMore) {
                scope.launch {
                    viewModel.loadMore()
                }
            }
        }
    }
}
