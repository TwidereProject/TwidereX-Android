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
import com.twidere.twiderex.dataprovider.db.CacheDatabaseImpl
import com.twidere.twiderex.dataprovider.mapper.toUi
import com.twidere.twiderex.db.base.CacheDatabaseDaoTest
import com.twidere.twiderex.mock.model.mockIListModel
import com.twidere.twiderex.model.MicroBlogKey
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class ListsDaoImplTest : CacheDatabaseDaoTest() {
    val accountKey = MicroBlogKey.twitter("test")

    private val insertData = listOf(
        mockIListModel().toUi(accountKey),
        mockIListModel().toUi(accountKey),
        mockIListModel().toUi(accountKey),
        mockIListModel().toUi(accountKey),
        mockIListModel().toUi(accountKey)
    )
    @Test
    fun checkInsertDbLists() = runBlocking {
        val cacheDatabase = CacheDatabaseImpl(roomDatabase)
        cacheDatabase.listsDao().insertAll(insertData)
        val result = roomDatabase.listsDao().findAll()
        assertEquals(insertData.size, result?.size)
    }

    @Test
    fun findDbListWithListKey() = runBlocking {
        val cacheDatabase = CacheDatabaseImpl(roomDatabase)
        cacheDatabase.listsDao().insertAll(insertData)
        val result = cacheDatabase.listsDao().findWithListKey(insertData.first().listKey, accountKey)
        assertEquals(insertData.first().listKey, result?.listKey)
        val errorResult = cacheDatabase.listsDao().findWithListKey(insertData.first().listKey, MicroBlogKey.Empty)
        assertNull(errorResult)
    }

    @Test
    fun findDbListWithListKeyWithFlow_AutoUpdateAfterDbUpdate() = runBlocking {
        val cacheDatabase = CacheDatabaseImpl(roomDatabase)
        cacheDatabase.listsDao().insertAll(insertData)
        val source = cacheDatabase.listsDao().findWithListKeyWithFlow(insertData.first().listKey, accountKey)
        assertEquals(insertData.first().descriptions, source.firstOrNull()?.descriptions)
        cacheDatabase.listsDao().update(
            listOf(insertData.first().copy(descriptions = "update"))
        )
        assertEquals("update", source.firstOrNull()?.descriptions)

        cacheDatabase.listsDao().delete(listOf(insertData.first()))
        assertNull(source.firstOrNull())
    }

    @Test
    fun clearDbList() = runBlocking {
        val cacheDatabase = CacheDatabaseImpl(roomDatabase)
        cacheDatabase.listsDao().insertAll(insertData)
        cacheDatabase.listsDao().clearAll(accountKey)
        assert(roomDatabase.listsDao().findAll().isNullOrEmpty())
    }

    @Test
    fun getPagingSource_PagingSourceGenerateCorrectKeyForNext() = runBlocking {
        val cacheDatabase = CacheDatabaseImpl(roomDatabase)
        cacheDatabase.listsDao().insertAll(insertData)
        val pagingSource = cacheDatabase.listsDao().getPagingSource(
            accountKey = accountKey
        )
        val limit = 3
        val result = pagingSource.load(params = PagingSource.LoadParams.Refresh(0, limit, false))
        assert(result is PagingSource.LoadResult.Page)
        assertEquals(limit, (result as PagingSource.LoadResult.Page).nextKey)
        assertEquals(limit, result.data.size)

        val loadMoreResult = pagingSource.load(params = PagingSource.LoadParams.Append(result.nextKey ?: 0, limit, false))
        assert(loadMoreResult is PagingSource.LoadResult.Page)
        assertEquals(null, (loadMoreResult as PagingSource.LoadResult.Page).nextKey)
    }

    @Test
    fun getPagingSource_pagingSourceInvalidateAfterDbUpDate() = runBlocking {
        val cacheDatabase = CacheDatabaseImpl(roomDatabase)
        var invalidate = false
        cacheDatabase.listsDao().getPagingSource(
            accountKey = accountKey
        ).apply {
            registerInvalidatedCallback {
                invalidate = true
            }
            load(PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false))
        }
        cacheDatabase.listsDao().insertAll(listOf(mockIListModel().toUi(accountKey)))
        val start = System.currentTimeMillis()
        while (!invalidate && System.currentTimeMillis() - start < 3000) {
            continue
        }
        assert(invalidate)
    }

    @Test
    fun getPagingListCount_ReturnsCountMatchesQuery() = runBlocking {
        val cacheDatabase = CacheDatabaseImpl(roomDatabase)
        cacheDatabase.listsDao().insertAll(insertData + mockIListModel().toUi(MicroBlogKey.twitter("Not included")))
        assertEquals(insertData.size, roomDatabase.listsDao().getPagingListCount(accountKey))
        assertEquals(insertData.size, roomDatabase.listsDao().getPagingList(accountKey, limit = insertData.size + 10, offset = 0).size)
    }
}
