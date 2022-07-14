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

import androidx.paging.PagingSource
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.twidere.services.mastodon.model.TrendHistory
import com.twidere.twiderex.dataprovider.db.CacheDatabaseImpl
import com.twidere.twiderex.dataprovider.mapper.toUi
import com.twidere.twiderex.dataprovider.mapper.toUiTrend
import com.twidere.twiderex.db.base.CacheDatabaseDaoTest
import com.twidere.twiderex.mock.model.mockITrend
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiTrend
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals

typealias TwitterTrend = com.twidere.services.twitter.model.Trend
typealias MastodonTrend = com.twidere.services.mastodon.model.Trend

@RunWith(AndroidJUnit4::class)
internal class TrendDaoImplTest : CacheDatabaseDaoTest() {
    private val twitterAccountKey = MicroBlogKey.twitter("123")
    private val mastodonAccountKey = MicroBlogKey("456", "mastodon.com")
    private val trends = mutableListOf<UiTrend>()

    private val twitterTrendCount = 10
    private val mastodonTrendCount = 10

    override fun setUp() {
        super.setUp()
        for (i in 0 until twitterTrendCount) {
            trends.add(
                TwitterTrend(
                    name = "tweet $i",
                    url = "https://tweet $i"
                ).toUiTrend(twitterAccountKey)
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
                ).toUiTrend(mastodonAccountKey)
            )
        }
    }

    @Test
    fun insert_SaveBothTrendAndHistory() = runBlocking {
        val cacheDatabase = CacheDatabaseImpl(roomDatabase)
        cacheDatabase.trendDao().insertAll(trends)
        val twitterTrend = roomDatabase.trendDao().getPagingList(accountKey = twitterAccountKey, limit = twitterTrendCount, offset = 0)
        val mastodonTrend = roomDatabase.trendDao().getPagingList(accountKey = mastodonAccountKey, limit = mastodonTrendCount, offset = 0)
        assertEquals(twitterTrendCount, twitterTrend.size)
        assertEquals(mastodonTrendCount, mastodonTrend.size)
        mastodonTrend.forEach {
            assert(it.history.isNotEmpty())
        }
    }

    @Test
    fun getPagingListCount_ReturnsCountMatchesQuery() = runBlocking {
        val cacheDatabase = CacheDatabaseImpl(roomDatabase)
        cacheDatabase.trendDao().insertAll(trends)
        assertEquals(twitterTrendCount, roomDatabase.trendDao().getPagingListCount(twitterAccountKey))
        assertEquals(twitterTrendCount, roomDatabase.trendDao().getPagingList(twitterAccountKey, limit = twitterTrendCount + 10, offset = 0).size)
    }

    @Test
    fun getPagingSource_PagingSourceGenerateCorrectKeyForNext() = runBlocking {
        val cacheDatabase = CacheDatabaseImpl(roomDatabase)
        cacheDatabase.trendDao().insertAll(trends)
        val pagingSource = cacheDatabase.trendDao().getPagingSource(
            accountKey = twitterAccountKey
        )
        val result = pagingSource.load(params = PagingSource.LoadParams.Refresh(0, twitterTrendCount / 2, false))
        assert(result is PagingSource.LoadResult.Page)
        assertEquals(twitterTrendCount / 2, (result as PagingSource.LoadResult.Page).nextKey)
        assertEquals(twitterTrendCount / 2, result.data.size)

        val loadMoreResult = pagingSource.load(params = PagingSource.LoadParams.Append(result.nextKey ?: 0, twitterTrendCount / 2, false))
        assert(loadMoreResult is PagingSource.LoadResult.Page)
        assertEquals(null, (loadMoreResult as PagingSource.LoadResult.Page).nextKey)
        assertEquals(twitterTrendCount / 2, loadMoreResult.data.size)
    }

    @Test
    fun getPagingSource_pagingSourceInvalidateAfterDbUpDate() = runBlocking {
        var invalidate = false
        val cacheDatabase = CacheDatabaseImpl(roomDatabase)
        cacheDatabase.trendDao().getPagingSource(
            accountKey = twitterAccountKey
        ).apply {
            registerInvalidatedCallback {
                invalidate = true
            }
            load(PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false))
        }
        cacheDatabase.trendDao().insertAll(listOf(mockITrend().toUi(twitterAccountKey)))
        val start = System.currentTimeMillis()
        while (!invalidate && System.currentTimeMillis() - start < 3000) {
            continue
        }
        assert(invalidate)
    }

    @Test
    fun clearAll_ClearBothTrendAndTrendHistory() = runBlocking {
        val cacheDatabase = CacheDatabaseImpl(roomDatabase)
        cacheDatabase.trendDao().insertAll(trends)
        cacheDatabase.trendDao().clear(twitterAccountKey)
        assert(roomDatabase.trendDao().getAll(twitterAccountKey).isEmpty())

        cacheDatabase.trendDao().clear(mastodonAccountKey)
        assert(roomDatabase.trendDao().getAll(mastodonAccountKey).isEmpty())
        assert(roomDatabase.trendHistoryDao().getAll(mastodonAccountKey).isEmpty())
    }
}
