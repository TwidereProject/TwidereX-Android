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
package com.twidere.twiderex.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.twidere.twiderex.db.model.DbStatus
import com.twidere.twiderex.model.UserKey

@Dao
interface StatusDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(status: List<DbStatus>)

    @Query("SELECT * FROM status")
    suspend fun getAll(): List<DbStatus>

    @Query("SELECT * FROM status WHERE statusId == :id AND userKey == :userKey")
    suspend fun findWithStatusId(id: String, userKey: UserKey): DbStatus?

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(status: List<DbStatus>)
}
