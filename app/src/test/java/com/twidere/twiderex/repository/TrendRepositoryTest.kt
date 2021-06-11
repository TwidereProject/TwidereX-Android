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
package com.twidere.twiderex.repository

import com.twidere.twiderex.mock.MockCenter
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.MicroBlogKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class TrendRepositoryTest {

    private lateinit var repository: TrendRepository

    private var mockDatabase = MockCenter.mockCacheDatabase()

    private var mockTrendService = MockCenter.mockTrendService()

    @Mock
    private lateinit var mockAccountDetails: AccountDetails

    private lateinit var mocks: AutoCloseable

    private val accountKey = MicroBlogKey.twitter("123")

    @Before
    fun setUp() {
        mocks = MockitoAnnotations.openMocks(this)
        repository = TrendRepository(mockDatabase)
        whenever(mockAccountDetails.accountKey).thenReturn(accountKey)
        whenever(mockAccountDetails.service).thenReturn(mockTrendService)
    }

    @After
    fun tearDown() {
        mocks.close()
    }

    @Test
    fun trends_saveToDbAfterSuccess() = runBlocking {
        assert(mockDatabase.trendDao().find(accountKey, 10).isEmpty())
        val result = repository.trends(mockAccountDetails)
        assert(result.isNotEmpty())
        assert(mockDatabase.trendDao().find(accountKey, 10).isNotEmpty())

        // when service error , the repo get from db
        val serviceFailedResult = repository.trends(mockAccountDetails, "error")
        assert(serviceFailedResult.isNotEmpty())
    }

    @Test
    fun trends_getEmptyListAfterError() = runBlocking {
        val result = repository.trends(mockAccountDetails, "error")
        assert(result.isEmpty())
    }
}
