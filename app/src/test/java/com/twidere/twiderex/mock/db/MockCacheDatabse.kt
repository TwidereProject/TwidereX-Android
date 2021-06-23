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

import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.db.dao.DirectMessageConversationDao
import com.twidere.twiderex.db.dao.DirectMessageDao
import com.twidere.twiderex.db.dao.ListsDao
import com.twidere.twiderex.db.dao.MediaDao
import com.twidere.twiderex.db.dao.NotificationCursorDao
import com.twidere.twiderex.db.dao.PagingTimelineDao
import com.twidere.twiderex.db.dao.ReactionDao
import com.twidere.twiderex.db.dao.StatusDao
import com.twidere.twiderex.db.dao.StatusReferenceDao
import com.twidere.twiderex.db.dao.TrendDao
import com.twidere.twiderex.db.dao.TrendHistoryDao
import com.twidere.twiderex.db.dao.UrlEntityDao
import com.twidere.twiderex.db.dao.UserDao

class MockCacheDatabase : CacheDatabase() {
    override fun statusDao(): StatusDao {
        TODO("Not yet implemented")
    }

    private val mediaDao = MockMediaDao()
    override fun mediaDao(): MediaDao {
        return mediaDao
    }

    private val userDao = MockUserDao()
    override fun userDao(): UserDao {
        return userDao
    }

    override fun reactionDao(): ReactionDao {
        TODO("Not yet implemented")
    }

    override fun pagingTimelineDao(): PagingTimelineDao {
        TODO("Not yet implemented")
    }

    private val urlEntityDao = MockUrlEntityDao()
    override fun urlEntityDao(): UrlEntityDao {
        return urlEntityDao
    }

    override fun statusReferenceDao(): StatusReferenceDao {
        TODO("Not yet implemented")
    }

    private val listsDao = MockListsDao()
    override fun listsDao(): ListsDao {
        return listsDao
    }

    override fun notificationCursorDao(): NotificationCursorDao {
        TODO("Not yet implemented")
    }

    private val trendDao = MockTrendDao()
    override fun trendDao(): TrendDao {
        return trendDao
    }

    private val trendHistoryDao = MockTrendHistoryDao()
    override fun trendHistoryDao(): TrendHistoryDao {
        return trendHistoryDao
    }

    private val conversationDao = MockDirectMessageConversationDao()
    override fun directMessageConversationDao(): DirectMessageConversationDao {
        return conversationDao
    }

    private val dmDao = MockDirectMessageDao()
    override fun directMessageDao(): DirectMessageDao {
        return dmDao
    }

    override fun createOpenHelper(config: DatabaseConfiguration?): SupportSQLiteOpenHelper {
        TODO("Not yet implemented")
    }

    override fun createInvalidationTracker(): InvalidationTracker {
        return InvalidationTracker(this, "mock")
    }

    override fun clearAllTables() {
        TODO("Not yet implemented")
    }
}
