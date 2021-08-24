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
package com.twidere.twiderex.room.db.paging

import android.annotation.SuppressLint
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.room.InvalidationTracker
import androidx.room.RoomDatabase

@SuppressLint("RestrictedApi")
internal class DbPagingSource<UI : Any>(
    roomDatabase: RoomDatabase,
    private val loadFromDb: suspend (offset: Int, limit: Int) -> List<UI>,
    vararg tables: String
) : PagingSource<Int, UI>() {
    private val databaseObserver = object : InvalidationTracker.Observer(tables) {
        override fun onInvalidated(tables: MutableSet<String>) {
            invalidate()
        }
    }

    init {
        roomDatabase.invalidationTracker.addWeakObserver(databaseObserver)
    }

    override fun getRefreshKey(state: PagingState<Int, UI>): Int? {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UI> {
        return try {
            val list = loadFromDb(params.key ?: 0, params.loadSize)
            val nextKey = if (list.size < params.loadSize) null else (params.key ?: 0) + list.size
            LoadResult.Page(
                data = list,
                prevKey = null,
                nextKey = nextKey
            )
        } catch (e: Throwable) {
            LoadResult.Error(e)
        }
    }
}
