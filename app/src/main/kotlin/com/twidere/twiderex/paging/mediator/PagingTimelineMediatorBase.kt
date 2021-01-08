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
package com.twidere.twiderex.paging.mediator

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
import com.twidere.twiderex.utils.notify

@OptIn(ExperimentalPagingApi::class)
abstract class PagingTimelineMediatorBase(
    accountKey: MicroBlogKey,
    database: CacheDatabase,
    private val inAppNotification: InAppNotification
) : PagingMediator(accountKey = accountKey, database = database) {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, DbPagingTimelineWithStatus>
    ): MediatorResult {
        try {
            val key = when (loadType) {
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = true
                        )
                    lastItem.status.status.data.statusId
                }
                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.REFRESH -> {
                    null
                }
            }
            val pageSize = state.config.pageSize

            val result = load(pageSize, key).map {
                it.toDbTimeline(accountKey, TimelineType.Custom).toPagingDbTimeline(pagingKey)
            }.let {
                it.filter {
                    it.status.status.data.statusId != key
                }
            }.let {
                transform(it)
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

    protected open fun transform(data: List<DbPagingTimelineWithStatus>): List<DbPagingTimelineWithStatus> {
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
        max_id: String?
    ): List<IStatus>
}
