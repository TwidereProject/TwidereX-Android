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
import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.twidere.services.mastodon.model.TrendHistory
import com.twidere.twiderex.db.mapper.toDbTrend
import com.twidere.twiderex.db.model.DbTrendWithHistory
import com.twidere.twiderex.db.model.saveToDb
import com.twidere.twiderex.model.MicroBlogKey
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.Executors

typealias TwitterTrend = com.twidere.services.twitter.model.Trend
typealias MastodonTrend = com.twidere.services.mastodon.model.Trend

@RunWith(AndroidJUnit4::class)
class DbTrendTest {
    private lateinit var cacheDatabase: CacheDatabase
    private val twitterAccountKey = MicroBlogKey.twitter("123")
    private val mastodonAccountKey = MicroBlogKey("456", "mastodon.com")
    private val trends = mutableListOf<DbTrendWithHistory>()

    private val twitterTrendCount = 10
    private val mastodonTrendCount = 10

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        cacheDatabase = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), CacheDatabase::class.java)
            .setTransactionExecutor(Executors.newSingleThreadExecutor()).build()
        for (i in 0 until twitterTrendCount) {
            trends.add(
                TwitterTrend(
                    name = "tweet $i",
                    url = "https://tweet $i"
                ).toDbTrend(twitterAccountKey)
            )
        }
        for (i in 0 until mastodonTrendCount) {
            trends.add(
                MastodonTrend(
                    name = "mastodon $i",
                    url = "https://mastodon $i",
                    history = mutableListOf(
                        TrendHistory(
                            accounts = "10",
                            uses = "20",
                            day = "${System.currentTimeMillis()}"
                        )
                    )
                ).toDbTrend(mastodonAccountKey)
            )
        }
    }

    @After
    fun tearDown() {
        cacheDatabase.close()
    }

    @Test
    fun findAll_matchTheAccountKeyAndLimit() = runBlocking {
        trends.saveToDb(cacheDatabase)

        val twitterTrends = cacheDatabase.trendDao().find(twitterAccountKey, 5)
        Assert.assertEquals(5, twitterTrends.size)
        assert(twitterTrends[0].trend.displayName.startsWith("tweet"))
        assert(twitterTrends[0].trend.url.startsWith("https://tweet"))
        Assert.assertEquals(0, twitterTrends[0].history.size)

        val mastodonTrends = cacheDatabase.trendDao().find(mastodonAccountKey, 7)
        Assert.assertEquals(7, mastodonTrends.size)
        assert(mastodonTrends[0].trend.displayName.startsWith("mastodon"))
        assert(mastodonTrends[0].trend.url.startsWith("https://mastodon"))
        // check if the history trend key is the same to trend
        Assert.assertEquals(mastodonTrends[0].trend.trendKey, mastodonTrends[0].history[0].trendKey)
    }

    @Test
    fun testPagingSource() = runBlocking {
        trends.saveToDb(cacheDatabase)
        val pagingSource = cacheDatabase.trendDao().getPagingSource(twitterAccountKey)
        val resultFirst = pagingSource.load(PagingSource.LoadParams.Refresh(null, loadSize = 2, false))
        Assert.assertEquals(2, (resultFirst as PagingSource.LoadResult.Page).data.size)
        Assert.assertEquals("tweet 0", resultFirst.data[0].trend.displayName)

        val resultLoadMore = pagingSource.load(PagingSource.LoadParams.Append(resultFirst.nextKey ?: 2, loadSize = 2, false))
        Assert.assertEquals(2, (resultLoadMore as PagingSource.LoadResult.Page).data.size)
        Assert.assertEquals("tweet 2", resultLoadMore.data[0].trend.displayName)
    }

    @Test
    fun clearAll_matchTheAccountKey() = runBlocking {
        trends.saveToDb(cacheDatabase)
        // make sure twitter trends is in database
        Assert.assertEquals(twitterTrendCount, cacheDatabase.trendDao().find(twitterAccountKey, twitterTrendCount).size)
        cacheDatabase.trendDao().clearAll(twitterAccountKey)
        // check whether twitter trends has been deleted
        Assert.assertEquals(0, cacheDatabase.trendDao().find(twitterAccountKey, twitterTrendCount).size)

        // make sure mastodon trends and history is in database
        Assert.assertEquals(mastodonTrendCount, cacheDatabase.trendDao().find(mastodonAccountKey, mastodonTrendCount).size)
        Assert.assertEquals(1, cacheDatabase.trendDao().find(mastodonAccountKey, mastodonTrendCount)[0].history.size)
        // clear history
        cacheDatabase.trendHistoryDao().clearAll(mastodonAccountKey)
        Assert.assertEquals(0, cacheDatabase.trendDao().find(mastodonAccountKey, mastodonTrendCount)[0].history.size)
    }
}
