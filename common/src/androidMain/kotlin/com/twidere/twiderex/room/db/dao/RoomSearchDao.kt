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
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.room.db.model.DbSearch
import kotlinx.coroutines.flow.Flow

@Dao
internal interface RoomSearchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(search: List<DbSearch>)

    @Query("SELECT * FROM search where accountKey == :accountKey ORDER BY lastActive DESC")
    fun getAll(accountKey: MicroBlogKey): Flow<List<DbSearch>>

    @Query("SELECT * FROM search where saved == 0 AND accountKey == :accountKey ORDER BY lastActive DESC")
    fun getAllHistory(accountKey: MicroBlogKey): Flow<List<DbSearch>>

    @Query("SELECT * FROM search where saved == 1 AND accountKey == :accountKey ORDER BY lastActive DESC")
    fun getAllSaved(accountKey: MicroBlogKey): Flow<List<DbSearch>>

    @Query("SELECT * FROM search WHERE content == :content AND accountKey == :accountKey")
    suspend fun get(content: String, accountKey: MicroBlogKey): DbSearch?

    @Delete
    suspend fun remove(search: DbSearch)

    @Query("DELETE FROM search where saved == 0")
    suspend fun clear()
}
