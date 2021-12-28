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

import androidx.paging.PagingSource
import androidx.paging.PagingState

class MemoryCachePagingSource<Value : Any>(
    private val memoryCache: PagingMemoryCache<Value>,
) : PagingSource<Int, Value>(), OnInvalidateObserver {
    init {
        memoryCache.addWeakObserver(this)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Value> {
        return try {
            val count = params.loadSize
            val startIndex = params.key ?: 0
            val endIndex = startIndex + count
            val result = memoryCache.find(startIndex, endIndex)
            LoadResult.Page(
                data = result,
                null,
                if (result.isEmpty() || result.size < count) null else startIndex + result.size
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun onInvalidate() {
        invalidate()
    }

    override fun getRefreshKey(state: PagingState<Int, Value>): Int? {
        return null
    }
}
