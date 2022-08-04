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
package com.twidere.twiderex.viewmodel.timeline

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingConfig
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.paging.mediator.paging.PagingWithGapMediator
import com.twidere.twiderex.paging.mediator.paging.pager
import com.twidere.twiderex.paging.mediator.paging.toUi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull

private const val timelinePageSize = 20
private const val timelineInitialLoadSize = 40
private const val FIRST_VISIBLE_KEY_SUFFIX = "_firstVisibleItemIndex"
private const val FIRST_OFFSET_KEY_SUFFIX = "_firstVisibleItemScrollOffset"

data class TimelineScrollState(
  val firstVisibleItemIndex: Int = 0,
  val firstVisibleItemScrollOffset: Int = 0,
)

@OptIn(ExperimentalPagingApi::class)
@Composable
fun TimelinePresenter(
  event: Flow<TimeLineEvent>,
  dataStore: DataStore<Preferences> = get(),
  pagingMediator: PagingWithGapMediator,
  savedStateKey: String?,
): TimelineState {

  val source = remember {
    pagingMediator.pager(
      config = PagingConfig(
        pageSize = timelinePageSize,
        initialLoadSize = timelineInitialLoadSize
      )
    ).toUi()
  }.collectAsLazyPagingItems()

  val loadingBetween by remember {
    pagingMediator.loadingBetween
  }.collectAsState(
    emptyList()
  )

  val listState = rememberLazyListState()

  LaunchedEffect(Unit) {
    savedStateKey?.let {
      val firstVisibleItemIndexKey = intPreferencesKey("$it$FIRST_VISIBLE_KEY_SUFFIX")
      val firstVisibleItemScrollOffsetKey =
        intPreferencesKey("$it$FIRST_OFFSET_KEY_SUFFIX")
      dataStore.data.firstOrNull()?.let {
        val firstVisibleItemIndex = it[firstVisibleItemIndexKey] ?: 0
        val firstVisibleItemScrollOffset = it[firstVisibleItemScrollOffsetKey] ?: 0
        TimelineScrollState(
          firstVisibleItemIndex = firstVisibleItemIndex,
          firstVisibleItemScrollOffset = firstVisibleItemScrollOffset,
        )
      }
    }?.let {
      listState.scrollToItem(
        it.firstVisibleItemIndex,
        it.firstVisibleItemScrollOffset
      )
    }
  }

  LaunchedEffect(Unit) {
    // TODO FIXME #listState 20211119: listState.isScrollInProgress is always false on desktop
    //  - https://github.com/JetBrains/compose-jb/issues/1423
    snapshotFlow { listState.isScrollInProgress }
      .distinctUntilChanged()
      .filter { !it }
      .filter { listState.layoutInfo.totalItemsCount != 0 }
      .collect {
        dataStore.edit { preferences ->
          savedStateKey?.let { key ->
            val firstVisibleItemIndexKey = intPreferencesKey("$key$FIRST_VISIBLE_KEY_SUFFIX")
            val firstVisibleItemScrollOffsetKey =
              intPreferencesKey("$key$FIRST_OFFSET_KEY_SUFFIX")
            preferences[firstVisibleItemIndexKey] = listState.firstVisibleItemIndex
            preferences[firstVisibleItemScrollOffsetKey] = listState.firstVisibleItemScrollOffset
          }
        }
      }
  }

  LaunchedEffect(Unit) {
    event.collect {
      when (it) {
        is TimeLineEvent.LoadBetween -> {
          pagingMediator.loadBetween(
            pageSize = timelinePageSize,
            maxStatusKey = it.maxStatusKey,
            sinceStatusKey = it.sinceStatueKey
          )
        }
      }
    }
  }

  return TimelineState(
    source = source,
    loadingBetween = loadingBetween,
    listState = listState
  )
}

interface TimeLineEvent {
  data class LoadBetween(
    val maxStatusKey: MicroBlogKey,
    val sinceStatueKey: MicroBlogKey,
  ) : TimeLineEvent
}

data class TimelineState(
  val source: LazyPagingItems<UiStatus>,
  val loadingBetween: List<MicroBlogKey>,
  val listState: LazyListState
)
