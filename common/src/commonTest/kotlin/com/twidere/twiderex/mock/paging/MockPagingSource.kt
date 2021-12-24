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
package com.twidere.twiderex.mock.paging

import androidx.paging.DifferCallback
import androidx.paging.NullPaddedList
import androidx.paging.PagingData
import androidx.paging.PagingDataDiffer
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.jetbrains.annotations.TestOnly

internal class MockPagingSource<T : Any> @TestOnly constructor(val data: List<T>) : PagingSource<Int, T>() {

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            val page = params.key ?: 0
            val count = params.loadSize
            val startIndex = page * count
            val endIndex = page * count + count
            val result = when {
                endIndex <= data.size -> {
                    data.subList(startIndex, endIndex)
                }
                data.size in (startIndex + 1) until endIndex -> {
                    data.subList(startIndex, data.size)
                }
                else -> {
                    emptyList()
                }
            }
            LoadResult.Page(result, null, if (result.isEmpty()) null else page + 1)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
@TestOnly
internal suspend fun <T : Any> PagingData<T>.collectDataForTest(): List<T> {
    val dcb = object : DifferCallback {
        override fun onChanged(position: Int, count: Int) {}
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
    }
    val items = mutableListOf<T>()
    val dif = object : PagingDataDiffer<T>(dcb, UnconfinedTestDispatcher()) {
        override suspend fun presentNewList(
            previousList: NullPaddedList<T>,
            newList: NullPaddedList<T>,
            lastAccessedIndex: Int,
            onListPresentable: () -> Unit
        ): Int? {
            for (idx in 0 until newList.size)
                items.add(newList.getFromStorage(idx))
            onListPresentable()
            return null
        }
    }
    dif.collectFrom(this)
    return items
}

@TestOnly
internal suspend fun <K : Any, V : Any> PagingSource<K, V>.collectDataForTest(): List<V> {
    return if (this is MockPagingSource) {
        PagingData.from(this.data).collectDataForTest()
    } else {
        emptyList()
    }
}
