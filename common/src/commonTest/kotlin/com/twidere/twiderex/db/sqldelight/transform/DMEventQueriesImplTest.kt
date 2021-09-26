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
package com.twidere.twiderex.db.sqldelight.transform

import com.twidere.twiderex.base.BaseCacheDatabaseTest
import com.twidere.twiderex.dataprovider.mapper.toUi
import com.twidere.twiderex.mock.model.mockIDirectMessage
import com.twidere.twiderex.mock.model.mockIUser
import com.twidere.twiderex.model.MicroBlogKey
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class DMEventQueriesImplTest : BaseCacheDatabaseTest() {
    @Test
    fun insert_ReplaceWhenUniqueIndexEquals() = runBlocking {
        val accountKey = MicroBlogKey.twitter("accountId")
        val insert = mockIDirectMessage(accountId = accountKey.id, inCome = false).toUi(accountKey, mockIUser(id = accountKey.id).toUi(accountKey))
        database.dMEventQueries.insert(insert.copy(htmlText = "insert").toDbEventWithAttachments().event)
        assertEquals(
            "insert",
            database.dMEventQueries.findWithMessageKey(
                accountKey = insert.accountKey,
                conversationKey = insert.conversationKey,
                messageKey = insert.messageKey
            ).executeAsOneOrNull()?.htmlText
        )

        database.dMEventQueries.insert(insert.copy(htmlText = "replace").toDbEventWithAttachments().event)

        assertEquals(
            "replace",
            database.dMEventQueries.findWithMessageKey(
                accountKey = insert.accountKey,
                conversationKey = insert.conversationKey,
                messageKey = insert.messageKey
            ).executeAsOneOrNull()?.htmlText
        )
    }

    @Test
    fun delete_DeleteWithUniqueIndex() = runBlocking {
        val accountKey = MicroBlogKey.twitter("accountId")
        val insert = mockIDirectMessage(accountId = accountKey.id, inCome = false).toUi(accountKey, mockIUser(id = accountKey.id).toUi(accountKey))
        database.dMEventQueries.insert(insert.toDbEventWithAttachments().event)
        assertNotNull(
            database.dMEventQueries.findWithMessageKey(
                accountKey = insert.accountKey,
                conversationKey = insert.conversationKey,
                messageKey = insert.messageKey
            ).executeAsOneOrNull()
        )
        database.dMEventQueries.delete(
            accountKey = insert.accountKey,
            conversationKey = MicroBlogKey.twitter("random"),
            messageKey = MicroBlogKey.twitter("random"),
        )
        database.dMEventQueries.delete(
            accountKey = MicroBlogKey.twitter("random"),
            conversationKey = insert.conversationKey,
            messageKey = MicroBlogKey.twitter("random"),
        )
        database.dMEventQueries.delete(
            accountKey = MicroBlogKey.twitter("random"),
            conversationKey = MicroBlogKey.twitter("random"),
            messageKey = insert.messageKey,
        )
        assertNotNull(
            database.dMEventQueries.findWithMessageKey(
                accountKey = insert.accountKey,
                conversationKey = insert.conversationKey,
                messageKey = insert.messageKey
            ).executeAsOneOrNull()
        )
        database.dMEventQueries.delete(
            accountKey = insert.accountKey,
            conversationKey = insert.conversationKey,
            messageKey = insert.messageKey
        )
        assertNull(
            database.dMEventQueries.findWithMessageKey(
                accountKey = insert.accountKey,
                conversationKey = insert.conversationKey,
                messageKey = insert.messageKey
            ).executeAsOneOrNull()
        )
    }

    @Test
    fun getMessageCount_ReturnsCountMatchesAccountKeyAndConversationKey() = runBlocking {
        val accountKey = MicroBlogKey.twitter("accountId")
        val insert = mockIDirectMessage(accountId = accountKey.id, inCome = false).toUi(accountKey, mockIUser(id = accountKey.id).toUi(accountKey))
        database.dMEventQueries.insert(insert.copy(messageKey = MicroBlogKey.twitter("1")).toDbEventWithAttachments().event)
        database.dMEventQueries.insert(insert.copy(messageKey = MicroBlogKey.twitter("2")).toDbEventWithAttachments().event)
        database.dMEventQueries.insert(insert.copy(messageKey = MicroBlogKey.twitter("3")).toDbEventWithAttachments().event)

        assertEquals(3, database.dMEventQueries.getMessageCount(accountKey = accountKey, conversationKey = insert.conversationKey).executeAsOne())

        assertEquals(0, database.dMEventQueries.getMessageCount(accountKey = MicroBlogKey.twitter("empty"), conversationKey = insert.conversationKey).executeAsOne())
    }
}
