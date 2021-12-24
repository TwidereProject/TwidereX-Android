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
import androidx.room.Transaction
import androidx.room.Update
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.room.db.model.DbList
import kotlinx.coroutines.flow.Flow

@Dao
internal interface RoomListsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(lists: List<DbList>)

    @Query("SELECT * FROM lists WHERE listKey == :listKey AND accountKey == :accountKey")
    suspend fun findWithListKey(listKey: MicroBlogKey, accountKey: MicroBlogKey): DbList?

    @Query("SELECT * FROM lists WHERE listKey == :listKey AND accountKey == :accountKey")
    fun findWithListKeyWithFlow(listKey: MicroBlogKey, accountKey: MicroBlogKey): Flow<DbList?>

    @Query("SELECT * FROM lists")
    suspend fun findAll(): List<DbList>?

    @Query("SELECT * FROM lists WHERE accountKey == :accountKey")
    suspend fun findWithAccountKey(accountKey: MicroBlogKey): List<DbList>?

    @Transaction
    @Query("SELECT * FROM lists WHERE accountKey == :accountKey LIMIT :limit OFFSET :offset")
    suspend fun getPagingList(
        accountKey: MicroBlogKey,
        limit: Int,
        offset: Int
    ): List<DbList>

    @Transaction
    @Query("SELECT COUNT(*) FROM (SELECT * FROM lists WHERE accountKey == :accountKey)")
    suspend fun getPagingListCount(
        accountKey: MicroBlogKey,
    ): Int

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(lists: List<DbList>)

    @Delete
    suspend fun delete(lists: List<DbList>)

    @Query("DELETE FROM lists WHERE accountKey == :accountKey")
    suspend fun clearAll(
        accountKey: MicroBlogKey,
    )
}
