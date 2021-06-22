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
import com.twidere.twiderex.db.model.DbDirectMessage
import com.twidere.twiderex.db.model.DbDirectMessageWithMedia
import com.twidere.twiderex.model.MicroBlogKey

@Dao
interface DirectMessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(trends: List<DbDirectMessage>)

    @Transaction
    @Query("SELECT * FROM direct_message WHERE accountKey == :accountKey AND conversationKey == :conversationKey ORDER BY sortId DESC")
    suspend fun find(accountKey: MicroBlogKey, conversationKey: MicroBlogKey): List<DbDirectMessageWithMedia>

    @Transaction
    @Query("SELECT * FROM direct_message WHERE accountKey == :accountKey AND conversationKey == :conversationKey ORDER BY sortId DESC")
    fun getPagingSource(
        accountKey: MicroBlogKey,
        conversationKey: MicroBlogKey
    ): PagingSource<Int, DbDirectMessageWithMedia>

    @Delete
    suspend fun delete(data: DbDirectMessage): DbDirectMessage

    @Query("DELETE FROM direct_message WHERE accountKey == :accountKey AND conversationKey == :conversationKey")
    suspend fun clearAll(accountKey: MicroBlogKey, conversationKey: MicroBlogKey)
}
