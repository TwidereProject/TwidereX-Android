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
package com.twidere.twiderex.repository

import com.twidere.twiderex.mock.db.MockAppDatabase
import com.twidere.twiderex.mock.db.MockCacheDatabase
import com.twidere.twiderex.model.MicroBlogKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

internal class SearchRepositoryTest {
    @Test
    fun addToSearchHistoryWhenSavedIsFalse() = runBlocking {
        val accountKey = MicroBlogKey.twitter("test")
        val repo = SearchRepository(MockAppDatabase(), MockCacheDatabase())
        repo.addOrUpgrade(content = "query1", accountKey = accountKey)
        repo.addOrUpgrade(content = "query2", accountKey = accountKey)
        repo.addOrUpgrade(content = "query3", accountKey = accountKey, saved = true)
        val searchHistory = repo.searchHistory(accountKey).first()
        assertEquals(2, searchHistory.size)
        searchHistory.forEach {
            assert(!it.saved)
        }
    }

    @Test
    fun addOrUpdateToSavedSearchWhenSavedIsTrue() = runBlocking {
        val accountKey = MicroBlogKey.twitter("test")
        val repo = SearchRepository(MockAppDatabase(), MockCacheDatabase())
        repo.addOrUpgrade(content = "query1", accountKey = accountKey, saved = false)
        assert(repo.savedSearch(accountKey).first().isEmpty())
        repo.addOrUpgrade(content = "query1", accountKey = accountKey, saved = true)
        repo.addOrUpgrade(content = "query2", accountKey = accountKey, saved = true)
        val savedSearch = repo.savedSearch(accountKey).first()
        assertEquals(2, savedSearch.size)
        savedSearch.forEach {
            assert(it.saved)
        }
    }

    @Test
    fun canNotSetSavedToFalseOnSavedSearch() = runBlocking {
        val accountKey = MicroBlogKey.twitter("test")
        val repo = SearchRepository(MockAppDatabase(), MockCacheDatabase())
        repo.addOrUpgrade(content = "query", accountKey = accountKey, saved = true)
        repo.addOrUpgrade(content = "query", accountKey = accountKey, saved = false)
        val savedSearch = repo.savedSearch(accountKey).first()
        assert(savedSearch.isNotEmpty())
    }

    @Test
    fun deleteFromDbAfterRemove() = runBlocking {
        val accountKey = MicroBlogKey.twitter("test")
        val repo = SearchRepository(MockAppDatabase(), MockCacheDatabase())
        val savedSearchFlow = repo.savedSearch(accountKey)
        val searchHistoryFlow = repo.searchHistory(accountKey)
        repo.addOrUpgrade(content = "query saved", accountKey = accountKey, saved = true)
        repo.addOrUpgrade(content = "query history", accountKey = accountKey, saved = false)
        assert(savedSearchFlow.first().isNotEmpty())
        assert(searchHistoryFlow.first().isNotEmpty())
        repo.remove(repo.savedSearch(accountKey).first().first())
        repo.remove(repo.searchHistory(accountKey).first().first())
        assert(savedSearchFlow.first().isEmpty())
        assert(searchHistoryFlow.first().isEmpty())
    }
}
