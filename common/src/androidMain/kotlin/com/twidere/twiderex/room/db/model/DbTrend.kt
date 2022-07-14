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
    tableName = "trends",
    indices = [Index(value = ["trendKey", "url"], unique = true)],
)
internal data class DbTrend(
    @PrimaryKey
    val _id: String,
    val trendKey: MicroBlogKey,
    val accountKey: MicroBlogKey,
    val displayName: String,
    val url: String,
    val query: String,
    val volume: Long,
)

internal data class DbTrendWithHistory(
    @Embedded
    val trend: DbTrend,

    @Relation(
        parentColumn = "trendKey",
        entityColumn = "trendKey",
        entity = DbTrendHistory::class
    )
    val history: List<DbTrendHistory>,
) {
    companion object {
        suspend fun List<DbTrendWithHistory>.saveToDb(database: RoomCacheDatabase) {
            map { it.trend }.let {
                database.trendDao().insertAll(it)
            }
            map { it.history }
                .flatten()
                .let {
                    database.trendHistoryDao().insertAll(it)
                }
        }
    }
}
