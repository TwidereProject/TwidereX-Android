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
import com.twidere.services.microblog.model.IPaging
import com.twidere.services.microblog.model.IStatus
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.mock.db.MockCacheDatabase
import com.twidere.twiderex.mock.model.mockIStatus
import com.twidere.twiderex.mock.model.toIPaging
import com.twidere.twiderex.mock.paging.collectDataForTest
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.paging.PagingTimeLineWithStatus
import com.twidere.twiderex.paging.IPagination
import com.twidere.twiderex.paging.mediator.paging.PagingTimelineMediatorBase
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

internal class PagingTimelineMediatorBaseTest {
    @ExperimentalPagingApi
    @Test
    fun refresh_whenReturnedSuccessSaveResultToDatabase() = runBlocking {
        val accountKey = MicroBlogKey.twitter("test")
        val database = MockCacheDatabase()
        val mediator = MockPagingTimelineMediatorBase(
            accountKey = accountKey,
            database = database
        )
        assert(database.pagingTimelineDao().getPagingSource(mediator.pagingKey, accountKey).collectDataForTest().isEmpty())
        val pagingState = PagingState<Int, PagingTimeLineWithStatus>(emptyList(), config = PagingConfig(20), anchorPosition = 0, leadingPlaceholderCount = 0)
        val result = mediator.load(LoadType.REFRESH, pagingState)
        assert(database.pagingTimelineDao().getPagingSource(mediator.pagingKey, accountKey).collectDataForTest().isNotEmpty())
        assert(result is RemoteMediator.MediatorResult.Success)
        assert(!(result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun refresh_SaveTransformedDataIfTransformed() = runBlocking {
        val accountKey = MicroBlogKey.twitter("test")
        val database = MockCacheDatabase()
        val mediator = MockTransformPagingTimelineMediatorBase(
            accountKey = accountKey,
            database = database
        )
        assert(database.pagingTimelineDao().getPagingSource(mediator.pagingKey, accountKey).collectDataForTest().isEmpty())
        val pagingState = PagingState<Int, PagingTimeLineWithStatus>(emptyList(), config = PagingConfig(20), anchorPosition = 0, leadingPlaceholderCount = 0)
        val result = mediator.load(LoadType.REFRESH, pagingState)
        val timelines = database.pagingTimelineDao().getPagingSource(mediator.pagingKey, accountKey).collectDataForTest()
        assertEquals("transformed text", timelines.first().status.rawText)
        assert(result is RemoteMediator.MediatorResult.Success)
        assert(!(result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }
}

internal open class MockPagingTimelineMediatorBase(
    accountKey: MicroBlogKey,
    database: CacheDatabase
) : PagingTimelineMediatorBase<MockPagingTimelineMediatorBase.MockPagination>(accountKey = accountKey, database = database) {
    class MockPagination(
        val nextKey: String?
    ) : IPagination

    override val pagingKey: String
        get() = "Mock paging key"

    override fun provideNextPage(
        raw: List<IStatus>,
        result: List<PagingTimeLineWithStatus>
    ): MockPagination {
        return if (raw is IPaging) {
            MockPagination(raw.nextPage)
        } else {
            MockPagination(null)
        }
    }

    override suspend fun load(pageSize: Int, paging: MockPagination?): List<IStatus> {
        return listOf(mockIStatus()).toIPaging()
    }
}

internal class MockTransformPagingTimelineMediatorBase(
    accountKey: MicroBlogKey,
    database: CacheDatabase
) : MockPagingTimelineMediatorBase(
    accountKey = accountKey, database = database
) {
    override fun transform(
        state: PagingState<Int, PagingTimeLineWithStatus>,
        data: List<PagingTimeLineWithStatus>,
        list: List<IStatus>
    ): List<PagingTimeLineWithStatus> {
        return data.map {
            it.copy(status = it.status.copy(rawText = "transformed text"))
        }
    }
}
