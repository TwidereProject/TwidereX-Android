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
package com.twidere.twiderex.paging.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.twidere.services.microblog.model.IStatus
import com.twidere.twiderex.dataprovider.mapper.toPagingTimeline
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.mock.db.MockCacheDatabase
import com.twidere.twiderex.mock.model.mockIStatus
import com.twidere.twiderex.mock.model.toIPaging
import com.twidere.twiderex.mock.paging.collectDataForTest
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.paging.PagingTimeLineWithStatus
import com.twidere.twiderex.model.paging.saveToDb
import com.twidere.twiderex.paging.mediator.paging.PagingWithGapMediator
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

internal class PagingWithGapMediatorTest {
    @ExperimentalPagingApi
    @Test
    fun refresh_whenReturnedSuccessSaveResultToDatabase() = runBlocking {
        val accountKey = MicroBlogKey.twitter("test")
        val database = MockCacheDatabase()
        val pagingKey = "Mock Test"
        val mediator = MockPagingGapMediator(
            accountKey = accountKey,
            database = database,
            statusId = System.currentTimeMillis().toString(),
            pagingKey = pagingKey
        )
        assert(database.pagingTimelineDao().getPagingSource(mediator.pagingKey, accountKey).collectDataForTest().isEmpty())
        val pagingState = PagingState<Int, PagingTimeLineWithStatus>(emptyList(), config = PagingConfig(20), anchorPosition = 0, leadingPlaceholderCount = 0)
        val result = mediator.load(LoadType.REFRESH, pagingState)
        assert(database.pagingTimelineDao().getPagingSource(mediator.pagingKey, accountKey).collectDataForTest().isNotEmpty())
        assert(result is RemoteMediator.MediatorResult.Success)
        assert(!(result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @ExperimentalPagingApi
    @Test
    fun refresh_whenReturnedResultNotInDatabaseIsGapShouldBeTrue() = runBlocking {
        val accountKey = MicroBlogKey.twitter("test")
        val pagingKey = "Mock Paging Key"
        val database = MockCacheDatabase()
        val sinceId = "sinceId"
        listOf(mockIStatus(sinceId).toPagingTimeline(accountKey, pagingKey)).saveToDb(database)
        val mediator = MockPagingGapMediator(
            accountKey = accountKey,
            database = database,
            statusId = "newId",
            pagingKey = pagingKey
        )
        val pagingState = PagingState<Int, PagingTimeLineWithStatus>(emptyList(), config = PagingConfig(20), anchorPosition = 0, leadingPlaceholderCount = 0)
        mediator.load(LoadType.REFRESH, pagingState)
        val result = database.pagingTimelineDao().getLatest(pagingKey, accountKey)
        assertEquals(true, result?.timeline?.isGap)
        assertEquals(true, result?.status?.isGap)
    }

    @ExperimentalPagingApi
    @Test
    fun refresh_whenReturnedResultInDatabaseIsGapShouldBeFalse() = runBlocking {
        val accountKey = MicroBlogKey.twitter("test")
        val pagingKey = "Mock Paging Key"
        val database = MockCacheDatabase()
        val sinceId = "sinceId"
        listOf(mockIStatus(sinceId).toPagingTimeline(accountKey, pagingKey)).saveToDb(database)
        val mediator = MockPagingGapMediator(
            accountKey = accountKey,
            database = database,
            statusId = sinceId,
            pagingKey = pagingKey
        )
        val pagingState = PagingState<Int, PagingTimeLineWithStatus>(emptyList(), config = PagingConfig(20), anchorPosition = 0, leadingPlaceholderCount = 0)
        mediator.load(LoadType.REFRESH, pagingState)
        val result = database.pagingTimelineDao().getLatest(pagingKey, accountKey)
        assertEquals(false, result?.timeline?.isGap)
        assertEquals(false, result?.status?.isGap)
    }
}

internal class MockPagingGapMediator(
    accountKey: MicroBlogKey,
    database: CacheDatabase,
    private val statusId: String,
    override val pagingKey: String
) : PagingWithGapMediator(
    accountKey = accountKey,
    database = database
) {
    override suspend fun loadBetweenImpl(
        pageSize: Int,
        max_id: String?,
        since_id: String?
    ): List<IStatus> {
        delay(5)
        return listOf(mockIStatus(id = statusId)).toIPaging()
    }
}
