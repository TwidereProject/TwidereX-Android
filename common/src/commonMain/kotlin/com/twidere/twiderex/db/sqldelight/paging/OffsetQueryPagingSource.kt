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
package com.twidere.twiderex.db.sqldelight.paging

import androidx.paging.PagingState
import com.squareup.sqldelight.Query
import com.squareup.sqldelight.Transacter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicLong

internal class OffsetQueryPagingSource<RowType : Any>(
    private val queryProvider: (limit: Long, offset: Long) -> Query<RowType>,
    private val countQuery: Query<Long>,
    private val transacter: Transacter,
    private val dispatcher: CoroutineDispatcher,
) : QueryPagingSource<Int, RowType>() {

    override val jumpingSupported get() = true

    private val itemCount: AtomicLong = AtomicLong(-1)

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, RowType> = withContext(dispatcher) {
        val tempCount = itemCount.get()
        // if itemCount is < 0, then it is initial load
        if (tempCount < 0) {
            initialLoad(params)
        } else {
            // otherwise, it is a subsequent load
            loadFromDb(params, tempCount)
        }
    }

    private fun initialLoad(params: LoadParams<Int>): LoadResult<Int, RowType> {
        return transacter.transactionWithResult {
            val tempCount = countQuery.executeAsOne()
            itemCount.set(tempCount)
            loadFromDb(params, tempCount)
        }
    }

    private fun loadFromDb(params: LoadParams<Int>, tempCount: Long): LoadResult<Int, RowType> {
        val key = params.key ?: 0
        val limit: Int = getLimit(params, key)
        val offset: Int = getOffset(params, key, tempCount)
        return queryDatabase(offset, limit, tempCount)
    }

    private fun getLimit(params: LoadParams<Int>, key: Int): Int {
        return when (params) {
            is LoadParams.Prepend ->
                if (key < params.loadSize) key else params.loadSize
            else -> params.loadSize
        }
    }

    private fun getOffset(params: LoadParams<Int>, key: Int, itemCount: Long): Int {
        return when (params) {
            is LoadParams.Prepend ->
                if (key < params.loadSize) 0 else (key - params.loadSize)
            is LoadParams.Append -> key
            is LoadParams.Refresh ->
                if (key >= itemCount) {
                    maxOf(0, itemCount - params.loadSize)
                } else {
                    key
                }.toInt()
        }
    }

    private fun queryDatabase(
        offset: Int,
        limit: Int,
        itemCount: Long,
    ): LoadResult<Int, RowType> {
        return try {
            val data = queryProvider(limit.toLong(), offset.toLong())
                .also { currentQuery = it }
                .executeAsList()
            val nextPosToLoad = offset + data.size
            val nextKey =
                if (data.isEmpty() || data.size < limit || nextPosToLoad >= itemCount) {
                    null
                } else {
                    nextPosToLoad
                }
            val prevKey = if (offset <= 0 || data.isEmpty()) null else offset
            LoadResult.Page(
                data = data,
                prevKey = prevKey,
                nextKey = nextKey,
                itemsBefore = offset,
                itemsAfter = maxOf(0, itemCount - nextPosToLoad).toInt()
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, RowType>) = state.anchorPosition
}
