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
package com.twidere.twiderex.dataprovider.db

import androidx.room.withTransaction
import com.twidere.twiderex.dataprovider.db.dao.DirectMessageConversationDaoImpl
import com.twidere.twiderex.dataprovider.db.dao.DirectMessageEventDaoImpl
import com.twidere.twiderex.dataprovider.db.dao.ListsDaoImpl
import com.twidere.twiderex.dataprovider.db.dao.MediaDaoImpl
import com.twidere.twiderex.dataprovider.db.dao.NotificationCursorDaoImpl
import com.twidere.twiderex.dataprovider.db.dao.PagingTimelineDaoImpl
import com.twidere.twiderex.dataprovider.db.dao.StatusDaoImpl
import com.twidere.twiderex.dataprovider.db.dao.TrendDaoImpl
import com.twidere.twiderex.dataprovider.db.dao.UserDaoImpl
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.room.db.RoomCacheDatabase

internal class CacheDatabaseImpl(private val roomCacheDatabase: RoomCacheDatabase) : CacheDatabase {
    private val statusDao = StatusDaoImpl(roomCacheDatabase)
    override fun statusDao() = statusDao

    private val mediaDao = MediaDaoImpl(roomCacheDatabase)
    override fun mediaDao() = mediaDao

    private val userDao = UserDaoImpl(roomCacheDatabase)
    override fun userDao() = userDao

    private val pagingTimelineDao = PagingTimelineDaoImpl(roomCacheDatabase)
    override fun pagingTimelineDao() = pagingTimelineDao

    private val listsDao = ListsDaoImpl(roomCacheDatabase)
    override fun listsDao() = listsDao

    private val notificationCursorDao = NotificationCursorDaoImpl(roomCacheDatabase)
    override fun notificationCursorDao() = notificationCursorDao

    private val trendDao = TrendDaoImpl(roomCacheDatabase)
    override fun trendDao() = trendDao

    private val dmConversationDao = DirectMessageConversationDaoImpl(roomCacheDatabase)
    override fun directMessageConversationDao() = dmConversationDao

    private val dmEventDao = DirectMessageEventDaoImpl(roomCacheDatabase)
    override fun directMessageDao() = dmEventDao

    override suspend fun clearAllTables() = roomCacheDatabase.clearAllTables()

    override suspend fun <R> withTransaction(block: suspend () -> R) = roomCacheDatabase.withTransaction(block)
}
