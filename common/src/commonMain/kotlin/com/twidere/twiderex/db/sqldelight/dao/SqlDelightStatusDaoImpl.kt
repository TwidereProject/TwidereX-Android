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

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import com.twidere.twiderex.db.dao.StatusDao
import com.twidere.twiderex.db.sqldelight.model.DbStatusWithAttachments.Companion.saveToDb
import com.twidere.twiderex.db.sqldelight.model.DbStatusWithAttachments.Companion.withAttachments
import com.twidere.twiderex.db.sqldelight.transform.toDbStatusWithAttachments
import com.twidere.twiderex.db.sqldelight.transform.toUi
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.sqldelight.SqlDelightCacheDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SqlDelightStatusDaoImpl(private val database: SqlDelightCacheDatabase) : StatusDao {
    override suspend fun insertAll(listOf: List<UiStatus>, accountKey: MicroBlogKey) {
        listOf.map { it.toDbStatusWithAttachments(accountKey) }.saveToDb(database)
    }

    override suspend fun findWithStatusKey(
        statusKey: MicroBlogKey,
        accountKey: MicroBlogKey
    ): UiStatus? {
        return database.statusQueries.findWithStatusKey(statusKey = statusKey)
            .executeAsList().firstOrNull()
            ?.withAttachments(database, accountKey)
            ?.toUi()
    }

    override fun findWithStatusKeyWithFlow(
        statusKey: MicroBlogKey,
        accountKey: MicroBlogKey
    ): Flow<UiStatus?> {
        return database.statusQueries
            .findWithStatusKey(statusKey = statusKey)
            .asFlow()
            .mapToOneOrNull()
            .map {
                it?.withAttachments(database, accountKey)
                    ?.toUi()
            }
    }

    override suspend fun delete(statusKey: MicroBlogKey) {
        database.statusQueries.delete(statusKey = statusKey)
    }

    override suspend fun updateAction(
        statusKey: MicroBlogKey,
        accountKey: MicroBlogKey,
        liked: Boolean?,
        retweet: Boolean?
    ) {
        database.statusReactionsQueries.findWithStatusKey(
            accountKey = accountKey,
            statusKey = statusKey
        ).executeAsOneOrNull()
            ?.let {
                database.statusReactionsQueries.insert(
                    it.copy(
                        liked = liked ?: it.liked,
                        retweeted = retweet ?: it.retweeted
                    )
                )
            }
    }
}
