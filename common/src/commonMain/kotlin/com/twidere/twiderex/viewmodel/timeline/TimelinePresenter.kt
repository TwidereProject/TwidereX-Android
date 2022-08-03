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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

private const val timelinePageSize = 20
private const val timelineInitialLoadSize = 40

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

  val timeLineScrollState by remember {
    flow<TimelineScrollState?> {
      savedStateKey?.let {
        val firstVisibleItemIndexKey = intPreferencesKey("${it}_firstVisibleItemIndex")
        val firstVisibleItemScrollOffsetKey =
          intPreferencesKey("${it}_firstVisibleItemScrollOffset")
        dataStore.data.firstOrNull()?.let {
          val firstVisibleItemIndex = it[firstVisibleItemIndexKey] ?: 0
          val firstVisibleItemScrollOffset = it[firstVisibleItemScrollOffsetKey] ?: 0
          TimelineScrollState(
            firstVisibleItemIndex = firstVisibleItemIndex,
            firstVisibleItemScrollOffset = firstVisibleItemScrollOffset,
          )
        }
      }
    }
  }.collectAsState(null)

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
        is TimeLineEvent.SaveScrollState -> {
          dataStore.edit { preferences ->
            savedStateKey?.let { key ->
              val firstVisibleItemIndexKey = intPreferencesKey("${key}_firstVisibleItemIndex")
              val firstVisibleItemScrollOffsetKey =
                intPreferencesKey("${key}_firstVisibleItemScrollOffset")
              preferences[firstVisibleItemIndexKey] = it.offset.firstVisibleItemIndex
              preferences[firstVisibleItemScrollOffsetKey] = it.offset.firstVisibleItemScrollOffset
            }
          }
        }
      }
    }
  }

  return TimelineState(
    source = source,
    loadingBetween = loadingBetween,
    timelineScrollState = timeLineScrollState
  )
}

interface TimeLineEvent {
  data class LoadBetween(
    val maxStatusKey: MicroBlogKey,
    val sinceStatueKey: MicroBlogKey,
  ) : TimeLineEvent
  data class SaveScrollState(
    val offset: TimelineScrollState
  ) : TimeLineEvent
}

data class TimelineState(
  val source: LazyPagingItems<UiStatus>,
  val loadingBetween: List<MicroBlogKey>,
  val timelineScrollState: TimelineScrollState?
)
