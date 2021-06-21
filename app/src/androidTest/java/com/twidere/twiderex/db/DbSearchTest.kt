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
package com.twidere.twiderex.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.twidere.twiderex.db.dao.SearchDao
import com.twidere.twiderex.db.model.DbSearch
import com.twidere.twiderex.model.MicroBlogKey
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class DbSearchTest {
    private lateinit var appDatabase: AppDatabase
    private val twitterAccountKey = MicroBlogKey.twitter("123")
    private val mastodonAccountKey = MicroBlogKey("456", "mastodon.com")

    private val searches = mutableListOf<DbSearch>()
    private lateinit var searchDao: SearchDao
    private val twitterSearchCount = 10
    private val mastodonSearchCount = 10

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        appDatabase = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
            .build()
        searchDao = appDatabase.searchDao()
        for (i in 0 until twitterSearchCount) {
            searches.add(
                DbSearch(
                    _id = UUID.randomUUID().toString(),
                    content = "twitter $i",
                    lastActive = System.currentTimeMillis(),
                    saved = i % 2 == 0,
                    accountKey = twitterAccountKey
                )
            )
        }
        for (i in 0 until mastodonSearchCount) {
            searches.add(
                DbSearch(
                    _id = UUID.randomUUID().toString(),
                    content = "mastodon $i",
                    lastActive = System.currentTimeMillis(),
                    saved = i % 2 == 0,
                    accountKey = mastodonAccountKey
                )
            )
        }

        runBlocking {
            searchDao.insertAll(searches)
        }
    }

    @After
    fun tearDown() {
        appDatabase.close()
    }

    @Test
    fun getAll_returnResultByAccountKey() = runBlocking {
        val result = searchDao.getAll(twitterAccountKey)
        val observer = Observer<List<DbSearch>?> { }
        result.observeForever(observer)
        Assert.assertEquals(twitterSearchCount, result.value?.size)
        result.value?.forEach {
            assert(it.content.startsWith("twitter"))
        } ?: assert(false)
    }

    @Test
    fun getAllHistory_returnResultsMatchAccountKeyAndNotSaved() = runBlocking {
        val result = searchDao.getAllHistory(mastodonAccountKey)
        val observer = Observer<List<DbSearch>?> { }
        result.observeForever(observer)
        result.value?.forEach {
            assert(it.content.startsWith("mastodon"))
            assert(!it.saved)
        } ?: assert(false)
    }

    @Test
    fun getAllSaved_returnResultsMatchAccountKeyAndSaved() = runBlocking {
        val result = searchDao.getAllSaved(twitterAccountKey)
        val observer = Observer<List<DbSearch>?> { }
        result.observeForever(observer)
        result.value?.forEach {
            assert(it.content.startsWith("twitter"))
            assert(it.saved)
        } ?: assert(false)
    }

    @Test
    fun getSearch_returnResultsMatchContentAndAccountKey() = runBlocking {
        val result = searchDao.get("twitter 0", twitterAccountKey)
        Assert.assertEquals("twitter 0", result?.content)
        val errorResult = searchDao.get("twitter 0", mastodonAccountKey)
        assert(errorResult == null)
    }

    @Test
    fun deleteSearch_getNullAfterDelete() = runBlocking {
        val result = searchDao.get("twitter 0", twitterAccountKey)
        Assert.assertEquals("twitter 0", result?.content)
        searchDao.remove(result!!)
        assert(searchDao.get("twitter 0", twitterAccountKey) == null)
    }

    @Test
    fun clearHistory_deleteSearchNotSaved() = runBlocking {
        searchDao.clear()
        val twitterResult = searchDao.getAll(twitterAccountKey)
        val mastodonResult = searchDao.getAll(mastodonAccountKey)
        val observer = Observer<List<DbSearch>?> { }
        twitterResult.observeForever(observer)
        mastodonResult.observeForever(observer)
        twitterResult.value?.forEach {
            assert(it.saved)
        } ?: assert(false)

        mastodonResult.value?.forEach {
            assert(it.saved)
        } ?: assert(false)
    }
}
