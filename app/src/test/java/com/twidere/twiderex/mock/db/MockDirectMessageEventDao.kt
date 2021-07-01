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
import com.twidere.twiderex.db.dao.DirectMessageEventDao
import com.twidere.twiderex.db.mapper.toDbUser
import com.twidere.twiderex.db.model.DbDMEvent
import com.twidere.twiderex.db.model.DbDMEventWithAttachments
import com.twidere.twiderex.model.MicroBlogKey

class MockDirectMessageEventDao : DirectMessageEventDao {
    val db = mutableListOf<DbDMEvent>()

    override suspend fun insertAll(messages: List<DbDMEvent>) {
        db.addAll(messages)
    }

    override suspend fun getAll(accountKey: MicroBlogKey): List<DbDMEventWithAttachments> {
        return db.map {
            DbDMEventWithAttachments(
                message = it,
                emptyList(),
                emptyList(),
                User(
                    it.accountKey.id.toLong(),
                    it.accountKey.id
                ).toDbUser()
            )
        }
    }

    override suspend fun find(
        accountKey: MicroBlogKey,
        conversationKey: MicroBlogKey
    ): List<DbDMEventWithAttachments> {
        TODO("Not yet implemented")
    }

    override suspend fun findWithMessageKey(
        accountKey: MicroBlogKey,
        conversationKey: MicroBlogKey,
        messageKey: MicroBlogKey
    ): DbDMEventWithAttachments? {
        TODO("Not yet implemented")
    }

    override fun getPagingSource(
        accountKey: MicroBlogKey,
        conversationKey: MicroBlogKey
    ): PagingSource<Int, DbDMEventWithAttachments> {
        TODO("Not yet implemented")
    }

    override suspend fun delete(data: DbDMEvent) {
        TODO("Not yet implemented")
    }

    override fun getMessageCount(accountKey: MicroBlogKey, conversationKey: MicroBlogKey): Long {
        TODO("Not yet implemented")
    }

    override suspend fun clearConversation(
        accountKey: MicroBlogKey,
        conversationKey: MicroBlogKey
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun clearAll(accountKey: MicroBlogKey) {
        TODO("Not yet implemented")
    }
}
