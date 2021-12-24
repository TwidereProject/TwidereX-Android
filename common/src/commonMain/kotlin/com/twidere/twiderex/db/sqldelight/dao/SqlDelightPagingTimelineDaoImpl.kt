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
import com.twidere.twiderex.db.dao.PagingTimelineDao
import com.twidere.twiderex.db.sqldelight.model.DbPagingTimelineWithStatus
import com.twidere.twiderex.db.sqldelight.model.DbPagingTimelineWithStatus.Companion.saveToDb
import com.twidere.twiderex.db.sqldelight.model.DbPagingTimelineWithStatus.Companion.withStatus
import com.twidere.twiderex.db.sqldelight.model.DbStatusWithAttachments.Companion.withAttachments
import com.twidere.twiderex.db.sqldelight.paging.QueryPagingSource
import com.twidere.twiderex.db.sqldelight.query.flatMap
import com.twidere.twiderex.db.sqldelight.transform.toDbPagingTimeline
import com.twidere.twiderex.db.sqldelight.transform.toUi
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.paging.PagingTimeLine
import com.twidere.twiderex.model.paging.PagingTimeLineWithStatus
import com.twidere.twiderex.sqldelight.SqlDelightCacheDatabase

internal class SqlDelightPagingTimelineDaoImpl(private val database: SqlDelightCacheDatabase) : PagingTimelineDao {
    override fun getPagingSource(
        pagingKey: String,
        accountKey: MicroBlogKey
    ): PagingSource<Int, PagingTimeLineWithStatus> {
        return QueryPagingSource(
            countQuery = database.pagingTimelineQueries.getPagingCount(accountKey = accountKey, pagingKey = pagingKey),
            transacter = database.pagingTimelineQueries,
            queryProvider = { limit, offset, relationQueryRegister ->
                database.pagingTimelineQueries.getPagingList(
                    accountKey = accountKey,
                    pagingKey = pagingKey,
                    limit = limit,
                    offset = offset
                ).flatMap {
                    DbPagingTimelineWithStatus(
                        timeline = it,
                        status = database.statusQueries
                            .findWithStatusKey(statusKey = it.statusKey)
                            .also { query ->
                                relationQueryRegister.addRelationQuery(query)
                            }.executeAsOne()
                            .withAttachments(database, accountKey = accountKey)
                    ).toUi()
                }
            }
        )
    }

    override suspend fun clearAll(pagingKey: String, accountKey: MicroBlogKey) {
        database.pagingTimelineQueries.clearAll(accountKey = accountKey, pagingKey = pagingKey)
    }

    override suspend fun getLatest(
        pagingKey: String,
        accountKey: MicroBlogKey
    ): PagingTimeLineWithStatus? {
        return database.pagingTimelineQueries.getLatest(accountKey = accountKey, pagingKey = pagingKey).executeAsOneOrNull()?.withStatus(database)?.toUi()
    }

    override suspend fun findWithStatusKey(
        maxStatusKey: MicroBlogKey,
        accountKey: MicroBlogKey
    ): PagingTimeLine? {
        return database.pagingTimelineQueries.findWithStatusKey(
            statusKey = maxStatusKey,
            accountKey = accountKey
        ).executeAsList().firstOrNull()?.toUi()
    }

    override suspend fun insertAll(listOf: List<PagingTimeLine>) {
        listOf.map { it.toDbPagingTimeline() }.saveToDb(database)
    }

    override suspend fun delete(statusKey: MicroBlogKey) {
        database.pagingTimelineQueries.delete(statusKey = statusKey)
    }
}
