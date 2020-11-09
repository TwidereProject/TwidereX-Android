/*
 *  TwidereX
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of TwidereX.
 * 
 *  TwidereX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  TwidereX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with TwidereX. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.db.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.twidere.twiderex.model.UserKey

@Entity(
    tableName = "status",
    indices = [Index(value = ["statusId"], unique = true)],
)
data class DbStatusV2(
    /**
     * Id that being used in the database
     */
    @PrimaryKey
    val _id: String,
    /**
     * Actual tweet/toots id
     */
    val statusId: String,
    val text: String,
    val timestamp: Long,
    val retweetCount: Long,
    val likeCount: Long,
    val replyCount: Long,
    val placeString: String?,
    val source: String,
    val hasMedia: Boolean,
    val userId: String,
    val lang: String?,
    val replyStatusId: String?,
    val quoteStatusId: String?,
    val retweetStatusId: String?,
)

data class DbStatusWithMediaAndUser(
    @Embedded
    val data: DbStatusV2,
    @Relation(parentColumn = "statusId", entityColumn = "statusId")
    val media: List<DbMedia>,
    @Relation(parentColumn = "userId", entityColumn = "userId")
    val user: DbUser,
    @Relation(parentColumn = "statusId", entityColumn = "statusId")
    val reactions: List<DbStatusReaction>,
)

data class DbStatusWithReference(
    @Embedded
    val status: DbStatusWithMediaAndUser,
    @Relation(parentColumn = "replyStatusId", entityColumn = "statusId", entity = DbStatusV2::class)
    val replyTo: DbStatusWithMediaAndUser?,
    @Relation(parentColumn = "quoteStatusId", entityColumn = "statusId", entity = DbStatusV2::class)
    val quote: DbStatusWithMediaAndUser?,
    @Relation(parentColumn = "retweetStatusId", entityColumn = "statusId", entity = DbStatusV2::class)
    val retweet: DbStatusWithMediaAndUser?,
)

@Entity(
    tableName = "status_reactions",
    indices = [Index(value = ["statusId", "userKey"], unique = true)],
)
data class DbStatusReaction(
    /**
     * Id that being used in the database
     */
    @PrimaryKey
    val _id: String,
    /**
     * Actual tweet/toots id
     */
    val statusId: String,
    val userKey: UserKey,
    var liked: Boolean,
    var retweeted: Boolean,
)
