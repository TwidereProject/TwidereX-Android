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
package com.twidere.twiderex.db.sqldelight.model

import com.twidere.twiderex.sqldelight.SqlDelightCacheDatabase
import com.twidere.twiderex.sqldelight.table.DbTrend
import com.twidere.twiderex.sqldelight.table.DbTrendHistory

internal data class DbTrendWithHistory(
    val trend: DbTrend,
    val history: List<DbTrendHistory>
) {
    companion object {
        fun List<DbTrendWithHistory>.saveToDb(database: SqlDelightCacheDatabase) {
            database.transaction {
                forEach { database.trendQueries.insert(it.trend) }
                map { it.history }.flatten().forEach { database.trendHistoryQueries.insert(it) }
            }
        }

        fun DbTrend.withHistory(database: SqlDelightCacheDatabase) = DbTrendWithHistory(
            trend = this,
            history = database.trendHistoryQueries.findWithTrendKey(
                trendKey = trendKey,
                accountKey = accountKey
            ).executeAsList()
        )
    }
}
