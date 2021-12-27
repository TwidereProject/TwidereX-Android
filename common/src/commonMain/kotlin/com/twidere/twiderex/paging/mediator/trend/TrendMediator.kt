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
package com.twidere.twiderex.paging.mediator.trend

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.twidere.services.microblog.TrendService
import com.twidere.twiderex.dataprovider.mapper.toUi
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.defaultLoadCount
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiTrend
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalPagingApi::class)
class TrendMediator(
    private val database: CacheDatabase,
    private val service: TrendService,
    private val accountKey: MicroBlogKey,
    private val locationId: String
) : RemoteMediator<Int, UiTrend>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, UiTrend>
    ): MediatorResult {
        return try {
            if (loadType == LoadType.REFRESH) {
                val lists = service.trends(locationId)
                database.withTransaction {
                    database.trendDao().clear(accountKey)
                    database.trendDao().insertAll(lists.map { it.toUi(accountKey) })
                }
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
        pagingSourceFactory: () -> PagingSource<Int, UiTrend> = {
            database.trendDao().getPagingSource(accountKey = accountKey)
        }
    ): Pager<Int, UiTrend> {
        return Pager(
            config = config,
            remoteMediator = this,
            pagingSourceFactory = pagingSourceFactory,
        )
    }

    companion object {
        fun Pager<Int, UiTrend>.toUi(): Flow<PagingData<UiTrend>> {
            return this.flow
        }
    }
}
