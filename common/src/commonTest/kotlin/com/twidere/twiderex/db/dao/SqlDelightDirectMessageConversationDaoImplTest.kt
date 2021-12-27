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
import com.twidere.twiderex.db.sqldelight.dao.SqlDelightDirectMessageConversationDaoImpl
import com.twidere.twiderex.db.sqldelight.dao.SqlDelightDirectMessageEventDaoImpl
import com.twidere.twiderex.mock.model.mockIDirectMessage
import com.twidere.twiderex.mock.model.mockIUser
import com.twidere.twiderex.mock.model.toConversation
import com.twidere.twiderex.model.MicroBlogKey
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class SqlDelightDirectMessageConversationDaoImplTest : BaseCacheDatabaseTest() {
    private lateinit var dao: SqlDelightDirectMessageConversationDaoImpl
    private val accountKey = MicroBlogKey.twitter("account")
    override fun setUp() {
        super.setUp()
        dao = SqlDelightDirectMessageConversationDaoImpl(
            database = database
        )
    }

    @Test
    fun getPagingSource_PagingSourceGenerateCorrectKeyForNext() = runBlocking {
        val list = listOf(
            mockIDirectMessage(accountId = accountKey.id, otherUserID = "other1")
                .toUi(accountKey, mockIUser(id = "other1").toUi(accountKey)),
            mockIDirectMessage(accountId = accountKey.id, otherUserID = "other2")
                .toUi(accountKey, mockIUser(id = "other2").toUi(accountKey)),
            mockIDirectMessage(accountId = accountKey.id, otherUserID = "other3")
                .toUi(accountKey, mockIUser(id = "other3").toUi(accountKey)),
        )
        val eventDao = SqlDelightDirectMessageEventDaoImpl(database)
        eventDao.insertAll(list)
        dao.insertAll(list.map { it.toConversation() })
        val pagingSource = dao.getPagingSource(
            accountKey = accountKey,
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
    fun getPagingSource_pagingSourceInvalidateAfterDbUpdate() = runBlocking {
        val message = mockIDirectMessage(accountId = accountKey.id, otherUserID = "other")
            .toUi(accountKey, mockIUser(id = "other").toUi(accountKey))
        var invalidate = false
        dao.getPagingSource(
            accountKey = accountKey,
        ).apply {
            registerInvalidatedCallback {
                invalidate = true
            }
            load(PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false))
        }
        val eventDao = SqlDelightDirectMessageEventDaoImpl(database)
        eventDao.insertAll(listOf(message))
        dao.insertAll(listOf(message.toConversation()))
        val start = System.currentTimeMillis()
        while (!invalidate && System.currentTimeMillis() - start < 3000) {
            continue
        }
        assert(invalidate)
    }

    @Test
    fun findWithConversationKeyFlow_FlowUpdatesAfterDbUpdate() = runBlocking {
        val message = mockIDirectMessage(accountId = accountKey.id, otherUserID = "other")
            .toUi(accountKey, mockIUser(id = "other").toUi(accountKey)).toConversation()
        val flow = dao.findWithConversationKeyFlow(accountKey = accountKey, conversationKey = message.conversationKey)
        assertNull(flow.firstOrNull())
        dao.insertAll(listOf(message))
        assertNotNull(flow.firstOrNull())
        dao.insertAll(listOf(message.copy(conversationName = "update")))
        assertEquals("update", flow.firstOrNull()?.conversationName)
    }

    @Test
    fun delete_DeleteConversationAndAllMessagesItContainsFromDb() = runBlocking {
        val list = listOf(
            mockIDirectMessage(accountId = accountKey.id, otherUserID = "other1")
                .toUi(accountKey, mockIUser(id = "other1").toUi(accountKey)),
            mockIDirectMessage(accountId = accountKey.id, otherUserID = "other2")
                .toUi(accountKey, mockIUser(id = "other2").toUi(accountKey)),
            mockIDirectMessage(accountId = accountKey.id, otherUserID = "other3")
                .toUi(accountKey, mockIUser(id = "other3").toUi(accountKey)),
        )
        val eventDao = SqlDelightDirectMessageEventDaoImpl(database)
        eventDao.insertAll(list)
        dao.insertAll(list.map { it.toConversation() })
        assertEquals(1, database.dMEventQueries.getMessagesPagingCount(accountKey = accountKey, conversationKey = list.first().conversationKey).executeAsOne())
        assertEquals(3, dao.find(accountKey = accountKey).size)
        dao.delete(list.first().toConversation())
        assertNull(dao.findWithConversationKey(accountKey = accountKey, conversationKey = list.first().conversationKey))
        assertEquals(0, database.dMEventQueries.getMessagesPagingCount(accountKey = accountKey, conversationKey = list.first().conversationKey).executeAsOne())
    }
}
