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
package com.twidere.twiderex.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.twidere.twiderex.db.model.DbPagingTimeline
import com.twidere.twiderex.db.model.DbPagingTimelineWithStatus
import com.twidere.twiderex.model.MicroBlogKey

@Dao
interface PagingTimelineDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(timeline: List<DbPagingTimeline>)

    @Query("SELECT * FROM paging_timeline WHERE statusKey == :statusKey AND accountKey == :accountKey")
    suspend fun findWithStatusKey(statusKey: MicroBlogKey, accountKey: MicroBlogKey): DbPagingTimeline?

    @Query("SELECT * FROM paging_timeline WHERE statusKey in (:statusKey)")
    suspend fun findAllWithStatusKey(statusKey: List<MicroBlogKey>): List<DbPagingTimeline>

    @Transaction
    @Query("SELECT * FROM paging_timeline WHERE pagingKey == :pagingKey AND accountKey == :accountKey ORDER BY sortId DESC")
    fun getPagingSource(
        pagingKey: String,
        accountKey: MicroBlogKey,
    ): PagingSource<Int, DbPagingTimelineWithStatus>

    @Transaction
    @Query("SELECT * FROM paging_timeline WHERE pagingKey == :pagingKey AND accountKey == :accountKey ORDER BY timestamp DESC")
    fun getLatest(pagingKey: String, accountKey: MicroBlogKey): DbPagingTimelineWithStatus?

    @Query("DELETE FROM paging_timeline WHERE pagingKey == :pagingKey AND accountKey == :accountKey")
    suspend fun clearAll(
        pagingKey: String,
        accountKey: MicroBlogKey,
    )

    @Delete
    suspend fun delete(timeline: List<DbPagingTimeline>)

    @Query("DELETE FROM paging_timeline WHERE statusKey == :key")
    suspend fun delete(key: MicroBlogKey)
}
