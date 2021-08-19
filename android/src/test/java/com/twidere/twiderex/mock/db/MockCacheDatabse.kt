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
import com.twidere.twiderex.db.dao.ListsDao
import com.twidere.twiderex.db.dao.PagingTimelineDao
import com.twidere.twiderex.db.dao.ReactionDao
import com.twidere.twiderex.db.dao.StatusDao
import com.twidere.twiderex.db.dao.StatusReferenceDao
import com.twidere.twiderex.db.dao.TrendDao
import com.twidere.twiderex.db.dao.TrendHistoryDao
import com.twidere.twiderex.db.dao.UrlEntityDao
import com.twidere.twiderex.db.dao.UserDao
import com.twidere.twiderex.room.db.RoomCacheDatabase
import com.twidere.twiderex.room.db.dao.RoomDirectMessageConversationDao
import com.twidere.twiderex.room.db.dao.RoomDirectMessageEventDao
import com.twidere.twiderex.room.db.dao.RoomMediaDao
import com.twidere.twiderex.room.db.dao.RoomNotificationCursorDao
import java.util.ArrayDeque
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class MockRoomCacheDatabase : RoomCacheDatabase() {
    override fun getTransactionExecutor(): Executor {
        return object : Executor {
            private var mExecutor = Executors.newSingleThreadExecutor()
            private val mTasks = ArrayDeque<Runnable>()
            private var mActive: Runnable? = null

            @Synchronized
            override fun execute(command: Runnable) {
                mTasks.offer(
                    Runnable {
                        try {
                            command.run()
                        } finally {
                            scheduleNext()
                        }
                    }
                )
                if (mActive == null) {
                    scheduleNext()
                }
            }

            @Synchronized
            fun scheduleNext() {
                if (mTasks.poll().also { mActive = it } != null) {
                    mExecutor.execute(mActive)
                }
            }
        }
    }
    override fun beginTransaction() {
    }

    override fun setTransactionSuccessful() {
    }

    override fun endTransaction() {
    }

    override fun statusDao(): StatusDao {
        TODO("Not yet implemented")
    }

    private val mediaDao = MockMediaDao()
    override fun mediaDao(): RoomMediaDao {
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

    override fun notificationCursorDao(): RoomNotificationCursorDao {
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
    override fun directMessageConversationDao(): RoomDirectMessageConversationDao {
        return conversationDao
    }

    private val dmDao = MockDirectMessageEventDao()
    override fun directMessageDao(): RoomDirectMessageEventDao {
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
