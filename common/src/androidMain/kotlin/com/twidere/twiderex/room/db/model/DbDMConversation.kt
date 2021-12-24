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
package com.twidere.twiderex.room.db.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.room.db.RoomCacheDatabase

@Entity(
    tableName = "dm_conversation",
    indices = [Index(value = ["accountKey", "conversationKey"], unique = true)],
)
internal data class DbDMConversation(
    @PrimaryKey
    val _id: String,
    val accountKey: MicroBlogKey,
    // conversation
    val conversationId: String,
    val conversationKey: MicroBlogKey,
    val conversationAvatar: String,
    val conversationName: String,
    val conversationSubName: String,
    val conversationType: Type,
    val recipientKey: MicroBlogKey,
) {
    enum class Type {
        ONE_TO_ONE,
        GROUP
    }

    companion object {
        suspend fun List<DbDMConversation>.saveToDb(cacheDatabase: RoomCacheDatabase) {
            cacheDatabase.directMessageConversationDao().insertAll(this)
        }
    }
}

internal data class DbDirectMessageConversationWithMessage(
    @Relation(parentColumn = "conversationKey", entityColumn = "conversationKey")
    val conversation: DbDMConversation,

    @Embedded
    val latestMessage: DbDMEventWithAttachments
)
