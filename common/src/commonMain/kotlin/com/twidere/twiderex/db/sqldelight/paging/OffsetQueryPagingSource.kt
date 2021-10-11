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

internal class OffsetQueryPagingSource<RowType : Any>(
    private val queryProvider: (limit: Long, offset: Long) -> Query<RowType>,
    private val countQuery: Query<Long>,
    private val transacter: Transacter,
    private val dispatcher: CoroutineDispatcher,
) : QueryPagingSource<Int, RowType>() {

    override val jumpingSupported get() = true

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, RowType> = withContext(dispatcher) {
        try {
            val key = params.key ?: 0
            transacter.transactionWithResult {
                val count = countQuery.executeAsOne()
                if (count != 0L && key.toLong() >= count) throw IndexOutOfBoundsException()

                val loadSize = if (key < 0) params.loadSize + key else params.loadSize

                val data = queryProvider(loadSize.toLong(), maxOf(0, key).toLong())
                    .also { currentQuery = it }
                    .executeAsList()

                LoadResult.Page(
                    data = data,
                    // allow one, and only one negative prevKey in a paging set. This is done for
                    // misaligned prepend queries to avoid duplicates.
                    prevKey = if (key <= 0) null else key - params.loadSize,
                    nextKey = if (key + params.loadSize >= count) null else key + params.loadSize,
                    itemsBefore = maxOf(0, key),
                    itemsAfter = maxOf(0, (count - (key + params.loadSize))).toInt()
                )
            }
        } catch (e: Exception) {
            if (e is IndexOutOfBoundsException) throw e
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, RowType>) = state.anchorPosition
}
