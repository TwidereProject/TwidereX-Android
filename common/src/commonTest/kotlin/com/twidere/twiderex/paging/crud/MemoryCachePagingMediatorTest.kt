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
package com.twidere.twiderex.paging.crud

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class TestMemoryCachePagingMediator(pagingMemoryCache: PagingMemoryCache<String>) : MemoryCachePagingMediator<Int, String>(pagingMemoryCache) {
    override suspend fun load(
        key: Int?,
        loadType: LoadType,
        state: PagingState<Int, String>
    ): PagingSource.LoadResult<Int, String> {
        return PagingSource.LoadResult.Page(listOf("1", "2"), null, null)
    }
}

class MemoryCachePagingMediatorTest {
    private val pagingMemoryCache = PagingMemoryCache<String>()

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun load_saveToPagingMemoryCacheAfterSuccess() = runBlocking {
        val mediator = TestMemoryCachePagingMediator(pagingMemoryCache)
        assertEquals(0, pagingMemoryCache.size())
        val pagingState = PagingState<Int, String>(emptyList(), config = PagingConfig(20), anchorPosition = 0, leadingPlaceholderCount = 0)
        mediator.load(LoadType.REFRESH, pagingState)
        assertEquals(2, pagingMemoryCache.size())
    }
}
