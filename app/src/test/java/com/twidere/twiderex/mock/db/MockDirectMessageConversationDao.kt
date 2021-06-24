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
package com.twidere.twiderex.mock.db

import androidx.paging.PagingSource
import com.twidere.services.twitter.model.User
import com.twidere.twiderex.db.dao.DirectMessageConversationDao
import com.twidere.twiderex.db.mapper.toDbUser
import com.twidere.twiderex.db.model.DbDMConversation
import com.twidere.twiderex.db.model.DbDMEvent
import com.twidere.twiderex.db.model.DbDMEventWithAttachments
import com.twidere.twiderex.db.model.DbDirectMessageConversationWithMessage
import com.twidere.twiderex.model.MicroBlogKey

class MockDirectMessageConversationDao : DirectMessageConversationDao {
    val db = mutableListOf<DbDMConversation>()
    override suspend fun insertAll(conversations: List<DbDMConversation>) {
        db.addAll(conversations)
    }

    override suspend fun find(accountKey: MicroBlogKey): List<DbDirectMessageConversationWithMessage> {
        return db.map {
            DbDirectMessageConversationWithMessage(
                conversation = it,
                latestMessage = DbDMEventWithAttachments(
                    message = DbDMEvent(
                        "",
                        accountKey,
                        1,
                        it.conversationKey,
                        "",
                        MicroBlogKey.Empty,
                        "",
                        System.currentTimeMillis(),
                        "",
                        accountKey,
                        accountKey,
                        sendStatus = DbDMEvent.SendStatus.SUCCESS
                    ),
                    emptyList(),
                    emptyList(),
                    User(
                        id = accountKey.id.toLong(),
                        idStr = accountKey.id
                    ).toDbUser()
                )
            )
        }
    }

    override fun getPagingSource(accountKey: MicroBlogKey): PagingSource<Int, DbDirectMessageConversationWithMessage> {
        TODO("Not yet implemented")
    }

    override suspend fun delete(data: DbDMConversation) {
        TODO("Not yet implemented")
    }

    override suspend fun clearAll(accountKey: MicroBlogKey) {
        TODO("Not yet implemented")
    }
}
