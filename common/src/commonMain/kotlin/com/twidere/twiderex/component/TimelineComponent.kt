/*
 *  Twidere X
 *
 *  Copyright (C) TwidereProject and Contributors
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.twidere.twiderex.component.foundation.SwipeToRefreshLayout
import com.twidere.twiderex.component.lazy.LazyListController
import com.twidere.twiderex.component.lazy.ui.LazyUiStatusList
import com.twidere.twiderex.extensions.observeAsState
import com.twidere.twiderex.extensions.refreshOrRetry
import com.twidere.twiderex.kmp.Platform
import com.twidere.twiderex.kmp.currentPlatform
import com.twidere.twiderex.viewmodel.timeline.TimelineScrollState
import com.twidere.twiderex.viewmodel.timeline.TimelineViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.viewModelScope

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
        val listState = rememberLazyListState()
        LaunchedEffect(Unit) {
            var inited = false
            val scrollState = viewModel.provideScrollState()
            snapshotFlow { listState.layoutInfo.totalItemsCount }
                .distinctUntilChanged()
                .filter { it != 0 }
                .filter { !inited }
                .collect {
                    inited = true
                    listState.scrollToItem(
                        scrollState.firstVisibleItemIndex,
                        scrollState.firstVisibleItemScrollOffset
                    )
                }
        }
        if (items.itemCount > 0) {
            LaunchedEffect(lazyListController) {
                lazyListController?.listState = listState
            }
        }
        LaunchedEffect(Unit) {
            // TODO FIXME #listState 20211119: listState.isScrollInProgress is always false on desktop - https://github.com/JetBrains/compose-jb/issues/1423
            snapshotFlow { listState.isScrollInProgress }
                .distinctUntilChanged()
                .filter { !it }
                .filter { listState.layoutInfo.totalItemsCount != 0 }
                .collect {
                    viewModel.saveScrollState(
                        TimelineScrollState(
                            firstVisibleItemIndex = listState.firstVisibleItemIndex,
                            firstVisibleItemScrollOffset = listState.firstVisibleItemScrollOffset,
                        )
                    )
                }
        }
        // TODO: Temporary solution， remove after [FIXME #listState] is fixed
        if (currentPlatform == Platform.JVM) {
            DisposableEffect(Unit) {
                onDispose {
                    viewModel.viewModelScope.launch {
                        viewModel.saveScrollState(
                            TimelineScrollState(
                                firstVisibleItemIndex = listState.firstVisibleItemIndex,
                                firstVisibleItemScrollOffset = listState.firstVisibleItemScrollOffset,
                            )
                        )
                    }
                }
            }
        }
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
