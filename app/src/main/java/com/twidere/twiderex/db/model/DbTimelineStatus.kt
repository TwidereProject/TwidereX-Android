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
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.model.PlatformType
import com.twidere.twiderex.model.UserKey

@Entity(
    tableName = "timeline",
    indices = [Index(value = ["statusId", "userKey", "type"], unique = true)],
)
data class DbTimeline(
    @PrimaryKey
    val _id: String,
    val userKey: UserKey,
    val platformType: PlatformType,
    val timestamp: Long,
    var isGap: Boolean,
    val statusId: String,
    val type: TimelineType,
)

enum class TimelineType {
    Home,
    Mentions,
    User,
    UserFavourite,
    Conversation,
    SearchTweets,
}

data class DbTimelineWithStatus(
    @Embedded
    val timeline: DbTimeline,

    @Relation(
        parentColumn = "statusId",
        entityColumn = "statusId",
        entity = DbStatusV2::class,
    )
    val status: DbStatusWithReference,
)

suspend fun List<DbTimelineWithStatus>.saveToDb(
    database: AppDatabase,
) {
    val data = this
        .map { listOf(it.status.status, it.status.quote, it.status.retweet) }
        .flatten()
        .filterNotNull()
    data.map { it.user }.let {
        database.userDao().insertAll(it)
    }
    database.mediaDao().insertAll(data.map { it.media }.flatten())
    data.map { it.data }.let {
        database.statusDao().insertAll(it)
    }
    data.map { it.reactions }.flatten().let {
        database.reactionDao().insertAll(it)
    }
    this.map { it.timeline }.let {
        database.timelineDao().insertAll(it)
    }
}
