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
package com.twidere.twiderex.paging.dm

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.db.model.DbDirectMessageConversationWithMessage
import com.twidere.twiderex.mock.MockDirectMessageService
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.paging.mediator.dm.DMConversationMediator
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.Executors

/**
 * instead of testing pagination, we should focus on our code logic
 */

@RunWith(AndroidJUnit4::class)
class DMEventMediatorTest {
    private lateinit var mockDataBase: CacheDatabase

    private var mockService = MockDirectMessageService()
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        mockDataBase = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), CacheDatabase::class.java)
            .setTransactionExecutor(Executors.newSingleThreadExecutor()).build()
    }

    @After
    fun tearDown() {
        mockDataBase.clearAllTables()
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun refresh_LoadReturnsSuccessResultWhenSuccess() {
        runBlocking {
            mockService.add(mockService.generateDirectMessage(20, System.currentTimeMillis().toString(), "123"))
            Assert.assertEquals(0, mockDataBase.listsDao().findAll()?.size)
            val mediator = DMConversationMediator(mockDataBase, accountKey = MicroBlogKey.twitter("123")) {
                mockService.getDirectMessages(it, 50)
            }
            val pagingState = PagingState<Int, DbDirectMessageConversationWithMessage>(emptyList(), config = PagingConfig(20), anchorPosition = 0, leadingPlaceholderCount = 0)
            val result = mediator.load(LoadType.REFRESH, pagingState)
            assert(result is RemoteMediator.MediatorResult.Success)
            assert(!(result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun refresh_LoadReturnsErrorResultWhenErrorOccurs() = runBlocking {
        mockService.errorMsg = "Throw test failure"
        val mediator = DMConversationMediator(mockDataBase, accountKey = MicroBlogKey.twitter("123"),) {
            mockService.getDirectMessages(it, 50)
        }
        val pagingState = PagingState<Int, DbDirectMessageConversationWithMessage>(emptyList(), config = PagingConfig(20), anchorPosition = 0, leadingPlaceholderCount = 0)
        val result = mediator.load(LoadType.REFRESH, pagingState)
        assert(result is RemoteMediator.MediatorResult.Error)
    }
}
