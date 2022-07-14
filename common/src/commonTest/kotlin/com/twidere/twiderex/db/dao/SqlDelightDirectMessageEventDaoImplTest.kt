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
package com.twidere.twiderex.db.dao

import androidx.paging.PagingSource
import com.twidere.twiderex.base.BaseCacheDatabaseTest
import com.twidere.twiderex.dataprovider.mapper.toUi
import com.twidere.twiderex.db.sqldelight.dao.SqlDelightDirectMessageEventDaoImpl
import com.twidere.twiderex.mock.model.mockIDirectMessage
import com.twidere.twiderex.mock.model.mockIUser
import com.twidere.twiderex.model.MicroBlogKey
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals

internal class SqlDelightDirectMessageEventDaoImplTest : BaseCacheDatabaseTest() {
    private lateinit var dao: SqlDelightDirectMessageEventDaoImpl
    private val accountKey = MicroBlogKey.twitter("account")
    override fun setUp() {
        super.setUp()
        dao = SqlDelightDirectMessageEventDaoImpl(
            database = database
        )
    }

    @Test
    fun insertAll_InsertBothEventAndAttachments() = runBlocking {
        val insert = mockIDirectMessage(accountId = accountKey.id, otherUserID = "other")
            .toUi(accountKey, mockIUser(id = "other").toUi(accountKey))
        dao.insertAll(listOf(insert))
        val result = dao.findWithMessageKey(accountKey = accountKey, conversationKey = insert.conversationKey, messageKey = insert.messageKey)
        assertEquals(insert.messageKey, result?.messageKey)
        assertEquals(insert.sender.userKey, result?.sender?.userKey)
        assertEquals(insert.media.first().url, result?.media?.first()?.url)
        assertEquals(insert.urlEntity.first().url, result?.urlEntity?.first()?.url)
    }

    @Test
    fun getPagingSource_PagingSourceGenerateCorrectKeyForNext() = runBlocking {
        val list = listOf(
            mockIDirectMessage(accountId = accountKey.id, otherUserID = "other")
                .toUi(accountKey, mockIUser(id = "other").toUi(accountKey)),
            mockIDirectMessage(accountId = accountKey.id, otherUserID = "other")
                .toUi(accountKey, mockIUser(id = "other").toUi(accountKey)),
            mockIDirectMessage(accountId = accountKey.id, otherUserID = "other")
                .toUi(accountKey, mockIUser(id = "other").toUi(accountKey)),
        )
        dao.insertAll(list)
        val pagingSource = dao.getPagingSource(
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
        val message = mockIDirectMessage(accountId = accountKey.id, otherUserID = "other")
            .toUi(accountKey, mockIUser(id = "other").toUi(accountKey))
        var invalidate = false
        dao.getPagingSource(
            accountKey = accountKey,
            conversationKey = message.conversationKey
        ).apply {
            registerInvalidatedCallback {
                invalidate = true
            }
            load(PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false))
        }
        dao.insertAll(listOf(message))
        val start = System.currentTimeMillis()
        while (!invalidate && System.currentTimeMillis() - start < 3000) {
            continue
        }
        assert(invalidate)
    }
}
