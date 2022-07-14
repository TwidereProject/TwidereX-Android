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
package com.twidere.twiderex.dataprovider.db.dao

import androidx.paging.PagingSource
import com.twidere.twiderex.db.dao.TrendDao
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiTrend
import com.twidere.twiderex.room.db.RoomCacheDatabase
import com.twidere.twiderex.room.db.model.DbTrendWithHistory.Companion.saveToDb
import com.twidere.twiderex.room.db.paging.getPagingSource
import com.twidere.twiderex.room.db.transform.toDbTrendWithHistory

internal class TrendDaoImpl(
    val roomCacheDatabase: RoomCacheDatabase
) : TrendDao {
    override suspend fun insertAll(trends: List<UiTrend>) {
        trends.toDbTrendWithHistory().saveToDb(roomCacheDatabase)
    }

    override fun getPagingSource(accountKey: MicroBlogKey): PagingSource<Int, UiTrend> {
        return roomCacheDatabase.trendDao().getPagingSource(
            cacheDatabase = roomCacheDatabase,
            accountKey = accountKey
        )
    }

    override suspend fun clear(accountKey: MicroBlogKey) {
        roomCacheDatabase.trendDao().clearAll(accountKey)
        roomCacheDatabase.trendHistoryDao().clearAll(accountKey)
    }
}
