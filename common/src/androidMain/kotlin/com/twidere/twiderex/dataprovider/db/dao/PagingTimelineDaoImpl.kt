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
import com.twidere.twiderex.db.dao.PagingTimelineDao
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.paging.PagingTimeLine
import com.twidere.twiderex.model.paging.PagingTimeLineWithStatus
import com.twidere.twiderex.room.db.RoomCacheDatabase
import com.twidere.twiderex.room.db.paging.getPagingSource
import com.twidere.twiderex.room.db.transform.toDbPagingTimeline
import com.twidere.twiderex.room.db.transform.toPagingTimeline
import com.twidere.twiderex.room.db.transform.toUi

internal class PagingTimelineDaoImpl(private val database: RoomCacheDatabase) : PagingTimelineDao {
    override fun getPagingSource(
        pagingKey: String,
        accountKey: MicroBlogKey
    ): PagingSource<Int, PagingTimeLineWithStatus> {
        return database.pagingTimelineDao().getPagingSource(
            accountKey = accountKey,
            cacheDatabase = database,
            pagingKey = pagingKey
        )
    }

    override suspend fun clearAll(pagingKey: String, accountKey: MicroBlogKey) {
        database.pagingTimelineDao().clearAll(pagingKey, accountKey)
    }

    override suspend fun getLatest(
        pagingKey: String,
        accountKey: MicroBlogKey
    ) = database.pagingTimelineDao().getLatest(pagingKey, accountKey)?.toPagingTimeline(accountKey)

    override suspend fun findWithStatusKey(
        maxStatusKey: MicroBlogKey,
        accountKey: MicroBlogKey
    ) = database.pagingTimelineDao().findWithStatusKey(maxStatusKey, accountKey)?.toUi()

    override suspend fun insertAll(listOf: List<PagingTimeLine>) {
        database.pagingTimelineDao().insertAll(listOf.map { it.toDbPagingTimeline() })
    }

    override suspend fun delete(statusKey: MicroBlogKey) {
        database.pagingTimelineDao().delete(statusKey)
    }
}
