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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import com.twidere.twiderex.component.foundation.SwipeToRefreshLayout
import com.twidere.twiderex.component.lazy.LazyListController
import com.twidere.twiderex.component.lazy.ui.LazyUiStatusList
import com.twidere.twiderex.extensions.refreshOrRetry
import com.twidere.twiderex.kmp.Platform
import com.twidere.twiderex.kmp.currentPlatform
import com.twidere.twiderex.viewmodel.timeline.TimeLineEvent
import com.twidere.twiderex.viewmodel.timeline.TimelineScrollState
import com.twidere.twiderex.viewmodel.timeline.TimelineState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun TimelineComponent(
  state: TimelineState,
  channel: Channel<TimeLineEvent>,
  contentPadding: PaddingValues = PaddingValues(0.dp),
  lazyListController: LazyListController? = null,
) {
  SwipeToRefreshLayout(
    refreshingState = state.source.loadState.refresh is LoadState.Loading,
    onRefresh = {
      state.source.refreshOrRetry()
    },
    refreshIndicatorPadding = contentPadding
  ) {
    val listState = rememberLazyListState()
    LaunchedEffect(Unit) {
      state.timelineScrollState?.let {
        listState.scrollToItem(
          it.firstVisibleItemIndex,
          it.firstVisibleItemScrollOffset
        )
      }
    }
    if (state.source.itemCount > 0) {
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
          channel.trySend(
            TimeLineEvent.SaveScrollState(
              TimelineScrollState(
                firstVisibleItemIndex = listState.firstVisibleItemIndex,
                firstVisibleItemScrollOffset = listState.firstVisibleItemScrollOffset,
              )
            )
          )
        }
    }
    // TODO: Temporary solution， remove after [FIXME #listState] is fixed
    if (currentPlatform == Platform.JVM) {
      DisposableEffect(Unit) {
        onDispose {
          channel.trySend(
            TimeLineEvent.SaveScrollState(
              TimelineScrollState(
                firstVisibleItemIndex = listState.firstVisibleItemIndex,
                firstVisibleItemScrollOffset = listState.firstVisibleItemScrollOffset,
              )
            )
          )
        }
      }
    }
    LazyUiStatusList(
      items = state.source,
      state = listState,
      contentPadding = contentPadding,
      loadingBetween = state.loadingBetween,
      onLoadBetweenClicked = { current, next ->
        channel.trySend(TimeLineEvent.LoadBetween(current, next))
      },
    )
  }
}
