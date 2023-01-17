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
package com.twidere.twiderex.paging.mediator.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.paging.PagingTimeLineWithStatus
import com.twidere.twiderex.model.paging.saveToDb

@OptIn(ExperimentalPagingApi::class)
internal abstract class TimelinePagingMediator<Key>(
  database: CacheDatabase,
  accountKey: MicroBlogKey,
  override val pagingKey: String,
) : PagingMediator(
  database = database,
  accountKey = accountKey,
) {
  var autoSave: Boolean = true

  protected var key: Key? = null

  data class TimelineData<Key>(
    val key: Key?,
    val data: List<PagingTimeLineWithStatus>?
  )

  private var tempData: List<PagingTimeLineWithStatus>? = null

  override suspend fun load(
    loadType: LoadType,
    state: PagingState<Int, PagingTimeLineWithStatus>
  ): MediatorResult {
    key = when (loadType) {
      LoadType.REFRESH -> {
        null
      }

      LoadType.APPEND -> {
        key
      }

      else -> {
        return MediatorResult.Success(endOfPaginationReached = true)
      }
    }
    return try {
      val result = load(
        nextKey = key,
        pageSize = if (loadType == LoadType.REFRESH) state.config.initialLoadSize else state.config.pageSize
      )
      if (autoSave) {
        database.withTransaction {
          if (loadType == LoadType.REFRESH) {
            database.pagingTimelineDao().clearAll(
              accountKey = accountKey,
              pagingKey = pagingKey
            )
          }
          result.data?.saveToDb(database)
        }
      } else {
        tempData = result.data
      }
      key = result.key
      MediatorResult.Success(endOfPaginationReached = key == null)
    } catch (e: Throwable) {
      e.printStackTrace()
      MediatorResult.Error(e)
    }
  }

  abstract suspend fun load(nextKey: Key?, pageSize: Int): TimelineData<Key>
}
