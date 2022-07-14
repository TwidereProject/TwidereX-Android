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
package com.twidere.twiderex.db

import androidx.paging.PagingSource
import com.twidere.twiderex.dataprovider.db.CacheDatabaseImpl
import com.twidere.twiderex.dataprovider.mapper.toUi
import com.twidere.twiderex.db.base.CacheDatabaseDaoTest
import com.twidere.twiderex.mock.model.mockIDirectMessage
import com.twidere.twiderex.mock.model.mockIUser
import com.twidere.twiderex.model.MicroBlogKey
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class DirectMessageEventDaoImplTest : CacheDatabaseDaoTest() {
    val accountKey = MicroBlogKey.twitter("test")
    @Test
    fun insertAll_SaveBothMessageAndAttachmentsToDatabase() = runBlocking {
        val cacheDatabase = CacheDatabaseImpl(roomDatabase)
        val message = mockIDirectMessage(accountId = accountKey.id, otherUserID = "other")
            .toUi(accountKey, mockIUser(id = "other").toUi(accountKey))
        cacheDatabase.directMessageDao().insertAll(listOf(message))
        assert(roomDatabase.directMessageDao().getAll(accountKey).isNotEmpty())
        assertNotNull(roomDatabase.userDao().findWithUserKey(MicroBlogKey.twitter("other")))
        assert(roomDatabase.mediaDao().findMediaByBelongToKey(message.messageKey).isNotEmpty())
        assert(roomDatabase.urlEntityDao().findWithBelongToKey(message.messageKey).isNotEmpty())
    }

    @Test
    fun getMessageCount_ReturnCorrectCountOfMessage() = runBlocking {
        val cacheDatabase = CacheDatabaseImpl(roomDatabase)
        val message = mockIDirectMessage(accountId = accountKey.id, otherUserID = "other")
            .toUi(accountKey, mockIUser(id = "other").toUi(accountKey))
        assertEquals(
            0,
            cacheDatabase.directMessageDao().getMessageCount(
                accountKey,
                message.conversationKey
            )
        )
        cacheDatabase.directMessageDao().insertAll(listOf(message))
        assertEquals(
            1,
            cacheDatabase.directMessageDao().getMessageCount(
                accountKey,
                message.conversationKey
            )
        )
    }

    @Test
    fun findWithMessageKey() = runBlocking {
        val cacheDatabase = CacheDatabaseImpl(roomDatabase)
        val message = mockIDirectMessage(accountId = accountKey.id, otherUserID = "other")
            .toUi(accountKey, mockIUser(id = "other").toUi(accountKey))
        assertNull(
            cacheDatabase.directMessageDao().findWithMessageKey(
                accountKey = accountKey,
                conversationKey = message.conversationKey,
                messageKey = message.messageKey
            )
        )
        cacheDatabase.directMessageDao().insertAll(listOf(message))
        assertEquals(
            message.messageKey,
            cacheDatabase.directMessageDao().findWithMessageKey(
                accountKey = accountKey,
                conversationKey = message.conversationKey,
                messageKey = message.messageKey
            )?.messageKey
        )
    }

    @Test
    fun delete() = runBlocking {
        val cacheDatabase = CacheDatabaseImpl(roomDatabase)
        val message = mockIDirectMessage(accountId = accountKey.id, otherUserID = "other")
            .toUi(accountKey, mockIUser(id = "other").toUi(accountKey))
        cacheDatabase.directMessageDao().insertAll(listOf(message))
        cacheDatabase.directMessageDao().delete(message)
        assertNull(
            cacheDatabase.directMessageDao().findWithMessageKey(
                accountKey = accountKey,
                conversationKey = message.conversationKey,
                messageKey = message.messageKey
            )
        )
    }
    @Test
    fun getPagingListCount_ReturnsCountMatchesQuery() = runBlocking {
        val cacheDatabase = CacheDatabaseImpl(roomDatabase)
        val list = listOf(
            mockIDirectMessage(accountId = accountKey.id, otherUserID = "other1")
                .toUi(accountKey, mockIUser(id = "other1").toUi(accountKey)),
            mockIDirectMessage(accountId = accountKey.id, otherUserID = "other1")
                .toUi(accountKey, mockIUser(id = "other1").toUi(accountKey)),
            mockIDirectMessage(accountId = accountKey.id, otherUserID = "other3")
                .toUi(accountKey, mockIUser(id = "other3").toUi(MicroBlogKey.twitter("Not included"))),
        )
        cacheDatabase.directMessageDao().insertAll(list)
        assertEquals(2, roomDatabase.directMessageDao().getPagingList(accountKey, limit = 20, offset = 0, conversationKey = list.first().conversationKey).size)
        assertEquals(2, roomDatabase.directMessageDao().getPagingListCount(accountKey, conversationKey = list.first().conversationKey))
    }

    @Test
    fun getPagingSource_PagingSourceGenerateCorrectKeyForNext() = runBlocking {
        val cacheDatabase = CacheDatabaseImpl(roomDatabase)
        val list = listOf(
            mockIDirectMessage(accountId = accountKey.id, otherUserID = "other")
                .toUi(accountKey, mockIUser(id = "other").toUi(accountKey)),
            mockIDirectMessage(accountId = accountKey.id, otherUserID = "other")
                .toUi(accountKey, mockIUser(id = "other").toUi(accountKey)),
            mockIDirectMessage(accountId = accountKey.id, otherUserID = "other")
                .toUi(accountKey, mockIUser(id = "other").toUi(accountKey)),
        )
        cacheDatabase.directMessageDao().insertAll(list)
        val pagingSource = cacheDatabase.directMessageDao().getPagingSource(
            accountKey = accountKey,
            conversationKey = list.first().conversationKey
        )
        val limit = 2
        val result = pagingSource.load(params = PagingSource.LoadParams.Refresh(0, limit, false))
        assert(result is PagingSource.LoadResult.Page)
        assertEquals(limit, (result as PagingSource.LoadResult.Page).nextKey)
        assertEquals(limit, result.data.size)

        val loadMoreResult = pagingSource.load(params = PagingSource.LoadParams.Append(result.nextKey ?: 0, limit, false))
        assert(loadMoreResult is PagingSource.LoadResult.Page)
        assertEquals(null, (loadMoreResult as PagingSource.LoadResult.Page).nextKey)
    }

    @Test
    fun getPagingSource_pagingSourceInvalidateAfterDbUpDate() = runBlocking {
        val cacheDatabase = CacheDatabaseImpl(roomDatabase)
        val message = mockIDirectMessage(accountId = accountKey.id, otherUserID = "other")
            .toUi(accountKey, mockIUser(id = "other").toUi(accountKey))
        var invalidate = false
        cacheDatabase.directMessageDao().getPagingSource(
            accountKey = accountKey,
            conversationKey = message.conversationKey
        ).apply {
            registerInvalidatedCallback {
                invalidate = true
            }
            load(PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false))
        }
        cacheDatabase.directMessageDao().insertAll(listOf(message))
        val start = System.currentTimeMillis()
        while (!invalidate && System.currentTimeMillis() - start < 3000) {
            continue
        }
        assert(invalidate)
    }
}
