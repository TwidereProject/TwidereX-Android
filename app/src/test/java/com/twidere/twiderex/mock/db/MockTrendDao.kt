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
package com.twidere.twiderex.mock.db

import com.twidere.twiderex.db.dao.TrendDao
import com.twidere.twiderex.db.dao.TrendHistoryDao
import com.twidere.twiderex.db.model.DbTrend
import com.twidere.twiderex.db.model.DbTrendHistory
import com.twidere.twiderex.db.model.DbTrendWithHistory
import com.twidere.twiderex.model.MicroBlogKey
private val historiesMap = mutableMapOf<MicroBlogKey, MutableList<DbTrendHistory>>()
class MockTrendDao : TrendDao {
    private val data = mutableListOf<DbTrend>()
    override suspend fun insertAll(trends: List<DbTrend>) {
        data.addAll(trends)
    }

    override suspend fun find(accountKey: MicroBlogKey, limit: Int): List<DbTrendWithHistory> {
        return data.map {
            DbTrendWithHistory(it, historiesMap[it.trendKey] ?: emptyList())
        }.subList(0, limit.coerceIn(0, data.size))
    }

    override suspend fun clearAll(accountKey: MicroBlogKey) {
        data.clear()
    }
}

class MockTrendHistoryDao : TrendHistoryDao {
    override suspend fun insertAll(histories: List<DbTrendHistory>) {
        histories.map {
            val list = historiesMap[it.trendKey] ?: mutableListOf()
            list.add(it)
            historiesMap[it.trendKey] = list
        }
    }

    override suspend fun clearAll(accountKey: MicroBlogKey) {
        historiesMap.clear()
    }
}
