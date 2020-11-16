/*
 *  Twidere X
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
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

import androidx.lifecycle.MutableLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.room.withTransaction
import com.twidere.services.microblog.model.IStatus
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.mapper.toDbTimeline
import com.twidere.twiderex.db.model.DbPagingTimeline.Companion.toPagingDbTimeline
import com.twidere.twiderex.db.model.DbPagingTimelineWithStatus
import com.twidere.twiderex.db.model.TimelineType
import com.twidere.twiderex.db.model.saveToDb
import com.twidere.twiderex.model.UserKey
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
abstract class PagingWithGapMediator(
    userKey: UserKey,
    database: AppDatabase,
) : PagingMediator(userKey = userKey, database = database) {
    private var loadCount = 0
    protected open val skipInitialLoad = true
    val loadingBetween = MutableLiveData(listOf<String>())

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, DbPagingTimelineWithStatus>
    ): MediatorResult {
        if (skipInitialLoad && loadCount == 0 && loadType == LoadType.REFRESH) {
            loadCount++
            return MediatorResult.Success(
                endOfPaginationReached = false
            )
        }
        val max_id = when (loadType) {
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
        val since_id = when (loadType) {
            LoadType.APPEND -> {
                null
            }
            LoadType.PREPEND -> {
                return MediatorResult.Success(endOfPaginationReached = true)
            }
            LoadType.REFRESH -> {
                val firstItem = state.firstItemOrNull()
                firstItem?.status?.status?.data?.statusId
            }
        }
        return loadBetween(pageSize = state.config.pageSize, max_id = max_id, since_id = since_id)
    }

    suspend fun loadBetween(
        pageSize: Int,
        max_id: String? = null,
        since_id: String? = null,
    ): MediatorResult {
        if (max_id != null && since_id != null) {
            loadingBetween.postValue((loadingBetween.value ?: listOf()) + max_id)
        }
        try {
            val result = loadBetweenImpl(pageSize, max_id = max_id, since_id = since_id).map {
                it.toDbTimeline(userKey, TimelineType.Custom).toPagingDbTimeline(pagingKey)
            }
            database.withTransaction {
                if (max_id != null) {
                    database.pagingTimelineDao().findWithStatusId(max_id, userKey)?.let {
                        it.isGap = false
                        database.pagingTimelineDao().insertAll(listOf(it))
                    }
                }
                result.lastOrNull()?.timeline?.isGap = result.size >= pageSize
                result.saveToDb(database)
            }
            return MediatorResult.Success(
                endOfPaginationReached = result.isEmpty()
            )
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        } finally {
            if (max_id != null && since_id != null) {
                loadingBetween.postValue((loadingBetween.value ?: listOf()) - max_id)
            }
        }
    }

    protected abstract suspend fun loadBetweenImpl(
        pageSize: Int,
        max_id: String? = null,
        since_id: String? = null,
    ): List<IStatus>
}
