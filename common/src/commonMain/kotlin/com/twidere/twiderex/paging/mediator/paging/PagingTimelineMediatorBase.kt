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
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.PagingState
import androidx.paging.map
import com.twidere.services.microblog.model.IStatus
import com.twidere.twiderex.dataprovider.mapper.toPagingTimeline
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.paging.PagingTimeLineWithStatus
import com.twidere.twiderex.model.paging.saveToDb
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.paging.IPagination
import com.twidere.twiderex.paging.IPagingList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalPagingApi::class)
abstract class PagingTimelineMediatorBase<T : IPagination>(
    accountKey: MicroBlogKey,
    database: CacheDatabase,
) : PagingMediator(accountKey = accountKey, database = database) {
    private var paging: T? = null

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PagingTimeLineWithStatus>
    ): MediatorResult {
        try {
            val key = when (loadType) {
                LoadType.APPEND -> {
                    paging
                }
                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.REFRESH -> {
                    paging = null
                    null
                }
            }
            val pageSize = state.config.pageSize
            val last = state.lastItemOrNull()
            val result = load(pageSize, key).let { list ->
                list.map { status ->
                    status.toPagingTimeline(accountKey, pagingKey)
                }.filter {
                    last?.status?.statusKey != it.status.statusKey
                }.let {
                    transform(state, it, list)
                }.also {
                    paging = if (list is IPagingList<*, *>) {
                        @Suppress("UNCHECKED_CAST")
                        list.nextPage as T
                    } else {
                        provideNextPage(list, it)
                    }
                }
            }
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    clearData(database)
                }
                result.saveToDb(database)
            }

            return MediatorResult.Success(
                endOfPaginationReached = !hasMore(result, pageSize)
            )
        } catch (e: Throwable) {
            return MediatorResult.Error(e)
        }
    }

    protected abstract fun provideNextPage(
        raw: List<IStatus>,
        result: List<PagingTimeLineWithStatus>
    ): T

    protected open fun transform(
        state: PagingState<Int, PagingTimeLineWithStatus>,
        data: List<PagingTimeLineWithStatus>,
        list: List<IStatus>
    ): List<PagingTimeLineWithStatus> {
        return data
    }

    protected open fun hasMore(
        result: List<PagingTimeLineWithStatus>,
        pageSize: Int
    ) = result.isNotEmpty()

    protected open suspend fun clearData(database: CacheDatabase) {
        database.pagingTimelineDao().clearAll(pagingKey, accountKey = accountKey)
    }

    protected abstract suspend fun load(
        pageSize: Int,
        paging: T?
    ): List<IStatus>
}

fun Pager<Int, PagingTimeLineWithStatus>.toUi(): Flow<PagingData<UiStatus>> {
    return flow.map { pagingData ->
        pagingData.map { it.status }
    }
}
