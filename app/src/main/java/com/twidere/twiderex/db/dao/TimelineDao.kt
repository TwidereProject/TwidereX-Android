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
package com.twidere.twiderex.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.twidere.twiderex.db.model.DbTimeline
import com.twidere.twiderex.db.model.DbTimelineWithStatus
import com.twidere.twiderex.db.model.TimelineType
import com.twidere.twiderex.model.UserKey

@Dao
interface TimelineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(timeline: List<DbTimeline>)

    @Transaction
    @Query("SELECT * FROM timeline WHERE userKey == :userKey AND type == :timelineType ORDER BY timestamp DESC")
    fun getAllWithLiveData(
        userKey: UserKey,
        timelineType: TimelineType
    ): LiveData<List<DbTimelineWithStatus>>

    @Transaction
    @Query("SELECT * FROM timeline WHERE statusId == :id AND userKey == :userKey")
    suspend fun findWithStatusId(id: String, userKey: UserKey): DbTimelineWithStatus?

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(timeline: List<DbTimeline>)
}
