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
package com.twidere.twiderex.paging.crud

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator

@OptIn(ExperimentalPagingApi::class)
abstract class MemoryCachePagingMediator<Key : Any, Value : Any>(protected val memoryCache: PagingMemoryCache<Value>) : RemoteMediator<Int, Value>() {
    protected var paging: Key? = null
    override suspend fun load(loadType: LoadType, state: PagingState<Int, Value>): MediatorResult {
        return try {
            val key = when (loadType) {
                LoadType.APPEND -> paging
                LoadType.REFRESH -> {
                    memoryCache.clear()
                    null
                }
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            }
            when (val result = load(key, loadType, state)) {
                is PagingSource.LoadResult.Page -> {
                    paging = result.nextKey
                    memoryCache.insert(result.data)
                }
                is PagingSource.LoadResult.Error -> {
                    throw result.throwable
                }
                is PagingSource.LoadResult.Invalid -> Unit
            }
            MediatorResult.Success(paging == null)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    abstract suspend fun load(key: Key?, loadType: LoadType, state: PagingState<Int, Value>): PagingSource.LoadResult<Key, Value>
}
