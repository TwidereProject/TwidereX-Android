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
package com.twidere.twiderex.room.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.room.db.model.DbTrend
import com.twidere.twiderex.room.db.model.DbTrendWithHistory

@Dao
internal interface RoomTrendDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(trends: List<DbTrend>)

    @Transaction
    @Query("SELECT * FROM trends WHERE accountKey == :accountKey  LIMIT :limit")
    suspend fun find(accountKey: MicroBlogKey, limit: Int): List<DbTrendWithHistory>

    @Transaction
    @Query("SELECT * FROM trends WHERE accountKey == :accountKey LIMIT :limit OFFSET :offset")
    suspend fun getPagingList(
        accountKey: MicroBlogKey,
        limit: Int,
        offset: Int
    ): List<DbTrendWithHistory>

    @Transaction
    @Query("SELECT COUNT(*) FROM (SELECT * FROM trends WHERE accountKey == :accountKey)")
    suspend fun getPagingListCount(
        accountKey: MicroBlogKey
    ): Int

    @Query("DELETE FROM trends WHERE accountKey == :accountKey")
    suspend fun clearAll(
        accountKey: MicroBlogKey,
    )

    @Transaction
    @Query("SELECT * FROM trends WHERE accountKey == :accountKey")
    fun getAll(
        accountKey: MicroBlogKey,
    ): List<DbTrendWithHistory>
}
