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
import com.twidere.twiderex.db.model.DbDirectMessageConversation
import com.twidere.twiderex.db.model.DbDirectMessageConversationWithMessage
import com.twidere.twiderex.model.MicroBlogKey

@Dao
interface DirectMessageConversationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(trends: List<DbDirectMessageConversation>)

    // use join to found latest msg under each conversation, then use relation to found the conversation
    @Transaction
    @Query(
        "SELECT * FROM direct_message AS table1 " +
            "JOIN (SELECT conversationKey, max(sortId) as sortId FROM direct_message WHERE accountKey == :accountKey) AS table2 " +
            "ON table1.conversationKey = table2.conversationKey AND table1.sortId = table2.sortId " +
            "WHERE table1.accountKey == :accountKey"
    )
    suspend fun find(accountKey: MicroBlogKey): List<DbDirectMessageConversationWithMessage>

    @Transaction
    @Query(
        "SELECT * FROM direct_message AS table1 " +
            "JOIN (SELECT conversationKey, max(sortId) as sortId FROM direct_message WHERE accountKey == :accountKey) AS table2 " +
            "ON table1.conversationKey = table2.conversationKey AND table1.sortId = table2.sortId " +
            "WHERE table1.accountKey == :accountKey"
    )
    fun getPagingSource(
        accountKey: MicroBlogKey,
    ): PagingSource<Int, DbDirectMessageConversationWithMessage>

    @Delete
    suspend fun delete(data: DbDirectMessageConversation)

    @Query("DELETE FROM dm_conversation WHERE accountKey == :accountKey")
    suspend fun clearAll(accountKey: MicroBlogKey)
}
