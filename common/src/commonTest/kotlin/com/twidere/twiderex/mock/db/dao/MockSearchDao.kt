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

import com.twidere.twiderex.db.dao.SearchDao
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiSearch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.jetbrains.annotations.TestOnly

class MockSearchDao @TestOnly constructor() : SearchDao {
    private val fakeDb = mutableMapOf<MicroBlogKey, MutableList<UiSearch>>()
    override suspend fun insertAll(search: List<UiSearch>) {
        search.forEach { uiSearch ->
            fakeDb[uiSearch.accountKey].let {
                if (it.isNullOrEmpty()) {
                    fakeDb[uiSearch.accountKey] = mutableListOf(uiSearch)
                } else {
                    it.add(uiSearch)
                }
            }
        }
    }

    override fun getAll(accountKey: MicroBlogKey): Flow<List<UiSearch>> {
        return flow {
            fakeDb[accountKey]?.toList()?.let {
                emit(it)
            } ?: emit(emptyList<UiSearch>())
        }
    }

    override fun getAllHistory(accountKey: MicroBlogKey): Flow<List<UiSearch>> {
        return getAll(accountKey)
            .map { it.filter { search -> !search.saved } }
    }

    override fun getAllSaved(accountKey: MicroBlogKey): Flow<List<UiSearch>> {
        return getAll(accountKey)
            .map { it.filter { search -> search.saved } }
    }

    override suspend fun get(content: String, accountKey: MicroBlogKey): UiSearch? {
        return fakeDb[accountKey]?.find {
            it.content == content
        }
    }

    override suspend fun remove(search: UiSearch) {
        fakeDb[search.accountKey]?.removeAll { it.content == search.content }
    }

    override suspend fun clear() {
        fakeDb.clear()
    }
}
