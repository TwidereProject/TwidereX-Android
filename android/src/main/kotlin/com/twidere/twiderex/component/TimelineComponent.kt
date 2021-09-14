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
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.twidere.twiderex.component.foundation.SwipeToRefreshLayout
import com.twidere.twiderex.component.lazy.LazyListController
import com.twidere.twiderex.component.lazy.ui.LazyUiStatusList
import com.twidere.twiderex.extensions.observeAsState
import com.twidere.twiderex.extensions.refreshOrRetry
import com.twidere.twiderex.viewmodel.timeline.TimelineViewModel

@Composable
fun TimelineComponent(
    viewModel: TimelineViewModel,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    lazyListController: LazyListController? = null,
) {
    val items = viewModel.source.collectAsLazyPagingItems()
    val loadingBetween by viewModel.loadingBetween.observeAsState(initial = listOf())
    SwipeToRefreshLayout(
        refreshingState = items.loadState.refresh is LoadState.Loading,
        onRefresh = {
            items.refreshOrRetry()
        },
        refreshIndicatorPadding = contentPadding
    ) {
        // val scrollState by viewModel.timelineScrollState.observeAsState(
        //     initial = TimelineScrollState.Zero
        // )
        val listState = rememberLazyListState()
        // LaunchedEffect(Unit) {
        //     snapshotFlow { scrollState }
        //         .distinctUntilChanged()
        //         .collect {
        //             listState.scrollToItem(
        //                 it.firstVisibleItemIndex,
        //                 it.firstVisibleItemScrollOffset
        //             )
        //         }
        // }
        if (items.itemCount > 0) {
            LaunchedEffect(lazyListController) {
                lazyListController?.listState = listState
            }
        }
        // LaunchedEffect(Unit) {
        //     snapshotFlow { listState.isScrollInProgress }
        //         .distinctUntilChanged()
        //         .filter { !it }
        //         .collect {
        //             viewModel.saveScrollState(
        //                 TimelineScrollState(
        //                     firstVisibleItemIndex = listState.firstVisibleItemIndex,
        //                     firstVisibleItemScrollOffset = listState.firstVisibleItemScrollOffset,
        //                 )
        //             )
        //         }
        // }
        LazyUiStatusList(
            items = items,
            state = listState,
            contentPadding = contentPadding,
            loadingBetween = loadingBetween,
            onLoadBetweenClicked = { current, next ->
                viewModel.loadBetween(current, next)
            },
        )
    }
}
