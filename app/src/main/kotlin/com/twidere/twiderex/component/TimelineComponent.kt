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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSizeConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.twidere.twiderex.R
import com.twidere.twiderex.annotations.IncomingComposeUpdate
import com.twidere.twiderex.component.foundation.LoadingProgress
import com.twidere.twiderex.component.foundation.SwipeToRefreshLayout
import com.twidere.twiderex.component.lazy.LocalLazyListController
import com.twidere.twiderex.component.lazy.loadState
import com.twidere.twiderex.component.status.StatusDivider
import com.twidere.twiderex.component.status.TimelineStatusComponent
import com.twidere.twiderex.extensions.refreshOrRetry
import com.twidere.twiderex.ui.standardPadding
import com.twidere.twiderex.viewmodel.timeline.TimelineScrollState
import com.twidere.twiderex.viewmodel.timeline.TimelineViewModel
import kotlinx.coroutines.launch

@OptIn(IncomingComposeUpdate::class)
@Composable
fun TimelineComponent(viewModel: TimelineViewModel) {
    val items = viewModel.source.collectAsLazyPagingItems()
    val loadingBetween by viewModel.loadingBetween.observeAsState(initial = listOf())
    SwipeToRefreshLayout(
        refreshingState = items.loadState.refresh is LoadState.Loading,
        onRefresh = {
            items.refreshOrRetry()
        },
    ) {
        if (items.itemCount > 0) {
            val lastScrollState = remember {
                viewModel.restoreScrollState()
            }
            val listState = rememberLazyListState(
                initialFirstVisibleItemIndex = lastScrollState.firstVisibleItemIndex,
                initialFirstVisibleItemScrollOffset = lastScrollState.firstVisibleItemScrollOffset,
            )
            val scope = rememberCoroutineScope()
            LocalLazyListController.current.requestScrollTop = {
                scope.launch {
                    listState.snapToItemIndex(0)
                }
            }
            DisposableEffect(listState.isAnimationRunning) {
                if (!listState.isAnimationRunning) {
                    viewModel.saveScrollState(
                        TimelineScrollState(
                            firstVisibleItemIndex = listState.firstVisibleItemIndex,
                            firstVisibleItemScrollOffset = listState.firstVisibleItemScrollOffset,
                        )
                    )
                }
                onDispose { }
            }
            LazyColumn(
                state = listState
            ) {
                itemsIndexed(items) { index, it ->
                    it?.let { item ->
                        Column {
                            TimelineStatusComponent(
                                item,
                            )
                            when {
                                loadingBetween.contains(item.statusKey) -> {
                                    Divider()
                                    LoadingProgress()
                                    Divider()
                                }
                                item.isGap -> {
                                    Divider()
                                    TextButton(
                                        modifier = Modifier
                                            .defaultMinSizeConstraints(
                                                minHeight = ButtonDefaults.MinHeight,
                                            )
                                            .padding(ButtonDefaults.ContentPadding)
                                            .fillMaxWidth(),
                                        onClick = {
                                            items[index + 1]?.let { next ->
                                                viewModel.loadBetween(
                                                    item.statusKey,
                                                    next.statusKey,
                                                )
                                            }
                                        },
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_refresh),
                                            contentDescription = stringResource(
                                                id = R.string.accessibility_scene_timeline_load_gap
                                            )
                                        )
                                        Box(modifier = Modifier.width(standardPadding))
                                        Text(text = stringResource(id = R.string.common_controls_timeline_load_more))
                                    }
                                    Divider()
                                }
                                else -> {
                                    StatusDivider()
                                }
                            }
                        }
                    }
                }
                loadState(items.loadState.append) {
                    items.retry()
                }
            }
        }
    }
}
