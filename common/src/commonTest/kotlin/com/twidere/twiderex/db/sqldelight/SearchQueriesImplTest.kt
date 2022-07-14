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

import com.twidere.twiderex.base.BaseAppDatabaseTest
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.sqldelight.table.Search
import com.twidere.twiderex.sqldelight.table.SearchQueries
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class SearchQueriesImplTest : BaseAppDatabaseTest() {
    private val twitterAccountKey = MicroBlogKey.twitter("123")
    private val mastodonAccountKey = MicroBlogKey("456", "mastodon.com")
    @Test
    fun insertSearch_InsertOrReplaceWhenContentAndAccountKeyEquals(): Unit = runBlocking {
        val query = database.searchQueries
        val insert = Search("test", System.currentTimeMillis(), false, accountKey = twitterAccountKey)
        query.insert(insert)
        var result = query.getAll(twitterAccountKey).executeAsList()
        assertEquals(1, result.size)
        assertEquals(false, result.firstOrNull()?.saved)
        query.insert(insert.copy(saved = true))
        result = query.getAll(twitterAccountKey).executeAsList()
        assertEquals(1, result.size)
        assertEquals(true, result.firstOrNull()?.saved)
    }

    @Test
    fun getAll_ReturnResultByAccountKey() = runBlocking {
        val query = database.searchQueries
        query.insertList(5, twitterAccountKey)
        query.insertList(5, mastodonAccountKey)
        val twitterResult = query.getAll(twitterAccountKey).executeAsList()
        assert(twitterResult.isNotEmpty())
        twitterResult.forEach {
            assertEquals(twitterAccountKey, it.accountKey)
        }

        val mastodonResult = query.getAll(mastodonAccountKey).executeAsList()
        assert(mastodonResult.isNotEmpty())
        mastodonResult.forEach {
            assertEquals(mastodonAccountKey, it.accountKey)
        }
    }

    @Test
    fun getHistory_ReturnResultWhichSavedIs0() = runBlocking {
        val query = database.searchQueries
        query.insertList(5, twitterAccountKey, saved = false)
        query.insertList(5, mastodonAccountKey, saved = true)
        val history = query.getHistories(twitterAccountKey).executeAsList()
        assert(history.isNotEmpty())
        history.forEach {
            assertEquals(false, it.saved)
        }
    }

    @Test
    fun getSaved_ReturnResultWhichSavedIs1() = runBlocking {
        val query = database.searchQueries
        query.insertList(5, twitterAccountKey, saved = false)
        query.insertList(5, mastodonAccountKey, saved = true)
        val saved = query.getSaved(mastodonAccountKey).executeAsList()
        assert(saved.isNotEmpty())
        saved.forEach {
            assertEquals(true, it.saved)
        }
    }

    @Test
    fun removeSearch() = runBlocking {
        val query = database.searchQueries
        query.insertList(5, twitterAccountKey, saved = false)
        query.insert(Search(content = "remove", lastActive = System.currentTimeMillis(), saved = false, accountKey = twitterAccountKey))
        assertEquals("remove", query.get(content = "remove", accountKey = twitterAccountKey).executeAsOneOrNull()?.content)
        query.remove(content = "remove", accountKey = twitterAccountKey)
        assertNull(query.get(content = "remove", accountKey = twitterAccountKey).executeAsOneOrNull())
    }

    @Test
    fun clear_clearAllHistories() = runBlocking {
        val query = database.searchQueries
        query.insertList(5, twitterAccountKey, saved = false)
        query.insertList(5, mastodonAccountKey, saved = true)
        assertEquals(5, query.getAll(mastodonAccountKey).executeAsList().size)
        assertEquals(5, query.getAll(twitterAccountKey).executeAsList().size)
        query.clear()
        assertEquals(0, query.getAll(twitterAccountKey).executeAsList().size)
        val result = query.getAll(mastodonAccountKey).executeAsList()
        assertEquals(5, result.size)
        result.forEach {
            assertEquals(true, it.saved)
        }
    }

    private fun SearchQueries.insertList(count: Int, accountKey: MicroBlogKey, saved: Boolean = false) {
        transaction {
            for (i in 0 until count) {
                insert(Search("test $i", lastActive = System.currentTimeMillis(), saved = saved, accountKey = accountKey))
            }
        }
    }
}
