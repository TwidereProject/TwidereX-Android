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
package com.twidere.twiderex.mock.db.dao

import androidx.paging.PagingSource
import com.twidere.twiderex.db.dao.DirectMessageConversationDao
import com.twidere.twiderex.mock.paging.MockPagingSource
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiDMConversation
import com.twidere.twiderex.model.ui.UiDMConversationWithLatestMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.jetbrains.annotations.TestOnly

internal class MockDirectMessageConversationDao @TestOnly constructor(private val eventDao: MockDirectMessageEventDao) : DirectMessageConversationDao {
    private val fakeDb = mutableMapOf<MicroBlogKey, MutableList<UiDMConversation>>()
    override fun getPagingSource(accountKey: MicroBlogKey): PagingSource<Int, UiDMConversationWithLatestMessage> {
        return runBlocking {
            MockPagingSource(
                data = find(accountKey)
            )
        }
    }

    override fun findWithConversationKeyFlow(
        accountKey: MicroBlogKey,
        conversationKey: MicroBlogKey
    ): Flow<UiDMConversation?> {
        return flow {
            emit(findWithConversationKey(accountKey, conversationKey))
        }
    }

    override suspend fun findWithConversationKey(
        accountKey: MicroBlogKey,
        conversationKey: MicroBlogKey
    ): UiDMConversation? {
        return fakeDb[accountKey]?.find { it.conversationKey == conversationKey }
    }

    override suspend fun insertAll(listOf: List<UiDMConversation>) {
        listOf.forEach { con ->
            fakeDb[con.accountKey].let {
                if (it.isNullOrEmpty()) {
                    fakeDb[con.accountKey] = mutableListOf(con)
                } else {
                    it.removeAll { it.conversationKey == con.conversationKey }
                    it.add(con)
                }
            }
        }
    }

    override suspend fun find(accountKey: MicroBlogKey): List<UiDMConversationWithLatestMessage> {
        return fakeDb[accountKey]?.mapNotNull {
            eventDao.getLatestMessage(accountKey, it.conversationKey)?.let { event ->
                UiDMConversationWithLatestMessage(
                    conversation = it,
                    latestMessage = event
                )
            }
        } ?: emptyList()
    }

    override suspend fun delete(conversation: UiDMConversation) {
        fakeDb[conversation.accountKey]?.removeAll { it.conversationKey == conversation.conversationKey }
    }
}
