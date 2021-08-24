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
package com.twidere.twiderex.db

import androidx.paging.PagingSource
import com.twidere.twiderex.dataprovider.db.CacheDatabaseImpl
import com.twidere.twiderex.dataprovider.mapper.toUi
import com.twidere.twiderex.db.base.CacheDatabaseDaoTest
import com.twidere.twiderex.mock.model.mockIDirectMessage
import com.twidere.twiderex.mock.model.mockIUser
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiDMConversation
import com.twidere.twiderex.model.ui.UiDMEvent
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class DirectMessageConversationDaoImplTest : CacheDatabaseDaoTest() {
    val accountKey = MicroBlogKey.twitter("test")

    private fun UiDMEvent.toConversation() = UiDMConversation(
        accountKey = accountKey,
        conversationId = conversationKey.id,
        conversationKey = conversationKey,
        conversationAvatar = sender.profileImage.toString(),
        conversationName = sender.name,
        conversationSubName = sender.screenName,
        conversationType = UiDMConversation.Type.ONE_TO_ONE,
        recipientKey = conversationUserKey
    )

    @Test
    fun delete() = runBlocking {
        val cacheDatabase = CacheDatabaseImpl(roomDatabase)
        val conversation = mockIDirectMessage(accountId = accountKey.id, otherUserID = "other")
            .toUi(accountKey, mockIUser(id = "other").toUi(accountKey)).toConversation()
        cacheDatabase.directMessageConversationDao().insertAll(listOf(conversation))
        cacheDatabase.directMessageConversationDao().delete(conversation)
        assertNull(
            cacheDatabase.directMessageConversationDao().findWithConversationKey(
                accountKey = accountKey,
                conversationKey = conversation.conversationKey,
            )
        )
    }

    @Test
    fun getPagingSource_PagingSourceGenerateCorrectKeyForNext() = runBlocking {
        val cacheDatabase = CacheDatabaseImpl(roomDatabase)
        val list = listOf(
            mockIDirectMessage(accountId = accountKey.id, otherUserID = "other1")
                .toUi(accountKey, mockIUser(id = "other1").toUi(accountKey)),
            mockIDirectMessage(accountId = accountKey.id, otherUserID = "other2")
                .toUi(accountKey, mockIUser(id = "other2").toUi(accountKey)),
            mockIDirectMessage(accountId = accountKey.id, otherUserID = "other3")
                .toUi(accountKey, mockIUser(id = "other3").toUi(accountKey)),
        )
        cacheDatabase.directMessageDao().insertAll(list)
        cacheDatabase.directMessageConversationDao().insertAll(list.map { it.toConversation() })
        val pagingSource = cacheDatabase.directMessageConversationDao().getPagingSource(
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
    fun getPagingSource_pagingSourceInvalidateAfterDbUpDate() = runBlocking {
        val cacheDatabase = CacheDatabaseImpl(roomDatabase)
        val conversation = mockIDirectMessage(accountId = accountKey.id, otherUserID = "other")
            .toUi(accountKey, mockIUser(id = "other").toUi(accountKey)).toConversation()
        var invalidate = false
        cacheDatabase.directMessageConversationDao().getPagingSource(
            accountKey = accountKey,
        ).registerInvalidatedCallback {
            invalidate = true
        }
        cacheDatabase.directMessageConversationDao().insertAll(listOf(conversation))
        val start = System.currentTimeMillis()
        while (!invalidate && System.currentTimeMillis() - start < 3000) {
            continue
        }
        assert(invalidate)
    }

    @Test
    fun findWithConversationKeyFlow() = runBlocking {
        val cacheDatabase = CacheDatabaseImpl(roomDatabase)
        val conversation = mockIDirectMessage(accountId = accountKey.id, otherUserID = "other")
            .toUi(accountKey, mockIUser(id = "other").toUi(accountKey)).toConversation()
        val conversationFlow = cacheDatabase.directMessageConversationDao().findWithConversationKeyFlow(
            accountKey = accountKey,
            conversationKey = conversation.conversationKey
        )
        assertNull(conversationFlow.firstOrNull())
        cacheDatabase.directMessageConversationDao().insertAll(listOf(conversation))
        assertEquals(conversation.conversationKey, conversationFlow.firstOrNull()?.conversationKey)
    }
}
