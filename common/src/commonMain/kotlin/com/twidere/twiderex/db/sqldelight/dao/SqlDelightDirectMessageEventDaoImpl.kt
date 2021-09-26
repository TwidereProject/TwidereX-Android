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
package com.twidere.twiderex.db.sqldelight.dao

import androidx.paging.PagingSource
import com.twidere.twiderex.db.dao.DirectMessageEventDao
import com.twidere.twiderex.db.sqldelight.model.DbDMEventWithAttachments
import com.twidere.twiderex.db.sqldelight.transform.toDbEventWithAttachments
import com.twidere.twiderex.db.sqldelight.transform.toUi
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiDMEvent
import com.twidere.twiderex.sqldelight.table.DMEvent
import com.twidere.twiderex.sqldelight.table.DMEventQueries
import com.twidere.twiderex.sqldelight.table.MediaQueries
import com.twidere.twiderex.sqldelight.table.UrlEntityQueries
import com.twidere.twiderex.sqldelight.table.UserQueries

internal class SqlDelightDirectMessageEventDaoImpl(
    private val dmEventQueries: DMEventQueries,
    private val urlEntityQueries: UrlEntityQueries,
    private val mediaQueries: MediaQueries,
    private val userQueries: UserQueries
) : DirectMessageEventDao {
    override fun getPagingSource(
        accountKey: MicroBlogKey,
        conversationKey: MicroBlogKey
    ): PagingSource<Int, UiDMEvent> {
        TODO("Not yet implemented")
    }

    override suspend fun findWithMessageKey(
        accountKey: MicroBlogKey,
        conversationKey: MicroBlogKey,
        messageKey: MicroBlogKey
    ): UiDMEvent? = dmEventQueries.findWithMessageKey(
        accountKey = accountKey,
        conversationKey = conversationKey,
        messageKey = messageKey
    ).executeAsOneOrNull()?.withAttachments()?.toUi()

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
        return dmEventQueries.getMessageCount(accountKey = accountKey, conversationKey = conversationKey).executeAsOne()
    }

    override suspend fun insertAll(events: List<UiDMEvent>) {
        dmEventQueries.transaction {
            events.map { it.toDbEventWithAttachments() }.let { list ->
                list.forEach {
                    dmEventQueries.insert(it.event)
                    it.media.forEach { media -> mediaQueries.insert(media) }
                    userQueries.insert(it.sender)
                    it.url.forEach { url -> urlEntityQueries.insert(url) }
                }
            }
        }
    }

    private fun DMEvent.withAttachments(): DbDMEventWithAttachments {
        return dmEventQueries.transactionWithResult {
            DbDMEventWithAttachments(
                event = this@withAttachments,
                url = urlEntityQueries.findByBelongToKey(messageKey).executeAsList(),
                media = mediaQueries.findMediaByBelongToKey(messageKey).executeAsList(),
                sender = userQueries.findWithUserKey(senderAccountKey).executeAsOne()
            )
        }
    }
}
