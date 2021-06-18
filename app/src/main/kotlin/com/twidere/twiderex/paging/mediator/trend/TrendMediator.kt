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
package com.twidere.twiderex.paging.mediator.trend

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.paging.map
import com.twidere.services.microblog.TrendService
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.db.mapper.toDbTrend
import com.twidere.twiderex.db.model.DbTrendWithHistory
import com.twidere.twiderex.db.model.saveToDb
import com.twidere.twiderex.defaultLoadCount
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiTrend
import com.twidere.twiderex.model.ui.UiTrend.Companion.toUi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalPagingApi::class)
class TrendMediator(
    private val database: CacheDatabase,
    private val service: TrendService,
    private val accountKey: MicroBlogKey,
    private val locationId: String
) : RemoteMediator<Int, DbTrendWithHistory>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, DbTrendWithHistory>
    ): MediatorResult {
        return try {
            if (loadType == LoadType.REFRESH) {
                val lists = service.trends(locationId).map { it.toDbTrend(accountKey) }
                saveLists(lists)
            }
            MediatorResult.Success(endOfPaginationReached = true)
        } catch (e: Throwable) {
            MediatorResult.Error(e)
        }
    }

    fun pager(
        config: PagingConfig = PagingConfig(
            pageSize = defaultLoadCount,
            enablePlaceholders = false
        ),
        pagingSourceFactory: () -> PagingSource<Int, DbTrendWithHistory> = {
            database.trendDao().getPagingSource(accountKey = accountKey)
        }
    ): Pager<Int, DbTrendWithHistory> {
        return Pager(
            config = config,
            remoteMediator = this,
            pagingSourceFactory = pagingSourceFactory,
        )
    }

    private suspend fun saveLists(lists: List<DbTrendWithHistory>) {
        database.trendDao().clearAll(accountKey)
        lists.saveToDb(database)
    }

    companion object {
        fun Pager<Int, DbTrendWithHistory>.toUi(): Flow<PagingData<UiTrend>> {
            return this.flow.map { pagingData ->
                pagingData.map {
                    it.toUi()
                }
            }
        }
    }
}
