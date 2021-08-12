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
package com.twidere.twiderex.mock.db

import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.db.dao.DirectMessageConversationDao
import com.twidere.twiderex.db.dao.DirectMessageEventDao
import com.twidere.twiderex.db.dao.ListsDao
import com.twidere.twiderex.db.dao.MediaDao
import com.twidere.twiderex.db.dao.NotificationCursorDao
import com.twidere.twiderex.db.dao.PagingTimelineDao
import com.twidere.twiderex.db.dao.StatusDao
import com.twidere.twiderex.db.dao.TrendDao
import com.twidere.twiderex.db.dao.UserDao
import com.twidere.twiderex.mock.db.dao.MockMediaDao
import org.jetbrains.annotations.TestOnly

internal class MockCacheDatabase @TestOnly constructor() : CacheDatabase {
    override fun statusDao(): StatusDao {
        TODO("Not yet implemented")
    }

    override fun mediaDao(): MediaDao {
        return MockMediaDao()
    }

    override fun userDao(): UserDao {
        TODO("Not yet implemented")
    }

    override fun pagingTimelineDao(): PagingTimelineDao {
        TODO("Not yet implemented")
    }

    override fun listsDao(): ListsDao {
        TODO("Not yet implemented")
    }

    override fun notificationCursorDao(): NotificationCursorDao {
        TODO("Not yet implemented")
    }

    override fun trendDao(): TrendDao {
        TODO("Not yet implemented")
    }

    override fun directMessageConversationDao(): DirectMessageConversationDao {
        TODO("Not yet implemented")
    }

    override fun directMessageDao(): DirectMessageEventDao {
        TODO("Not yet implemented")
    }

    private var cleared = false
    override suspend fun clearAllTables() {
        cleared = true
    }

    override suspend fun <R> withTransaction(block: suspend () -> R): R {
        return block.invoke()
    }

    fun isAllTablesCleared(): Boolean {
        return cleared
    }
}
