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

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumnForIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.onDispose
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.twidere.twiderex.annotations.IncomingComposeUpdate
import com.twidere.twiderex.component.foundation.LoadingProgress
import com.twidere.twiderex.component.foundation.SwipeToRefreshLayout
import com.twidere.twiderex.component.status.StatusDivider
import com.twidere.twiderex.component.status.TimelineStatusComponent
import com.twidere.twiderex.viewmodel.twitter.timeline.TimelineViewModel
import kotlinx.coroutines.launch

@OptIn(IncomingComposeUpdate::class)
@Composable
fun TimelineComponent(viewModel: TimelineViewModel) {
    val items by viewModel.source.observeAsState(initial = listOf())
    val loadingBetween by viewModel.loadingBetween.observeAsState(initial = listOf())
    val loadingMore by viewModel.loadingMore.observeAsState(initial = false)
    val refreshing by viewModel.refreshing.observeAsState(initial = false)
    val scope = rememberCoroutineScope()
    SwipeToRefreshLayout(
        refreshingState = refreshing,
        onRefresh = {
            scope.launch {
                viewModel.refresh()
            }
        },
    ) {
        if (items.any()) {
            val listState =
                rememberLazyListState(initialFirstVisibleItemIndex = viewModel.restoreScrollState())
            onDispose {
                viewModel.saveScrollState(listState.firstVisibleItemIndex)
            }
            LazyColumnForIndexed(
                items = items,
                state = listState,
            ) { index, item ->
                Column {
                    if (!loadingMore && index == items.lastIndex) {
                        scope.launch {
                            viewModel.loadMore()
                        }
                    }
                    TimelineStatusComponent(
                        item,
                    )
                    if (index != items.lastIndex) {
                        when {
                            loadingBetween.contains(item.statusId) -> {
                                LoadingProgress()
                            }
                            item.isGap -> {
                                Divider()
                                TextButton(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    onClick = {
                                        scope.launch {
                                            viewModel.loadBetween(
                                                item.statusId,
                                                items[index + 1].statusId,
                                            )
                                        }
                                    },
                                ) {
                                    Text("Load more")
                                }
                                Divider()
                            }
                            else -> {
                                StatusDivider()
                            }
                        }
                    }
                    if (loadingMore && index == items.lastIndex) {
                        StatusDivider()
                        LoadingProgress()
                    }
                }
            }
        }
    }
}
