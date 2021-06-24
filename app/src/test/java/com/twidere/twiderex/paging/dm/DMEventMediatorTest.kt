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

import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import com.twidere.services.microblog.DirectMessageService
import com.twidere.services.twitter.model.User
import com.twidere.twiderex.db.mapper.toDbUser
import com.twidere.twiderex.db.model.DbDirectMessageConversationWithMessage
import com.twidere.twiderex.mock.MockCenter
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.paging.mediator.dm.DMConversationMediator
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

/**
 * instead of testing pagination, we should focus on our code logic
 */

@RunWith(MockitoJUnitRunner::class)
class DMEventMediatorTest {
    private var mockDataBase = MockCenter.mockCacheDatabase()

    private var mockService = MockCenter.mockDirectMessageService() as DirectMessageService

    @Test
    fun load_saveBothConversationAndMessageToDatabaseWhenSuccess() {
        runBlocking {
            assert(mockDataBase.directMessageConversationDao().find(MicroBlogKey.twitter("123")).isEmpty())
            assert(mockDataBase.directMessageDao().getAll(MicroBlogKey.twitter("123")).isEmpty())
            Assert.assertEquals(0, mockDataBase.listsDao().findAll()?.size)
            val mediator = DMConversationMediator(mockDataBase, mockService, accountKey = MicroBlogKey.twitter("123"),) {
                User(
                    id = it.id.toLong(),
                    idStr = it.id
                ).toDbUser()
            }
            val pagingState = PagingState<Int, DbDirectMessageConversationWithMessage>(emptyList(), config = PagingConfig(20), anchorPosition = 0, leadingPlaceholderCount = 0)
            mediator.load(LoadType.REFRESH, pagingState)
            // when mediator get data from service, it store to database
            assert(mockDataBase.directMessageConversationDao().find(MicroBlogKey.twitter("123")).isNotEmpty())
            assert(mockDataBase.directMessageDao().getAll(MicroBlogKey.twitter("123")).isNotEmpty())
        }
    }
}
