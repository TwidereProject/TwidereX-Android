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
import com.twidere.twiderex.db.sqldelight.transform.toDbDMConversation
import com.twidere.twiderex.mock.model.mockIDirectMessage
import com.twidere.twiderex.mock.model.mockIUser
import com.twidere.twiderex.mock.model.toConversation
import com.twidere.twiderex.model.MicroBlogKey
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class DMConversationQueriesImplTest : BaseCacheDatabaseTest() {
    @Test
    fun insertAll_ReplaceWhenUniqueIndexEquals() = runBlocking {
        val accountKey = MicroBlogKey.twitter("accountId")
        val insert = mockIDirectMessage(accountId = accountKey.id, otherUserID = "other")
            .toUi(accountKey, mockIUser(id = "other").toUi(accountKey)).toConversation().toDbDMConversation()
        database.dMConversationQueries.insert(insert.copy(conversationName = "insert"))
        assertEquals("insert", database.dMConversationQueries.findWithConversationKey(accountKey = accountKey, conversationKey = insert.conversationKey).executeAsOneOrNull()?.conversationName)
        database.dMConversationQueries.insert(insert.copy(conversationName = "replace"))
        assertEquals("replace", database.dMConversationQueries.findWithConversationKey(accountKey = accountKey, conversationKey = insert.conversationKey).executeAsOneOrNull()?.conversationName)
    }

    @Test
    fun delete_DeleteWithUniqueIndex() = runBlocking {
        val accountKey = MicroBlogKey.twitter("accountId")
        val insert = mockIDirectMessage(accountId = accountKey.id, inCome = false).toUi(accountKey, mockIUser(id = accountKey.id).toUi(accountKey))
            .toConversation().toDbDMConversation()
        database.dMConversationQueries.insert(insert)
        database.dMConversationQueries.delete(
            accountKey = insert.accountKey,
            conversationKey = MicroBlogKey.twitter("random"),
        )
        database.dMConversationQueries.delete(
            accountKey = MicroBlogKey.twitter("random"),
            conversationKey = insert.conversationKey,
        )
        assertNotNull(
            database.dMConversationQueries.findWithConversationKey(
                accountKey = insert.accountKey,
                conversationKey = insert.conversationKey,
            ).executeAsOneOrNull()
        )
        database.dMConversationQueries.delete(
            accountKey = insert.accountKey,
            conversationKey = insert.conversationKey,
        )
        assertNull(
            database.dMConversationQueries.findWithConversationKey(
                accountKey = insert.accountKey,
                conversationKey = insert.conversationKey,
            ).executeAsOneOrNull()
        )
    }
}
