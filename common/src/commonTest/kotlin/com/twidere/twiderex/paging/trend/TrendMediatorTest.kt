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
package com.twidere.twiderex.paging.trend

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.twidere.twiderex.MainThreadTestBase
import com.twidere.twiderex.mock.db.MockCacheDatabase
import com.twidere.twiderex.mock.paging.collectDataForTest
import com.twidere.twiderex.mock.service.MockTrendService
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiTrend
import com.twidere.twiderex.paging.mediator.trend.TrendMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

/**
 * instead of testing pagination, we should focus on our code logic
 */

internal class TrendMediatorTest : MainThreadTestBase() {
    private var mockDataBase = MockCacheDatabase()

    private var mockService = MockTrendService()

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun load_saveToDatabaseWhenSuccess() = runBlocking(Dispatchers.Main) {
        val accountKey = MicroBlogKey.twitter("123")
        assert(mockDataBase.trendDao().getPagingSource(accountKey).collectDataForTest().isEmpty())
        val mediator = TrendMediator(mockDataBase, mockService, accountKey = accountKey, "1")
        val pagingState = PagingState<Int, UiTrend>(emptyList(), config = PagingConfig(20), anchorPosition = 0, leadingPlaceholderCount = 0)
        val result = mediator.load(LoadType.REFRESH, pagingState)

        // when mediator get data from service, it store to database
        assert(mockDataBase.trendDao().getPagingSource(accountKey).collectDataForTest().isNotEmpty())
        assert(result is RemoteMediator.MediatorResult.Success)
        assert((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun refresh_LoadReturnsErrorResultWhenErrorOccurs() = runBlocking(Dispatchers.Main) {
        val accountKey = MicroBlogKey.twitter("123")
        mockService.errorMsg = "throw test error"
        assert(mockDataBase.trendDao().getPagingSource(accountKey).collectDataForTest().isEmpty())
        val mediator = TrendMediator(mockDataBase, mockService, accountKey = accountKey, "1")
        val pagingState = PagingState<Int, UiTrend>(emptyList(), config = PagingConfig(20), anchorPosition = 0, leadingPlaceholderCount = 0)
        val result = mediator.load(LoadType.REFRESH, pagingState)
        assert(result is RemoteMediator.MediatorResult.Error)
    }
}
