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
package com.twidere.twiderex.room.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.twidere.twiderex.room.db.model.DbDraft
import kotlinx.coroutines.flow.Flow

@Dao
internal interface RoomDraftDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg draft: DbDraft)

    @Query("SELECT * FROM draft")
    fun getAll(): Flow<List<DbDraft>>

    @Query("SELECT * FROM draft WHERE _id == :id")
    suspend fun get(id: String): DbDraft?

    @Delete
    suspend fun remove(draft: DbDraft)

    @Query("SELECT COUNT(*) FROM draft")
    fun getDraftCount(): Flow<Int>
}
