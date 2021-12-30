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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext

@OptIn(ExperimentalPagingApi::class)
abstract class PagingWithGapMediator(
    accountKey: MicroBlogKey,
    database: CacheDatabase,
) : PagingMediator(accountKey = accountKey, database = database) {

    private val _loadingBetween = MutableStateFlow(listOf<MicroBlogKey>())
    val loadingBetween
        get() = _loadingBetween.asSharedFlow()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.SKIP_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PagingTimeLineWithStatus>
    ): MediatorResult {
        val maxStatusKey = when (loadType) {
            LoadType.APPEND -> {
                val lastItem = state.lastItemOrNull()
                    ?: return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                lastItem.status.statusKey
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
                        .getLatest(pagingKey, accountKey)?.status?.statusKey
                }
            }
        }
        return loadBetween(
            pageSize = state.config.pageSize,
            maxStatusKey = maxStatusKey,
            sinceStatusKey = sinceStatueKey
        )
    }

    suspend fun loadBetween(
        pageSize: Int,
        maxStatusKey: MicroBlogKey? = null,
        sinceStatusKey: MicroBlogKey? = null,
    ): MediatorResult {
        if (maxStatusKey != null && sinceStatusKey != null) {
            _loadingBetween.value = _loadingBetween.value + maxStatusKey
        }
        try {
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
                if (maxStatusKey != null) {
                    database.pagingTimelineDao().findWithStatusKey(maxStatusKey, accountKey)?.let {
                        it.isGap = false
                        database.pagingTimelineDao().insertAll(listOf(it))
                    }
                }
                if (sinceStatusKey != null) {
                    result.lastOrNull()?.let {
                        database.pagingTimelineDao().findWithStatusKey(it.status.statusKey, accountKey = accountKey)
                    }.let {
                        result.lastOrNull()?.timeline?.isGap = it == null
                    }
                }
                result.saveToDb(database)
            }
            return MediatorResult.Success(
                endOfPaginationReached = !hasMore(result, max_id),
            )
        } catch (e: Throwable) {
            return MediatorResult.Error(e)
        } finally {
            if (maxStatusKey != null && sinceStatusKey != null) {
                _loadingBetween.value = _loadingBetween.value - maxStatusKey
            }
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
