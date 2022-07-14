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
package com.twidere.twiderex.db

import com.twidere.twiderex.dataprovider.db.AppDatabaseImpl
import com.twidere.twiderex.db.base.AppDatabaseDaoTest
import com.twidere.twiderex.mock.model.mockUiSearch
import com.twidere.twiderex.model.MicroBlogKey
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals

internal class SearchDaoImplTest : AppDatabaseDaoTest() {
    private val twitterAccountKey = MicroBlogKey.twitter("123")
    private val mastodonAccountKey = MicroBlogKey("456", "mastodon.com")

    @Test
    fun getAll_returnResultByAccountKey(): Unit = runBlocking {
        val appDatabase = AppDatabaseImpl(roomDatabase)
        appDatabase.searchDao().insertAll(
            listOf(
                mockUiSearch(content = "twitter", accountKey = twitterAccountKey),
                mockUiSearch(content = "mastodon", accountKey = mastodonAccountKey)
            )
        )
        val twitterSearch = appDatabase.searchDao().getAll(twitterAccountKey)
        twitterSearch.firstOrNull()?.forEach {
            assertEquals("twitter", it.content)
        } ?: assert(false)

        val mastodonSearch = appDatabase.searchDao().getAll(mastodonAccountKey)
        mastodonSearch.firstOrNull()?.forEach {
            assertEquals("mastodon", it.content)
        } ?: assert(false)
    }

    @Test
    fun getAllHistory_returnResultsMatchAccountKeyAndNotSaved() = runBlocking {
        val appDatabase = AppDatabaseImpl(roomDatabase)
        appDatabase.searchDao().insertAll(
            listOf(
                mockUiSearch(content = "saved", accountKey = twitterAccountKey, saved = true),
                mockUiSearch(content = "not saved", accountKey = twitterAccountKey)
            )
        )
        val result = appDatabase.searchDao().getAllHistory(mastodonAccountKey).firstOrNull()
        result?.forEach {
            assert(!it.saved)
            assertEquals("not saved", it.content)
        } ?: assert(false)
    }

    @Test
    fun getAllSaved_returnResultsMatchAccountKeyAndSaved() = runBlocking {
        val appDatabase = AppDatabaseImpl(roomDatabase)
        appDatabase.searchDao().insertAll(
            listOf(
                mockUiSearch(content = "saved", accountKey = twitterAccountKey, saved = true),
                mockUiSearch(content = "not saved", accountKey = twitterAccountKey)
            )
        )
        val result = appDatabase.searchDao().getAllSaved(twitterAccountKey).firstOrNull()
        result?.forEach {
            assert(it.saved)
            assertEquals("saved", it.content)
        } ?: assert(false)
    }

    @Test
    fun getSearch_returnResultsMatchContentAndAccountKey() = runBlocking {
        val appDatabase = AppDatabaseImpl(roomDatabase)
        appDatabase.searchDao().insertAll(listOf(mockUiSearch(content = "twitter", accountKey = twitterAccountKey)))
        val result = appDatabase.searchDao().get("twitter", twitterAccountKey)
        assertEquals("twitter", result?.content)

        val errorResult = appDatabase.searchDao().get("twitter", mastodonAccountKey)
        assert(errorResult == null)
    }

    @Test
    fun deleteSearch_getNullAfterDelete() = runBlocking {
        val appDatabase = AppDatabaseImpl(roomDatabase)
        appDatabase.searchDao().insertAll(listOf(mockUiSearch(content = "twitter", accountKey = twitterAccountKey)))
        val result = appDatabase.searchDao().get("twitter", twitterAccountKey)
        assertEquals("twitter", result?.content)
        appDatabase.searchDao().remove(result!!)
        assert(appDatabase.searchDao().get("twitter", twitterAccountKey) == null)
    }

    @Test
    fun clearHistory_deleteSearchNotSaved() = runBlocking {
        val appDatabase = AppDatabaseImpl(roomDatabase)
        appDatabase.searchDao().insertAll(
            listOf(
                mockUiSearch(content = "twitter", accountKey = twitterAccountKey),
                mockUiSearch(content = "twitter saved", accountKey = twitterAccountKey, saved = true),
                mockUiSearch(content = "mastodon", accountKey = mastodonAccountKey),
                mockUiSearch(content = "mastodon saved", accountKey = mastodonAccountKey, saved = true)
            )
        )
        appDatabase.searchDao().clear()
        val twitterResult = appDatabase.searchDao().getAll(twitterAccountKey).firstOrNull()
        val mastodonResult = appDatabase.searchDao().getAll(mastodonAccountKey).firstOrNull()
        twitterResult?.forEach {
            assert(it.saved)
            assertEquals("twitter saved", it.content)
        } ?: assert(false)

        mastodonResult?.forEach {
            assert(it.saved)
            assertEquals("mastodon saved", it.content)
        } ?: assert(false)
    }
}
