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
import com.twidere.twiderex.db.sqldelight.dao.SqlDelightListsDaoImpl
import com.twidere.twiderex.mock.model.mockIListModel
import com.twidere.twiderex.model.MicroBlogKey
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals

internal class SqlDelightListsDaoImplTest : BaseCacheDatabaseTest() {
    private lateinit var dao: SqlDelightListsDaoImpl
    private val accountKey = MicroBlogKey.twitter("account")
    override fun setUp() {
        super.setUp()
        dao = SqlDelightListsDaoImpl(
            listQueries = database.listQueries
        )
    }

    @Test
    fun insertAll_InsertAllUiList() = runBlocking {
        val list = listOf(
            mockIListModel(name = "1").toUi(accountKey),
            mockIListModel(name = "2").toUi(accountKey),
            mockIListModel(name = "3").toUi(accountKey),
        )
        dao.insertAll(list)
        assertEquals(3, database.listQueries.getPagingCount(accountKey = accountKey).executeAsOne())
    }

    @Test
    fun getPagingSource_PagingSourceGenerateCorrectKeyForNext() = runBlocking {
        val list = listOf(
            mockIListModel(name = "1").toUi(accountKey),
            mockIListModel(name = "2").toUi(accountKey),
            mockIListModel(name = "3").toUi(accountKey),
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
        val trend = mockIListModel(name = "list1").toUi(accountKey)
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
    fun update_UpdateWithGivenList() = runBlocking {
        val list = listOf(
            mockIListModel(name = "1").toUi(accountKey),
            mockIListModel(name = "2").toUi(accountKey),
            mockIListModel(name = "3").toUi(accountKey),
        )
        dao.insertAll(list)

        dao.update(list.map { it.copy(title = "update") })
        assertEquals(3, database.listQueries.getPagingCount(accountKey = accountKey).executeAsOne())
        list.forEach {
            assertEquals("update", dao.findWithListKey(accountKey = it.accountKey, listKey = it.listKey)?.title)
        }
    }

    @Test
    fun delete_DeleteWithGivenList() = runBlocking {
        val list = listOf(
            mockIListModel(name = "1").toUi(accountKey),
            mockIListModel(name = "2").toUi(accountKey),
            mockIListModel(name = "3").toUi(accountKey),
        )
        dao.insertAll(list)
        assertEquals(3, database.listQueries.getPagingCount(accountKey = accountKey).executeAsOne())
        dao.delete(list.subList(0, 2))
        assertEquals(1, database.listQueries.getPagingCount(accountKey = accountKey).executeAsOne())
    }
}
