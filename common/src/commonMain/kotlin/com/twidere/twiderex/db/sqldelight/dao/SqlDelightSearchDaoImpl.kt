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
package com.twidere.twiderex.db.sqldelight.dao

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.twidere.twiderex.db.dao.SearchDao
import com.twidere.twiderex.db.sqldelight.transform.toDbSearch
import com.twidere.twiderex.db.sqldelight.transform.toUi
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiSearch
import com.twidere.twiderex.sqldelight.table.Search
import com.twidere.twiderex.sqldelight.table.SearchQueries
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class SqlDelightSearchDaoImpl(private val queries: SearchQueries) : SearchDao {
    override suspend fun insertAll(search: List<UiSearch>) {
        queries.transaction {
            search.forEach {
                queries.insert(search = it.toDbSearch())
            }
        }
    }

    override fun getAll(accountKey: MicroBlogKey): Flow<List<UiSearch>> {
        return queries.getAll(accountKey = accountKey)
            .asUiFlow()
    }

    override fun getAllHistory(accountKey: MicroBlogKey): Flow<List<UiSearch>> {
        return queries.getHistories(accountKey = accountKey)
            .asUiFlow()
    }

    override fun getAllSaved(accountKey: MicroBlogKey): Flow<List<UiSearch>> {
        return queries.getSaved(accountKey = accountKey)
            .asUiFlow()
    }

    override suspend fun get(content: String, accountKey: MicroBlogKey): UiSearch? {
        return queries.get(content = content, accountKey = accountKey).executeAsOneOrNull()?.toUi()
    }

    override suspend fun remove(search: UiSearch) {
        queries.remove(content = search.content, accountKey = search.accountKey)
    }

    override suspend fun clear() {
        queries.clear()
    }

    private fun Query<Search>.asUiFlow() = asFlow()
        .mapToList()
        .map { it.map { search -> search.toUi() } }
}
