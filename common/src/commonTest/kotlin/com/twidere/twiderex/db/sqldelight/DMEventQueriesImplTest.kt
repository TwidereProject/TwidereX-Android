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

import com.twidere.twiderex.base.BaseCacheDatabaseTest
import com.twidere.twiderex.dataprovider.mapper.toUi
import com.twidere.twiderex.db.sqldelight.transform.toDbEventWithAttachments
import com.twidere.twiderex.mock.model.mockIDirectMessage
import com.twidere.twiderex.mock.model.mockIUser
import com.twidere.twiderex.model.MicroBlogKey
import kotlinx.coroutines.delay
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
    fun getMessagesPagingCount_ReturnsCountMatchesAccountKeyAndConversationKey() = runBlocking {
        val accountKey = MicroBlogKey.twitter("accountId")
        val insert = mockIDirectMessage(accountId = accountKey.id, inCome = false).toUi(accountKey, mockIUser(id = accountKey.id).toUi(accountKey))
        database.dMEventQueries.insert(insert.copy(messageKey = MicroBlogKey.twitter("1")).toDbEventWithAttachments().event)
        database.dMEventQueries.insert(insert.copy(messageKey = MicroBlogKey.twitter("2")).toDbEventWithAttachments().event)
        database.dMEventQueries.insert(insert.copy(messageKey = MicroBlogKey.twitter("3")).toDbEventWithAttachments().event)

        assertEquals(3, database.dMEventQueries.getMessagesPagingCount(accountKey = accountKey, conversationKey = insert.conversationKey).executeAsOne())

        assertEquals(0, database.dMEventQueries.getMessagesPagingCount(accountKey = MicroBlogKey.twitter("empty"), conversationKey = insert.conversationKey).executeAsOne())
    }

    @Test
    fun getLatestMessagesInEachConversation_ReturnsLatestMessagesInEachConversationAndOrderBySortIdDesc() = runBlocking {
        val accountKey = MicroBlogKey.twitter("accountId")
        val conversation1 = mockIDirectMessage(accountId = accountKey.id, inCome = false, otherUserID = "user1").toUi(accountKey, mockIUser(id = accountKey.id).toUi(accountKey))
        val conversation2 = mockIDirectMessage(accountId = accountKey.id, inCome = false, otherUserID = "user2").toUi(accountKey, mockIUser(id = accountKey.id).toUi(accountKey))
        database.dMEventQueries.insert(conversation1.copy(messageKey = MicroBlogKey.twitter("1"), sortId = System.currentTimeMillis()).toDbEventWithAttachments().event)
        delay(1)
        database.dMEventQueries.insert(conversation1.copy(messageKey = MicroBlogKey.twitter("latest"), recipientAccountKey = MicroBlogKey.twitter("user1"), sortId = System.currentTimeMillis()).toDbEventWithAttachments().event)

        database.dMEventQueries.insert(conversation2.copy(messageKey = MicroBlogKey.twitter("1"), sortId = System.currentTimeMillis()).toDbEventWithAttachments().event)
        delay(1)
        database.dMEventQueries.insert(conversation2.copy(messageKey = MicroBlogKey.twitter("latest"), sortId = System.currentTimeMillis()).toDbEventWithAttachments().event)

        val result = database.dMEventQueries.getLatestMessagesInEachConversation(accountKey).executeAsList()

        assertEquals(2, result.size)
        result.forEach {
            print(it.toString())
            assertEquals("latest", it.messageKey.id)
        }
    }

    @Test
    fun getLatestMessagesInEachConversation_PagingCount_ReturnsCountMatchesConversationCount() = runBlocking {
        val accountKey = MicroBlogKey.twitter("accountId")
        val conversation1 = mockIDirectMessage(accountId = accountKey.id, inCome = false, otherUserID = "user1").toUi(accountKey, mockIUser(id = accountKey.id).toUi(accountKey))
        val conversation2 = mockIDirectMessage(accountId = accountKey.id, inCome = false, otherUserID = "user2").toUi(accountKey, mockIUser(id = accountKey.id).toUi(accountKey))
        database.dMEventQueries.insert(conversation1.copy(messageKey = MicroBlogKey.twitter("1"), sortId = System.currentTimeMillis()).toDbEventWithAttachments().event)
        delay(1)
        database.dMEventQueries.insert(conversation1.copy(messageKey = MicroBlogKey.twitter("latest"), recipientAccountKey = MicroBlogKey.twitter("user1"), sortId = System.currentTimeMillis()).toDbEventWithAttachments().event)

        database.dMEventQueries.insert(conversation2.copy(messageKey = MicroBlogKey.twitter("1"), sortId = System.currentTimeMillis()).toDbEventWithAttachments().event)
        delay(1)
        database.dMEventQueries.insert(conversation2.copy(messageKey = MicroBlogKey.twitter("latest"), sortId = System.currentTimeMillis()).toDbEventWithAttachments().event)

        assertEquals(2, database.dMEventQueries.getLatestMessagesInEachConversationPagingCount(accountKey = accountKey).executeAsOne())
    }
}
