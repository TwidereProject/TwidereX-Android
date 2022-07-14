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
package com.twidere.twiderex.db.dao

import androidx.paging.PagingSource
import com.twidere.twiderex.base.BaseCacheDatabaseTest
import com.twidere.twiderex.dataprovider.mapper.toUi
import com.twidere.twiderex.db.sqldelight.dao.SqlDelightTrendDaoImpl
import com.twidere.twiderex.mock.model.mockITrend
import com.twidere.twiderex.model.MicroBlogKey
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals

internal class SqlDelightTrendDaoImplTest : BaseCacheDatabaseTest() {
    private lateinit var dao: SqlDelightTrendDaoImpl
    private val accountKey = MicroBlogKey.twitter("account")
    override fun setUp() {
        super.setUp()
        dao = SqlDelightTrendDaoImpl(
            database = database
        )
    }

    @Test
    fun insertAll_InsertBothTrendAndHistoryToDb() = runBlocking {
        val list = listOf(
            mockITrend(name = "trend1").toUi(accountKey),
            mockITrend(name = "trend2").toUi(accountKey),
            mockITrend(name = "trend3").toUi(accountKey),
        )
        dao.insertAll(list)
        val trends = database.trendQueries.getTrendPagingList(accountKey = accountKey, limit = 10, offset = 0).executeAsList()
        assertEquals(3, trends.size)
        trends.forEach {
            val histories = database.trendHistoryQueries.findWithTrendKey(trendKey = it.trendKey, accountKey = accountKey).executeAsList()
            assert(histories.isNotEmpty())
        }
    }

    @Test
    fun getPagingSource_PagingSourceGenerateCorrectKeyForNext() = runBlocking {
        val list = listOf(
            mockITrend(name = "trend1").toUi(accountKey),
            mockITrend(name = "trend2").toUi(accountKey),
            mockITrend(name = "trend3").toUi(accountKey),
        )
        dao.insertAll(list)
        val pagingSource = dao.getPagingSource(
            accountKey = accountKey,
        )
        val limit = 2
        val result = pagingSource.load(params = PagingSource.LoadParams.Refresh(0, limit, false))
        assert(result is PagingSource.LoadResult.Page)
        assertEquals(limit, (result as PagingSource.LoadResult.Page).nextKey)
        assertEquals(limit, result.data.size)

        val loadMoreResult = pagingSource.load(params = PagingSource.LoadParams.Append(result.nextKey ?: 0, limit, false))
        assert(loadMoreResult is PagingSource.LoadResult.Page)
        assertEquals(null, (loadMoreResult as PagingSource.LoadResult.Page).nextKey)
    }

    @Test
    fun getPagingSource_pagingSourceInvalidateAfterDbUpdate() = runBlocking {
        val trend = mockITrend(name = "trend1").toUi(accountKey)
        var invalidate = false
        dao.getPagingSource(
            accountKey = accountKey,
        ).apply {
            registerInvalidatedCallback {
                invalidate = true
            }
            load(PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false))
        }
        dao.insertAll(listOf(trend))
        val start = System.currentTimeMillis()
        while (!invalidate && System.currentTimeMillis() - start < 3000) {
            continue
        }
        assert(invalidate)
    }

    @Test
    fun clearAll_ClearAllTrendAndTrendHistoryWithMatchesGivenAccountKey() = runBlocking {
        val list = listOf(
            mockITrend(name = "trend1").toUi(accountKey),
            mockITrend(name = "trend2").toUi(accountKey),
            mockITrend(name = "trend3").toUi(accountKey),
        )
        dao.insertAll(list)
        dao.clear(accountKey)
        list.forEach {
            assert(database.trendHistoryQueries.findWithTrendKey(trendKey = it.trendKey, accountKey = accountKey).executeAsList().isEmpty())
        }
        assertEquals(0, database.trendQueries.getTrendPagingCount(accountKey = accountKey).executeAsOne())
    }
}
