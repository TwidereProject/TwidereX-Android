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
import com.twidere.services.microblog.model.IStatus
import com.twidere.twiderex.dataprovider.mapper.toPagingTimeline
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.paging.PagingTimeLineWithStatus
import com.twidere.twiderex.model.paging.saveToDb
import com.twidere.twiderex.model.ui.UiGap
import com.twidere.twiderex.model.ui.UiTimeline
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalPagingApi::class)
abstract class PagingWithGapMediator(
  accountKey: MicroBlogKey,
  database: CacheDatabase,
) : PagingMediator(accountKey = accountKey, database = database) {
  override suspend fun initialize(): InitializeAction {
    return InitializeAction.SKIP_INITIAL_REFRESH
  }

  override suspend fun load(
    loadType: LoadType,
    state: PagingState<Int, UiTimeline>
  ): MediatorResult {
    val maxStatusKey = when (loadType) {
      LoadType.APPEND -> {
        val lastItem = state.lastItemOrNull()
          ?: return MediatorResult.Success(
            endOfPaginationReached = true,
          )
        lastItem.statusKey
      }

      LoadType.PREPEND -> {
        return MediatorResult.Success(endOfPaginationReached = true)
      }

      LoadType.REFRESH -> {
        null
      }
    }
    val sinceStatueKey = when (loadType) {
      LoadType.APPEND -> {
        null
      }

      LoadType.PREPEND -> {
        return MediatorResult.Success(endOfPaginationReached = true)
      }

      LoadType.REFRESH -> {
        withContext(Dispatchers.IO) {
          database.pagingTimelineDao()
            .getLatest(pagingKey, accountKey)?.statusKey
        }
      }
    }
    return loadBetween(
      pageSize = if (loadType === LoadType.REFRESH) {
        state.config.initialLoadSize
      } else {
        state.config.pageSize
      },
      maxStatusKey = maxStatusKey,
      sinceStatusKey = sinceStatueKey,
    )
  }

  suspend fun loadBetween(
    pageSize: Int,
    maxStatusKey: MicroBlogKey? = null,
    sinceStatusKey: MicroBlogKey? = null,
  ): MediatorResult {
    try {
      val gap = if (maxStatusKey != null && sinceStatusKey != null) {
        database.pagingTimelineDao()
          .findWithStatusKey(MicroBlogKey.gap(maxStatusKey.id, sinceStatusKey.id), accountKey)
          ?.let {
            it as? UiGap
          }?.let {
            it.copy(loading = true)
          }?.let {
            database.pagingTimelineDao().insertAll(listOf(it))
            it
          }
      } else {
        null
      }
      val max_id = withContext(Dispatchers.IO) {
        maxStatusKey?.let { database.statusDao().findWithStatusKey(it, accountKey)?.statusId }
      }
      val result = loadBetweenImpl(pageSize, max_id = max_id, since_id = null).let { list ->
        list.map {
          it.toPagingTimeline(accountKey, pagingKey)
        }.let {
          transform(it, list)
        }
      }
      database.withTransaction {
        // if (maxStatusKey != null && sinceStatusKey != null) {
        //   database.pagingTimelineDao()
        //     .findWithStatusKey(MicroBlogKey.gap(maxStatusKey.id, sinceStatusKey.id), accountKey)
        //     ?.let {
        //       database.pagingTimelineDao().delete(MicroBlogKey.gap(maxStatusKey.id, sinceStatusKey.id))
        //     }
        // }
        if (sinceStatusKey != null) {
          result.lastOrNull()?.let {
            val status = database.pagingTimelineDao().findWithStatusKey(it.status.statusKey, accountKey = accountKey)
            if (status == null) {
              // is gap
              database.pagingTimelineDao()
                .insertAll(
                  listOf(
                    UiGap(
                      maxId = it.status.statusId,
                      sinceId = sinceStatusKey.id,
                      loading = false,
                    ),
                  ),
                )
            }
          }
        }
        result.saveToDb(database)
      }
      return MediatorResult.Success(
        endOfPaginationReached = !hasMore(result, max_id),
      )
    } catch (e: Throwable) {
      return MediatorResult.Error(e)
    }
  }

  protected open suspend fun transform(
    data: List<PagingTimeLineWithStatus>,
    list: List<IStatus>
  ): List<PagingTimeLineWithStatus> {
    return data
  }

  protected abstract suspend fun loadBetweenImpl(
    pageSize: Int,
    max_id: String? = null,
    since_id: String? = null,
  ): List<IStatus>

  protected open suspend fun hasMore(result: List<PagingTimeLineWithStatus>, max_id: String?): Boolean {
    // Twitter API returns single status with max_id  when there is no more data
    return result.size > 1 || result.firstOrNull()?.let {
      it.status.statusId != max_id
    } ?: false
  }
}
