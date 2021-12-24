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
package com.twidere.twiderex.paging

import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.twidere.twiderex.room.db.RoomCacheDatabase
import com.twidere.twiderex.room.db.paging.LimitOffsetTransformPagingSource
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class LimitOffsetTransformPagingSourceTest {
    private val count = 30

    private val pagingSource = LimitOffsetTransformPagingSource(
        loadPagingList = { offset, limit ->
            val list = mutableListOf<Model>()
            for (i in 0 until if (offset + limit <= count)limit else (count - offset)) {
                list.add(Model(name = (offset + i).toString()))
            }
            list
        },
        queryItemCount = {
            count
        },
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), RoomCacheDatabase::class.java).build()
    )
    @Test
    fun refreshReturnsResultPageWithCorrectParams() = runBlocking {
        val result = pagingSource.load(params = PagingSource.LoadParams.Refresh(key = null, loadSize = 15, placeholdersEnabled = false))
        assert(result is PagingSource.LoadResult.Page)
        if (result !is PagingSource.LoadResult.Page) return@runBlocking
        assertEquals(null, result.prevKey)
        assertEquals(15, result.nextKey)
        assertEquals(0, result.itemsBefore)
        assertEquals(15, result.itemsAfter)
        assertEquals(15, result.data.size)

        val noMoreResult = pagingSource.load(params = PagingSource.LoadParams.Refresh(key = null, loadSize = count, placeholdersEnabled = false))
        assert(noMoreResult is PagingSource.LoadResult.Page)
        if (noMoreResult is PagingSource.LoadResult.Page) {
            assertNull(noMoreResult.prevKey)
            assertNull(noMoreResult.nextKey)
            assertEquals(0, noMoreResult.itemsBefore)
            assertEquals(0, noMoreResult.itemsAfter)
            assertEquals(count, noMoreResult.data.size)
        }
    }

    @Test
    fun appendReturnsResultPageWithCorrectParams() = runBlocking {
        val result = pagingSource.load(params = PagingSource.LoadParams.Refresh(key = null, loadSize = 15, placeholdersEnabled = false))
        val loadMoreResult = pagingSource.load(params = PagingSource.LoadParams.Append(key = (result as PagingSource.LoadResult.Page).nextKey ?: 0, loadSize = 15, placeholdersEnabled = false))
        assert(loadMoreResult is PagingSource.LoadResult.Page)
        if (loadMoreResult is PagingSource.LoadResult.Page) {
            assertEquals(15, loadMoreResult.prevKey)
            assertNull(loadMoreResult.nextKey)
            assertEquals(15, loadMoreResult.itemsBefore)
            assertEquals(0, loadMoreResult.itemsAfter)
            assertEquals(15, loadMoreResult.data.size)
        }
    }

    @Test
    fun prependReturnsResultPageWithCorrectParams() = runBlocking {
        val prependResult = pagingSource.load(params = PagingSource.LoadParams.Prepend(key = 10, loadSize = 5, placeholdersEnabled = false))
        assert(prependResult is PagingSource.LoadResult.Page)
        if (prependResult is PagingSource.LoadResult.Page) {
            assertEquals(5, prependResult.prevKey)
            assertEquals(10, prependResult.nextKey)
            assertEquals(5, prependResult.itemsBefore)
            assertEquals(20, prependResult.itemsAfter)
            assertEquals(5, prependResult.data.size)
        }
    }
}

private data class Model(
    val name: String
)
