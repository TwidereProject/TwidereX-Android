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

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal class MemoryCachePagingSource<Value : Any>(
    private val memoryCache: PagingMemoryCache<Value>,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : LimitOffsetPagingSource<Value>(dispatcher), OnInvalidateObserver {

    override fun registerInvalidateObserver() = memoryCache.addWeakObserver(this)

    override suspend fun queryItemCount() = memoryCache.size()

    override suspend fun queryData(offset: Int, limit: Int) = memoryCache.find(offset, limit)

    override fun onInvalidate() = invalidate()
}
