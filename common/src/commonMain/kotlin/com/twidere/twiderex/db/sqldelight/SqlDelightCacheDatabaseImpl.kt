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
package com.twidere.twiderex.db.sqldelight

import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.db.sqldelight.dao.SqlDelightDirectMessageConversationDaoImpl
import com.twidere.twiderex.db.sqldelight.dao.SqlDelightDirectMessageEventDaoImpl
import com.twidere.twiderex.db.sqldelight.dao.SqlDelightListsDaoImpl
import com.twidere.twiderex.db.sqldelight.dao.SqlDelightMediaDaoImpl
import com.twidere.twiderex.db.sqldelight.dao.SqlDelightNotificationCursorDaoImpl
import com.twidere.twiderex.db.sqldelight.dao.SqlDelightPagingTimelineDaoImpl
import com.twidere.twiderex.db.sqldelight.dao.SqlDelightStatusDaoImpl
import com.twidere.twiderex.db.sqldelight.dao.SqlDelightTrendDaoImpl
import com.twidere.twiderex.db.sqldelight.dao.SqlDelightUserDaoImpl
import com.twidere.twiderex.sqldelight.SqlDelightCacheDatabase
import kotlinx.coroutines.runBlocking

internal class SqlDelightCacheDatabaseImpl(private val database: SqlDelightCacheDatabase) : CacheDatabase {
    private val statusDao = SqlDelightStatusDaoImpl(database)
    override fun statusDao() = statusDao

    private val mediaDao = SqlDelightMediaDaoImpl(database.mediaQueries)
    override fun mediaDao() = mediaDao

    private val userDao = SqlDelightUserDaoImpl(database.userQueries)
    override fun userDao() = userDao

    private val pagingTimelineDao = SqlDelightPagingTimelineDaoImpl(database)
    override fun pagingTimelineDao() = pagingTimelineDao

    private val listDao = SqlDelightListsDaoImpl(database.listQueries)
    override fun listsDao() = listDao

    private val notificationCursorDao = SqlDelightNotificationCursorDaoImpl(database.notificationCursorQueries)
    override fun notificationCursorDao() = notificationCursorDao

    private val trendDao = SqlDelightTrendDaoImpl(database)
    override fun trendDao() = trendDao

    private val dmConversationDao = SqlDelightDirectMessageConversationDaoImpl(database)
    override fun directMessageConversationDao() = dmConversationDao

    private val dmEventDao = SqlDelightDirectMessageEventDaoImpl(database)
    override fun directMessageDao() = dmEventDao

    override suspend fun clearAllTables() {
        database.cacheDropQueries.clearAllTables()
    }

    override suspend fun <R> withTransaction(block: suspend () -> R): R {
        // TODO find a way to handle transaction
        return database.transactionWithResult { runBlocking { block.invoke() } }
    }
}
