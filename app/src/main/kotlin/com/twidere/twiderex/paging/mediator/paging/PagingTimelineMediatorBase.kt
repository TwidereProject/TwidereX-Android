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
package com.twidere.twiderex.paging.mediator.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.room.withTransaction
import com.twidere.services.http.MicroBlogException
import com.twidere.services.microblog.model.IStatus
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.db.mapper.toDbTimeline
import com.twidere.twiderex.db.model.DbPagingTimeline.Companion.toPagingDbTimeline
import com.twidere.twiderex.db.model.DbPagingTimelineWithStatus
import com.twidere.twiderex.db.model.TimelineType
import com.twidere.twiderex.db.model.saveToDb
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.paging.IPagination
import com.twidere.twiderex.paging.IPagingList
import com.twidere.twiderex.utils.notify

@OptIn(ExperimentalPagingApi::class)
abstract class PagingTimelineMediatorBase<T : IPagination>(
    accountKey: MicroBlogKey,
    database: CacheDatabase,
    private val inAppNotification: InAppNotification
) : PagingMediator(accountKey = accountKey, database = database) {
    private var nextPage: T? = null

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, DbPagingTimelineWithStatus>
    ): MediatorResult {
        try {
            val key = when (loadType) {
                LoadType.APPEND -> {
                    nextPage
                }
                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.REFRESH -> {
                    nextPage = null
                    null
                }
            }
            val pageSize = state.config.pageSize
            val last = state.lastItemOrNull()
            val result = load(pageSize, key).let { list ->
                list.map { status ->
                    status.toDbTimeline(accountKey, TimelineType.Custom)
                        .toPagingDbTimeline(pagingKey)
                }.filter {
                    last?.status?.status?.data?.statusKey != it.status.status.data.statusKey
                }.let {
                    transform(loadType, state, it)
                }.also {
                    nextPage = if (list is IPagingList<*, *>) {
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
        } catch (e: MicroBlogException) {
            e.notify(inAppNotification)
            return MediatorResult.Error(e)
        } catch (e: Throwable) {
            e.notify(inAppNotification)
            return MediatorResult.Error(e)
        }
    }

    protected abstract fun provideNextPage(
        raw: List<IStatus>,
        result: List<DbPagingTimelineWithStatus>
    ): T

    protected open fun transform(
        type: LoadType,
        state: PagingState<Int, DbPagingTimelineWithStatus>,
        data: List<DbPagingTimelineWithStatus>
    ): List<DbPagingTimelineWithStatus> {
        return data
    }

    protected open fun hasMore(
        result: List<DbPagingTimelineWithStatus>,
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
