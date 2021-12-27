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
import com.twidere.twiderex.db.dao.TrendDao
import com.twidere.twiderex.db.sqldelight.model.DbTrendWithHistory.Companion.saveToDb
import com.twidere.twiderex.db.sqldelight.model.DbTrendWithHistory.Companion.withHistory
import com.twidere.twiderex.db.sqldelight.paging.QueryPagingSource
import com.twidere.twiderex.db.sqldelight.query.flatMap
import com.twidere.twiderex.db.sqldelight.transform.toDbTrendWithHistory
import com.twidere.twiderex.db.sqldelight.transform.toUi
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiTrend
import com.twidere.twiderex.sqldelight.SqlDelightCacheDatabase

internal class SqlDelightTrendDaoImpl(private val database: SqlDelightCacheDatabase) : TrendDao {
    override suspend fun insertAll(trends: List<UiTrend>) {
        trends.map { it.toDbTrendWithHistory() }.saveToDb(database)
    }

    override fun getPagingSource(accountKey: MicroBlogKey): PagingSource<Int, UiTrend> {
        return QueryPagingSource(
            countQuery = database.trendQueries.getTrendPagingCount(accountKey = accountKey),
            transacter = database.trendQueries,
            queryProvider = { limit, offset, _ ->
                database.trendQueries.getTrendPagingList(
                    accountKey = accountKey,
                    limit = limit,
                    offset = offset
                ).flatMap {
                    it.withHistory(database).toUi()
                }
            }
        )
    }

    override suspend fun clear(accountKey: MicroBlogKey) {
        database.transaction {
            database.trendQueries.clear(accountKey = accountKey)
            database.trendHistoryQueries.clear(accountKey = accountKey)
        }
    }
}
