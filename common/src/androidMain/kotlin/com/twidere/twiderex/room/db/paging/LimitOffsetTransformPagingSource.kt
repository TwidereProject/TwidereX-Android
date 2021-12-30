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
import androidx.room.InvalidationTracker
import androidx.room.RoomDatabase
import com.twidere.twiderex.paging.crud.LimitOffsetPagingSource
import kotlinx.coroutines.Dispatchers

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
) : LimitOffsetPagingSource<Value>(Dispatchers.IO) {

    private val observer = object : InvalidationTracker.Observer(tables) {
        override fun onInvalidated(tables: MutableSet<String>) {
            invalidate()
        }
    }

    @SuppressLint("RestrictedApi")
    override fun registerInvalidateObserver() {
        db.invalidationTracker.addWeakObserver(observer)
    }

    override suspend fun queryItemCount() = queryItemCount.invoke()

    override suspend fun queryData(offset: Int, limit: Int) = loadPagingList(offset, limit)

    @SuppressLint("RestrictedApi")
    override suspend fun processResult(result: LoadResult<Int, Value>): LoadResult<Int, Value> {
        // manually check if database has been updated. If so, the observers's
        // invalidation callback will invalidate this paging source
        db.invalidationTracker.refreshVersionsSync()
        return result
    }
}
