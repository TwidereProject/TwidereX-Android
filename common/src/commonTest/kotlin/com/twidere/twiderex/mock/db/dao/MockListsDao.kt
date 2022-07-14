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
package com.twidere.twiderex.mock.db.dao

import androidx.paging.PagingSource
import com.twidere.twiderex.db.dao.ListsDao
import com.twidere.twiderex.mock.paging.MockPagingSource
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jetbrains.annotations.TestOnly

internal class MockListsDao @TestOnly constructor() : ListsDao {
    private val fakeDb = mutableMapOf<MicroBlogKey, MutableList<UiList>>()

    override fun getPagingSource(accountKey: MicroBlogKey): PagingSource<Int, UiList> {
        return MockPagingSource(fakeDb[accountKey]?.toList() ?: emptyList())
    }

    override fun findWithListKeyWithFlow(
        listKey: MicroBlogKey,
        accountKey: MicroBlogKey
    ): Flow<UiList?> {
        return flow {
            emit(
                fakeDb[accountKey]?.let {
                    it.find { list -> list.listKey == listKey }
                }
            )
        }
    }

    override suspend fun insertAll(listOf: List<UiList>) {
        listOf.forEach { list ->
            fakeDb[list.accountKey].let {
                if (it.isNullOrEmpty()) {
                    fakeDb[list.accountKey] = mutableListOf(list)
                } else {
                    it.add(list)
                }
            }
        }
    }

    override suspend fun findWithListKey(listKey: MicroBlogKey, accountKey: MicroBlogKey): UiList? {
        return fakeDb[accountKey]?.let {
            it.find { list -> list.listKey == listKey }
        }
    }

    override suspend fun update(listOf: List<UiList>) {
        listOf.forEach { list ->
            fakeDb[list.accountKey].let {
                if (it.isNullOrEmpty()) {
                    fakeDb[list.accountKey] = mutableListOf(list)
                } else {
                    it.removeAll { origin -> origin.listKey == list.listKey }
                    it.add(list)
                }
            }
        }
    }

    override suspend fun delete(listOf: List<UiList>) {
        listOf.forEach { list ->
            fakeDb[list.accountKey]?.removeAll { it.listKey == list.listKey }
        }
    }

    override suspend fun clearAll(accountKey: MicroBlogKey) {
        fakeDb.clear()
    }
}
