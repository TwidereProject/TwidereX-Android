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
import com.twidere.twiderex.room.db.model.DbDMEvent.Companion.saveToDb

@Entity(
    tableName = "dm_event",
    indices = [Index(value = ["accountKey", "conversationKey", "messageKey"], unique = true)],
)
internal data class DbDMEvent(
    @PrimaryKey
    val _id: String,
    val accountKey: MicroBlogKey,
    val sortId: Long,
    // message
    val conversationKey: MicroBlogKey,
    val messageId: String,
    val messageKey: MicroBlogKey,
    // include hash tag in this parameter
    val htmlText: String,
    val originText: String,
    val createdTimestamp: Long,
    val messageType: String,
    val senderAccountKey: MicroBlogKey,
    val recipientAccountKey: MicroBlogKey,
    val sendStatus: SendStatus
) {
    val conversationUserKey: MicroBlogKey
        get() = if (accountKey == senderAccountKey) recipientAccountKey else senderAccountKey

    companion object {
        suspend fun List<DbDMEvent>.saveToDb(cacheDatabase: RoomCacheDatabase) {
            cacheDatabase.directMessageDao().insertAll(this)
        }
    }

    enum class SendStatus {
        PENDING,
        SUCCESS,
        FAILED
    }
}

internal data class DbDMEventWithAttachments(
    @Embedded
    val message: DbDMEvent,

    @Relation(parentColumn = "messageKey", entityColumn = "belongToKey", entity = DbMedia::class)
    val media: List<DbMedia>,

    @Relation(parentColumn = "messageKey", entityColumn = "statusKey", entity = DbUrlEntity::class)
    val urlEntity: List<DbUrlEntity>,

    @Relation(parentColumn = "senderAccountKey", entityColumn = "userKey", entity = DbUser::class)
    val sender: DbUser
) {
    companion object {
        suspend fun List<DbDMEventWithAttachments>.saveToDb(cacheDatabase: RoomCacheDatabase) {
            map {
                it.message
            }.saveToDb(cacheDatabase)

            cacheDatabase.mediaDao().insertAll(
                map {
                    it.media
                }.flatten()
            )

            cacheDatabase.urlEntityDao().insertAll(
                map {
                    it.urlEntity
                }.flatten()
            )

            cacheDatabase.userDao().insertAll(
                map { it.sender }
            )
        }
    }
}
