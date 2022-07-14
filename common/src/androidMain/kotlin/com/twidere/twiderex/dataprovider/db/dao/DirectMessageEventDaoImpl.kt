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
package com.twidere.twiderex.dataprovider.db.dao

import androidx.paging.PagingSource
import androidx.room.withTransaction
import com.twidere.twiderex.db.dao.DirectMessageEventDao
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiDMEvent
import com.twidere.twiderex.room.db.RoomCacheDatabase
import com.twidere.twiderex.room.db.model.DbDMEventWithAttachments.Companion.saveToDb
import com.twidere.twiderex.room.db.paging.getPagingSource
import com.twidere.twiderex.room.db.transform.toDbMEventWithAttachments
import com.twidere.twiderex.room.db.transform.toUi

internal class DirectMessageEventDaoImpl(
    private val roomCacheDatabase: RoomCacheDatabase
) : DirectMessageEventDao {
    override fun getPagingSource(
        accountKey: MicroBlogKey,
        conversationKey: MicroBlogKey
    ): PagingSource<Int, UiDMEvent> {
        return roomCacheDatabase.directMessageDao().getPagingSource(
            cacheDatabase = roomCacheDatabase,
            accountKey = accountKey,
            conversationKey = conversationKey
        )
    }

    override suspend fun findWithMessageKey(
        accountKey: MicroBlogKey,
        conversationKey: MicroBlogKey,
        messageKey: MicroBlogKey
    ) = roomCacheDatabase.directMessageDao().findWithMessageKey(accountKey, conversationKey, messageKey)?.toUi()

    override suspend fun delete(message: UiDMEvent) {
        roomCacheDatabase.withTransaction {
            roomCacheDatabase.directMessageDao().findWithMessageKey(
                accountKey = message.accountKey,
                conversationKey = message.conversationKey,
                messageKey = message.messageKey
            )?.let {
                roomCacheDatabase.directMessageDao().delete(message.toDbMEventWithAttachments(dbId = it.message._id).message)
            }
        }
    }

    override suspend fun getMessageCount(
        accountKey: MicroBlogKey,
        conversationKey: MicroBlogKey
    ) = roomCacheDatabase.directMessageDao().getMessageCount(accountKey, conversationKey)

    override suspend fun insertAll(events: List<UiDMEvent>) {
        events.map {
            it.toDbMEventWithAttachments()
        }.saveToDb(roomCacheDatabase)
    }
}
