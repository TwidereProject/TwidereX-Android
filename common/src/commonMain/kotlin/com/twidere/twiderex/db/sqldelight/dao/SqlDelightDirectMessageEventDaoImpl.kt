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
package com.twidere.twiderex.db.sqldelight.dao

import androidx.paging.PagingSource
import com.twidere.twiderex.db.dao.DirectMessageEventDao
import com.twidere.twiderex.db.sqldelight.model.DbDMEventWithAttachments.Companion.saveToDb
import com.twidere.twiderex.db.sqldelight.model.DbDMEventWithAttachments.Companion.withAttachments
import com.twidere.twiderex.db.sqldelight.paging.QueryPagingSource
import com.twidere.twiderex.db.sqldelight.query.flatMap
import com.twidere.twiderex.db.sqldelight.transform.toDbEventWithAttachments
import com.twidere.twiderex.db.sqldelight.transform.toUi
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiDMEvent
import com.twidere.twiderex.sqldelight.SqlDelightCacheDatabase
import kotlinx.coroutines.Dispatchers

internal class SqlDelightDirectMessageEventDaoImpl(
    private val database: SqlDelightCacheDatabase
) : DirectMessageEventDao {
    private val dmEventQueries = database.dMEventQueries
    override fun getPagingSource(
        accountKey: MicroBlogKey,
        conversationKey: MicroBlogKey
    ): PagingSource<Int, UiDMEvent> {
        return QueryPagingSource(
            countQuery = dmEventQueries.getMessagesPagingCount(accountKey = accountKey, conversationKey = conversationKey),
            transacter = dmEventQueries,
            dispatcher = Dispatchers.IO,
            queryProvider = { limit, offset, _ ->
                dmEventQueries.getMessagesPagingList(
                    accountKey = accountKey,
                    conversationKey = conversationKey,
                    limit = limit,
                    offset = offset
                ).flatMap {
                    it.withAttachments(database).toUi()
                }
            }
        )
    }

    override suspend fun findWithMessageKey(
        accountKey: MicroBlogKey,
        conversationKey: MicroBlogKey,
        messageKey: MicroBlogKey
    ): UiDMEvent? = dmEventQueries.findWithMessageKey(
        accountKey = accountKey,
        conversationKey = conversationKey,
        messageKey = messageKey
    ).executeAsOneOrNull()?.withAttachments(database)?.toUi()

    override suspend fun delete(message: UiDMEvent) {
        dmEventQueries.delete(
            accountKey = message.accountKey,
            conversationKey = message.conversationKey,
            messageKey = message.messageKey
        )
    }

    override suspend fun getMessageCount(
        accountKey: MicroBlogKey,
        conversationKey: MicroBlogKey
    ): Long {
        return dmEventQueries.getMessagesPagingCount(accountKey = accountKey, conversationKey = conversationKey).executeAsOne()
    }

    override suspend fun insertAll(events: List<UiDMEvent>) {
        events.map { it.toDbEventWithAttachments() }.saveToDb(database)
    }
}
