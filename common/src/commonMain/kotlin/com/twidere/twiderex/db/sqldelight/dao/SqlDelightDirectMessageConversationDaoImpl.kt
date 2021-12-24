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
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import com.twidere.twiderex.db.dao.DirectMessageConversationDao
import com.twidere.twiderex.db.sqldelight.model.DbDMConversationWithEvent.Companion.toDbDMConversationWithEvent
import com.twidere.twiderex.db.sqldelight.paging.QueryPagingSource
import com.twidere.twiderex.db.sqldelight.query.flatMap
import com.twidere.twiderex.db.sqldelight.transform.toDbDMConversation
import com.twidere.twiderex.db.sqldelight.transform.toUi
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiDMConversation
import com.twidere.twiderex.model.ui.UiDMConversationWithLatestMessage
import com.twidere.twiderex.sqldelight.SqlDelightCacheDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class SqlDelightDirectMessageConversationDaoImpl(
    private val database: SqlDelightCacheDatabase
) : DirectMessageConversationDao {
    private val dmConversationQueries = database.dMConversationQueries
    override fun getPagingSource(accountKey: MicroBlogKey): PagingSource<Int, UiDMConversationWithLatestMessage> {
        return QueryPagingSource(
            countQuery = database.dMEventQueries.getLatestMessagesInEachConversationPagingCount(accountKey = accountKey),
            transacter = database.dMEventQueries,
            queryProvider = { limit, offset, _ ->
                database.dMEventQueries.getLatestMessagesInEachConversationPagingList(
                    accountKey = accountKey,
                    limit = limit,
                    offset = offset
                ).flatMap {
                    it.toDbDMConversationWithEvent(database).toUi()
                }
            }
        )
    }

    override fun findWithConversationKeyFlow(
        accountKey: MicroBlogKey,
        conversationKey: MicroBlogKey
    ): Flow<UiDMConversation?> {
        return dmConversationQueries.findWithConversationKey(
            accountKey = accountKey,
            conversationKey = conversationKey
        ).asFlow()
            .mapToOneOrNull()
            .map { it?.toUi() }
    }

    override suspend fun findWithConversationKey(
        accountKey: MicroBlogKey,
        conversationKey: MicroBlogKey
    ): UiDMConversation? {
        return dmConversationQueries.findWithConversationKey(
            accountKey = accountKey,
            conversationKey = conversationKey
        ).executeAsOneOrNull()
            ?.toUi()
    }

    override suspend fun insertAll(listOf: List<UiDMConversation>) {
        dmConversationQueries.transaction {
            listOf.forEach {
                dmConversationQueries.insert(it.toDbDMConversation())
            }
        }
    }

    override suspend fun find(accountKey: MicroBlogKey): List<UiDMConversationWithLatestMessage> {
        return database.dMEventQueries.getLatestMessagesInEachConversation(accountKey = accountKey)
            .executeAsList()
            .map { it.toDbDMConversationWithEvent(database).toUi() }
    }

    override suspend fun delete(conversation: UiDMConversation) {
        dmConversationQueries.transaction {
            dmConversationQueries.delete(accountKey = conversation.accountKey, conversationKey = conversation.conversationKey)
            database.dMEventQueries.clearByConversationKey(accountKey = conversation.accountKey, conversationKey = conversation.conversationKey)
        }
    }
}
