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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.twidere.twiderex.component.foundation.EdgePadding
import com.twidere.twiderex.component.foundation.SwipeToRefreshLayout
import com.twidere.twiderex.component.lazy.LocalLazyListController
import com.twidere.twiderex.component.lazy.ui.LazyUiStatusList
import com.twidere.twiderex.extensions.refreshOrRetry
import com.twidere.twiderex.viewmodel.timeline.TimelineScrollState
import com.twidere.twiderex.viewmodel.timeline.TimelineViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

@Composable
fun TimelineComponent(
    viewModel: TimelineViewModel,
    edgePadding: EdgePadding = EdgePadding(top = false),
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    val items = viewModel.source.collectAsLazyPagingItems()
    val loadingBetween by viewModel.loadingBetween.observeAsState(initial = listOf())
    SwipeToRefreshLayout(
        refreshingState = items.loadState.refresh is LoadState.Loading,
        onRefresh = {
            items.refreshOrRetry()
        },
    ) {
        val lastScrollState = remember {
            viewModel.restoreScrollState()
        }
        val listState = rememberLazyListState(
            initialFirstVisibleItemIndex = lastScrollState.firstVisibleItemIndex,
            initialFirstVisibleItemScrollOffset = lastScrollState.firstVisibleItemScrollOffset,
        )
        val scope = rememberCoroutineScope()
        LocalLazyListController.current.requestScrollTop = remember {
            {
                scope.launch {
                    listState.scrollToItem(0)
                }
            }
        }
        LaunchedEffect(listState) {
            snapshotFlow { listState.isScrollInProgress }
                .distinctUntilChanged()
                .filter { !it }
                .collect {
                    viewModel.saveScrollState(
                        TimelineScrollState(
                            firstVisibleItemIndex = listState.firstVisibleItemIndex,
                            firstVisibleItemScrollOffset = listState.firstVisibleItemScrollOffset,
                        )
                    )
                }
        }
        LazyUiStatusList(
            state = listState,
            items = items,
            loadingBetween = loadingBetween,
            onLoadBetweenClicked = { current, next ->
                viewModel.loadBetween(current, next)
            },
            edgePadding = edgePadding,
            contentPadding = contentPadding
        )
    }
}
