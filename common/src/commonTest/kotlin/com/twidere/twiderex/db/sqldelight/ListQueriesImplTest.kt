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

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import com.twidere.twiderex.base.BaseCacheDatabaseTest
import com.twidere.twiderex.dataprovider.mapper.toUi
import com.twidere.twiderex.db.sqldelight.transform.toDbList
import com.twidere.twiderex.mock.model.mockIListModel
import com.twidere.twiderex.model.MicroBlogKey
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class ListQueriesImplTest : BaseCacheDatabaseTest() {
    private val accountKey = MicroBlogKey.twitter("account")
    @Test
    fun insert_ReplaceWhenPrimaryKeyEquals() = runBlocking {
        val insert = mockIListModel("insert").toUi(accountKey)
        database.listQueries.insert(insert.toDbList())
        assertEquals(
            "insert",
            database.listQueries.findWithListKey(
                accountKey = insert.accountKey,
                listKey = insert.listKey
            ).executeAsOneOrNull()?.title
        )
        database.listQueries.insert(insert.toDbList().copy(title = "replace"))
        assertEquals(
            "replace",
            database.listQueries.findWithListKey(
                accountKey = insert.accountKey,
                listKey = insert.listKey
            ).executeAsOneOrNull()?.title
        )
    }

    @Test
    fun getPagingList_ReturnResultsWithGiveLimitAndOffset() = runBlocking {
        val list = listOf(
            mockIListModel(name = "1"),
            mockIListModel(name = "2"),
            mockIListModel(name = "3"),
            mockIListModel(name = "4"),
        ).map { it.toUi(accountKey) }
        database.listQueries.transaction {
            list.forEach { database.listQueries.insert(it.toDbList()) }
        }
        assertEquals(4, database.listQueries.getPagingCount(accountKey = accountKey).executeAsOne())
        assertEquals(2, database.listQueries.getPagingList(accountKey = accountKey, limit = 2, offSet = 0).executeAsList().size)
        assertEquals("3", database.listQueries.getPagingList(accountKey = accountKey, limit = 2, offSet = 2).executeAsList().first().title)
    }

    @Test
    fun delete_DeleteListWithGiveUniqueIndex() = runBlocking {
        val insert = mockIListModel("insert").toUi(accountKey)
        database.listQueries.insert(insert.toDbList())
        val flow = database.listQueries.findWithListKey(accountKey = insert.accountKey, listKey = insert.listKey).asFlow().mapToOneOrNull()
        assertNotNull(flow.firstOrNull())
        database.listQueries.delete(accountKey = insert.accountKey, listKey = insert.listKey)
        assertNull(flow.firstOrNull())
    }

    @Test
    fun clearAll_DeleteAllListMatchesAccountKey() = runBlocking {
        val list = mutableListOf(
            mockIListModel(name = "1"),
            mockIListModel(name = "2"),
            mockIListModel(name = "3"),
            mockIListModel(name = "4"),
        ).map { it.toUi(accountKey) }
        database.listQueries.transaction {
            list.forEach { database.listQueries.insert(it.toDbList()) }
        }
        val otherAcct = MicroBlogKey.twitter("other")
        database.listQueries.insert(mockIListModel().toUi(otherAcct).toDbList())
        assertEquals(4, database.listQueries.getPagingCount(accountKey = accountKey).executeAsOne())
        assertEquals(1, database.listQueries.getPagingCount(accountKey = otherAcct).executeAsOne())

        database.listQueries.clearAll(accountKey = accountKey)

        assertEquals(0, database.listQueries.getPagingCount(accountKey = accountKey).executeAsOne())
        assertEquals(1, database.listQueries.getPagingCount(accountKey = otherAcct).executeAsOne())
    }
}
