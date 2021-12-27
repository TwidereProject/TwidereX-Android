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
import com.twidere.twiderex.mock.db.dao.MockDirectMessageConversationDao
import com.twidere.twiderex.mock.db.dao.MockDirectMessageEventDao
import com.twidere.twiderex.mock.db.dao.MockListsDao
import com.twidere.twiderex.mock.db.dao.MockMediaDao
import com.twidere.twiderex.mock.db.dao.MockNotificationCursorDao
import com.twidere.twiderex.mock.db.dao.MockPagingTimelineDao
import com.twidere.twiderex.mock.db.dao.MockStatusDao
import com.twidere.twiderex.mock.db.dao.MockTrendDao
import com.twidere.twiderex.mock.db.dao.MockUserDao
import org.jetbrains.annotations.TestOnly

class MockCacheDatabase @TestOnly constructor() : CacheDatabase {
    private val statusDao = MockStatusDao()
    override fun statusDao(): StatusDao {
        return statusDao
    }

    private val mediaDao = MockMediaDao()
    override fun mediaDao(): MediaDao {
        return mediaDao
    }

    private val userDao = MockUserDao()
    override fun userDao(): UserDao {
        return userDao
    }

    private val pagingTimelineDao = MockPagingTimelineDao(statusDao)
    override fun pagingTimelineDao(): PagingTimelineDao {
        return pagingTimelineDao
    }

    private val listsDao = MockListsDao()
    override fun listsDao(): ListsDao {
        return listsDao
    }

    private val notificationCursorDao = MockNotificationCursorDao()
    override fun notificationCursorDao(): NotificationCursorDao {
        return notificationCursorDao
    }

    private val trendDao = MockTrendDao()
    override fun trendDao(): TrendDao {
        return trendDao
    }

    private val dmDao = MockDirectMessageEventDao()
    override fun directMessageDao(): DirectMessageEventDao {
        return dmDao
    }

    private val conversationDao = MockDirectMessageConversationDao(dmDao)
    override fun directMessageConversationDao(): DirectMessageConversationDao {
        return conversationDao
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
