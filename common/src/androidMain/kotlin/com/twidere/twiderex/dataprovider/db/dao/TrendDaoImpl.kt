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
package com.twidere.twiderex.dataprovider.db.dao

import androidx.paging.PagingSource
import com.twidere.twiderex.db.dao.TrendDao
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiTrend
import com.twidere.twiderex.room.db.dao.RoomTrendDao
import com.twidere.twiderex.room.db.dao.RoomTrendHistoryDao
import com.twidere.twiderex.room.db.transform.toDbTrendWithHistory

internal class TrendDaoImpl(
    private val roomTrendDao: RoomTrendDao,
    private val roomTrendHistoryDao: RoomTrendHistoryDao
) : TrendDao {
    override suspend fun insertAll(trends: List<UiTrend>) {
        trends.toDbTrendWithHistory().apply {
            map { it.trend }.let {
                roomTrendDao.insertAll(it)
            }

            map { it.history }
                .flatten()
                .let {
                    roomTrendHistoryDao.insertAll(it)
                }
        }
    }

    override fun getPagingSource(accountKey: MicroBlogKey): PagingSource<Int, UiTrend> {
        TODO("Not yet implemented")
    }

    override suspend fun clear(accountKey: MicroBlogKey) {
        roomTrendDao.clearAll(accountKey)
        roomTrendHistoryDao.clearAll(accountKey)
    }
}
