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
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.model.MicroBlogKey

@Entity(
    tableName = "paging_timeline",
    indices = [
        Index(
            value = ["accountKey", "statusKey", "pagingKey"],
            unique = true
        )
    ],
)
data class DbPagingTimeline(
    @PrimaryKey
    val _id: String,
    val accountKey: MicroBlogKey,
    val pagingKey: String,
    val statusKey: MicroBlogKey,
    val timestamp: Long,
    val sortId: Long,
    var isGap: Boolean,
)

data class DbPagingTimelineWithStatus(
    @Embedded
    val timeline: DbPagingTimeline,

    @Relation(
        parentColumn = "statusKey",
        entityColumn = "statusKey",
        entity = DbStatusV2::class,
    )
    val status: DbStatusWithReference,
)

enum class UserTimelineType {
    Status,
    Media,
    Favourite
}

fun UserTimelineType.pagingKey(accountKey: MicroBlogKey) = "user:$accountKey:$this"

suspend fun List<DbPagingTimelineWithStatus>.saveToDb(
    database: CacheDatabase,
) {

    this.map { it.status }.saveToDb(database)
    this.map { it.timeline }.let {
        database.pagingTimelineDao().insertAll(it)
    }
}
