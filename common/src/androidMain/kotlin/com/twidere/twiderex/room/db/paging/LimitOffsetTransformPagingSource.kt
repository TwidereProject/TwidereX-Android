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
package com.twidere.twiderex.room.db.paging

import android.annotation.SuppressLint
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.room.InvalidationTracker
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
/**
 * An implementation of [PagingSource] to perform a LIMIT OFFSET query
 *
 * This class is used for Paging3 to perform Query and RawQuery in Room to return a PagingSource
 * for Pager's consumption. Registers observers on tables lazily and automatically invalidates
 * itself when data changes.
 */

private val INVALID = PagingSource.LoadResult.Invalid<Any, Any>()

internal class LimitOffsetTransformPagingSource<Value : Any>(
    /**
     * returns items from database matches offset and limit
     */
    private val loadPagingList: suspend (offset: Int, limit: Int) -> List<Value>,
    /**
     * returns count of requested items to calculate itemsAfter and itemsBefore for use in creating
     * LoadResult.Page<>
     */
    private val queryItemCount: suspend () -> Int,
    private val db: RoomDatabase,
    vararg tables: String
) : PagingSource<Int, Value>() {

    private val itemCount: AtomicInteger = AtomicInteger(-1)

    private val observer = object : InvalidationTracker.Observer(tables) {
        override fun onInvalidated(tables: MutableSet<String>) {
            invalidate()
        }
    }
    private val registeredObserver: AtomicBoolean = AtomicBoolean(false)

    @SuppressLint("RestrictedApi")
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Value> {
        return withContext(Dispatchers.IO) {
            registerObserverIfNecessary()
            val tempCount = itemCount.get()
            // if itemCount is < 0, then it is initial load
            if (tempCount < 0) {
                initialLoad(params)
            } else {
                // otherwise, it is a subsequent load
                val loadResult = loadFromDb(params, tempCount)
                // manually check if database has been updated. If so, the observers's
                // invalidation callback will invalidate this paging source
                db.invalidationTracker.refreshVersionsSync()
                @Suppress("UNCHECKED_CAST")
                if (invalid) INVALID as LoadResult.Invalid<Int, Value> else loadResult
            }
        }
    }

    /**
     *  For the very first time that this PagingSource's [load] is called. Executes the count
     *  query (initializes [itemCount]) and db query within a transaction to ensure initial load's
     *  data integrity.
     *
     *  For example, if the database gets updated after the count query but before the db query
     *  completes, the paging source may not invalidate in time, but this method will return
     *  data based on the original database that the count was performed on to ensure a valid
     *  initial load.
     */
    private suspend fun initialLoad(params: LoadParams<Int>): LoadResult<Int, Value> {
        return db.withTransaction {
            val tempCount = queryItemCount()
            itemCount.set(tempCount)
            loadFromDb(params, tempCount)
        }
    }

    private suspend fun loadFromDb(
        params: LoadParams<Int>,
        itemCount: Int,
    ): LoadResult<Int, Value> {
        val key = params.key ?: 0
        val limit: Int = getLimit(params, key)
        val offset: Int = getOffset(params, key, itemCount)
        return queryDatabase(offset, limit, itemCount)
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

    /**
     * calls RoomDatabase.query() to return a cursor and then calls convertRows() to extract and
     * return list of data
     *
     * throws [IllegalArgumentException] from [CursorUtil] if column does not exist
     *
     * @param offset offset parameter for LIMIT/OFFSET query. Bounded within user-supplied offset
     * if it is supplied
     *
     * @param limit limit parameter for LIMIT/OFFSET query. Bounded within user-supplied limit
     * if it is supplied
     */
    private suspend fun queryDatabase(
        offset: Int,
        limit: Int,
        itemCount: Int,
    ): LoadResult<Int, Value> {
        val data = loadPagingList(offset, limit)
        val nextPosToLoad = offset + data.size
        val nextKey =
            if (data.isEmpty() || data.size < limit || nextPosToLoad >= itemCount) {
                null
            } else {
                nextPosToLoad
            }
        val prevKey = if (offset <= 0 || data.isEmpty()) null else offset
        return LoadResult.Page(
            data = data,
            prevKey = prevKey,
            nextKey = nextKey,
            itemsBefore = offset,
            itemsAfter = maxOf(0, itemCount - nextPosToLoad)
        )
    }

    @SuppressLint("RestrictedApi")
    private fun registerObserverIfNecessary() {
        if (registeredObserver.compareAndSet(false, true)) {
            db.invalidationTracker.addWeakObserver(observer)
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
        return when {
            state.anchorPosition == null -> null
            else -> maxOf(0, state.anchorPosition!! - (initialLoadSize / 2))
        }
    }

    override val jumpingSupported: Boolean
        get() = true
}
