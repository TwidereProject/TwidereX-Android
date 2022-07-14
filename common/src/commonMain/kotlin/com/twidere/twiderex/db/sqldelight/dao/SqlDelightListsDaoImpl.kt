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

import androidx.paging.PagingSource
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import com.twidere.twiderex.db.dao.ListsDao
import com.twidere.twiderex.db.sqldelight.paging.QueryPagingSource
import com.twidere.twiderex.db.sqldelight.query.flatMap
import com.twidere.twiderex.db.sqldelight.transform.toDbList
import com.twidere.twiderex.db.sqldelight.transform.toUi
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiList
import com.twidere.twiderex.sqldelight.table.ListQueries
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class SqlDelightListsDaoImpl(private val listQueries: ListQueries) : ListsDao {
    override fun getPagingSource(accountKey: MicroBlogKey): PagingSource<Int, UiList> {
        return QueryPagingSource(
            countQuery = listQueries.getPagingCount(accountKey = accountKey),
            transacter = listQueries,
            queryProvider = { limit, offset, _ ->
                listQueries.getPagingList(accountKey = accountKey, limit = limit, offSet = offset)
                    .flatMap { it.toUi() }
            }
        )
    }

    override fun findWithListKeyWithFlow(
        listKey: MicroBlogKey,
        accountKey: MicroBlogKey
    ): Flow<UiList?> {
        return listQueries.findWithListKey(listKey = listKey, accountKey = accountKey)
            .asFlow()
            .mapToOneOrNull()
            .map { it?.toUi() }
    }

    override suspend fun insertAll(listOf: List<UiList>) {
        listQueries.transaction {
            listOf.forEach { listQueries.insert(it.toDbList()) }
        }
    }

    override suspend fun findWithListKey(listKey: MicroBlogKey, accountKey: MicroBlogKey): UiList? {
        return listQueries.findWithListKey(listKey = listKey, accountKey = accountKey)
            .executeAsOneOrNull()
            ?.toUi()
    }

    override suspend fun update(listOf: List<UiList>) {
        listQueries.transaction {
            listOf.forEach { listQueries.insert(it.toDbList()) }
        }
    }

    override suspend fun delete(listOf: List<UiList>) {
        listQueries.transaction {
            listOf.forEach { listQueries.delete(accountKey = it.accountKey, listKey = it.listKey) }
        }
    }

    override suspend fun clearAll(accountKey: MicroBlogKey) {
        listQueries.clearAll(accountKey = accountKey)
    }
}
