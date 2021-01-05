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
package com.twidere.twiderex.db.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.model.MicroBlogKey

@Entity(
    tableName = "timeline",
    indices = [Index(value = ["statusKey", "accountKey", "type"], unique = true)],
)
data class DbTimeline(
    @PrimaryKey
    val _id: String,
    val accountKey: MicroBlogKey,
    val timestamp: Long,
    var isGap: Boolean,
    val statusKey: MicroBlogKey,
    val type: TimelineType,
)

enum class TimelineType {
    Home,
    Mentions,
    Conversation,
    SearchTweets,
    Custom,
}

data class DbTimelineWithStatus(
    @Embedded
    val timeline: DbTimeline,

    @Relation(
        parentColumn = "statusKey",
        entityColumn = "statusKey",
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
    data.saveToDb(database)
    this.map { it.timeline }.let {
        database.timelineDao().insertAll(it)
    }
}
