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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

private val INVALID = PagingSource.LoadResult.Invalid<Any, Any>()

internal abstract class LimitOffsetPagingSource<Value : Any>(
    private val dispatcher: CoroutineDispatcher
) : PagingSource<Int, Value>() {

    protected val itemCount: AtomicInteger = AtomicInteger(-1)

    protected abstract fun registerInvalidateObserver()

    protected abstract suspend fun queryItemCount(): Int

    protected abstract suspend fun queryData(offset: Int, limit: Int): List<Value>

    protected open suspend fun processResult(result: LoadResult<Int, Value>): LoadResult<Int, Value> = result

    private val registeredObserver: AtomicBoolean = AtomicBoolean(false)

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Value> {
        return withContext(dispatcher) {
            registerObserverIfNecessary()
            // When Invalidated, Paging will create a new PagingSource, so itemCount will be reset to -1
            var tempCount = itemCount.get()
            // if itemCount is < 0, then it is initial load
            if (tempCount < 0) {
                tempCount = queryItemCount()
                itemCount.set(tempCount)
            }
            // loadData
            val key = params.key ?: 0
            val limit: Int = getLimit(params, key)
            val offset: Int = getOffset(params, key, tempCount)
            val data = queryData(offset = offset, limit = limit)
            val nextPosToLoad = offset + data.size
            val nextKey =
                if (data.isEmpty() || data.size < limit || nextPosToLoad >= tempCount) {
                    null
                } else {
                    nextPosToLoad
                }
            // Refreshed key could be any where in the list, so we need both prevKey and nextKey
            // in order to load more data both before and after the current key
            val prevKey = if (offset <= 0 || data.isEmpty()) null else offset
            val loadResult = processResult(
                LoadResult.Page(
                    data = data,
                    prevKey = prevKey,
                    nextKey = nextKey,
                    itemsBefore = offset,
                    itemsAfter = maxOf(0, tempCount - nextPosToLoad)
                )
            )
            @Suppress("UNCHECKED_CAST")
            if (invalid) INVALID as LoadResult.Invalid<Int, Value> else loadResult
        }
    }

    /**
     * Calculates query limit based on LoadType.
     *
     * Prepend: If requested loadSize is larger than available number of items to prepend, it will
     * query with OFFSET = 0, LIMIT = prevKey
     */
    private fun getLimit(params: LoadParams<Int>, key: Int): Int {
        return when (params) {
            is LoadParams.Prepend ->
                if (key < params.loadSize) key else params.loadSize
            else -> params.loadSize
        }
    }

    /**
     * calculates query offset amount based on loadtype
     *
     * Prepend: OFFSET is calculated by counting backwards the number of items that needs to be
     * loaded before [key]. For example, if key = 30 and loadSize = 5, then offset = 25 and items
     * in db position 26-30 are loaded.
     * If requested loadSize is larger than the number of available items to
     * prepend, OFFSET clips to 0 to prevent negative OFFSET.
     *
     * Refresh:
     * If initialKey is supplied through Pager, Paging 3 will now start loading from
     * initialKey with initialKey being the first item.
     * If key is supplied by [getRefreshKey],OFFSET will attempt to load around the anchorPosition
     * with anchorPosition being the middle item. See comments on [getRefreshKey] for more details.
     * If key (regardless if from initialKey or [getRefreshKey]) is larger than available items,
     * the last page will be loaded by counting backwards the loadSize before last item in
     * database. For example, this can happen if invalidation came from a large number of items
     * dropped. i.e. in items 0 - 100, items 41-80 are dropped. Depending on last
     * viewed item, hypothetically [getRefreshKey] may return key = 60. If loadSize = 10, then items
     * 31-40 will be loaded.
     */
    private fun getOffset(params: LoadParams<Int>, key: Int, itemCount: Int): Int {
        return when (params) {
            is LoadParams.Prepend ->
                if (key < params.loadSize) 0 else (key - params.loadSize)
            is LoadParams.Append -> key
            is LoadParams.Refresh ->
                if (key >= itemCount) {
                    maxOf(0, itemCount - params.loadSize)
                } else {
                    key
                }
        }
    }

    private fun registerObserverIfNecessary() {
        if (registeredObserver.compareAndSet(false, true)) {
            registerInvalidateObserver()
        }
    }

    /**
     *  It is unknown whether anchorPosition represents the item at the top of the screen or item at
     *  the bottom of the screen. To ensure the number of items loaded is enough to fill up the
     *  screen, half of loadSize is loaded before the anchorPosition and the other half is
     *  loaded after the anchorPosition -- anchorPosition becomes the middle item.
     *
     *  To prevent a negative key, key = 0 when the number of items available before anchorPosition
     *  is less than the requested amount of initialLoadSize / 2.
     */
    override fun getRefreshKey(state: PagingState<Int, Value>): Int? {
        val initialLoadSize = state.config.initialLoadSize
        return when (state.anchorPosition) {
            null -> null
            else -> maxOf(0, state.anchorPosition!! - (initialLoadSize / 2))
        }
    }

    override val jumpingSupported: Boolean
        get() = true
}
