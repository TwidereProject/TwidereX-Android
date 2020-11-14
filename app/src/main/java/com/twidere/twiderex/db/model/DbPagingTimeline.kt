/*
 *  Twidere X
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
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
import com.twidere.twiderex.model.UserKey
import java.util.UUID

@Entity(
    tableName = "paging_timeline",
    indices = [
        Index(
            value = ["userKey", "statusId", "pagingKey"],
            unique = true
        )
    ],
)
data class DbPagingTimeline(
    @PrimaryKey
    val _id: String,
    val userKey: UserKey,
    val pagingKey: String,
    val statusId: String,
    val timestamp: Long,
    var isGap: Boolean,
) {
    companion object {
        fun DbTimelineWithStatus.toPagingDbTimeline(
            pagingKey: String
        ): DbPagingTimelineWithStatus {
            return DbPagingTimelineWithStatus(
                timeline = with(timeline) {
                    DbPagingTimeline(
                        _id = UUID.randomUUID().toString(),
                        userKey = userKey,
                        pagingKey = pagingKey,
                        timestamp = timestamp,
                        statusId = statusId,
                        isGap = isGap,
                    )
                },
                status = status,
            )
        }
    }
}

data class DbPagingTimelineWithStatus(
    @Embedded
    val timeline: DbPagingTimeline,

    @Relation(
        parentColumn = "statusId",
        entityColumn = "statusId",
        entity = DbStatusV2::class,
    )
    val status: DbStatusWithReference,
)

enum class UserTimelineType {
    Status,
    Media,
    Favourite
}

fun UserTimelineType.pagingKey(screenName: String) = "user:$screenName:$this"

suspend fun List<DbPagingTimelineWithStatus>.saveToDb(
    database: AppDatabase,
) {
    val data = this
        .map { listOf(it.status.status, it.status.quote, it.status.retweet) }
        .flatten()
        .filterNotNull()
    data.saveToDb(database)
    this.map { it.timeline }.let {
        database.pagingTimelineDao().insertAll(it)
    }
}
