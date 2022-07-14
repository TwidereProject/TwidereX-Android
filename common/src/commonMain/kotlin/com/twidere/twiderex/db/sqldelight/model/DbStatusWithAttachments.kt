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

import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.ReferenceType
import com.twidere.twiderex.sqldelight.SqlDelightCacheDatabase
import com.twidere.twiderex.sqldelight.table.DbMedia
import com.twidere.twiderex.sqldelight.table.DbStatus
import com.twidere.twiderex.sqldelight.table.DbStatusReactions
import com.twidere.twiderex.sqldelight.table.DbUrlEntity
import com.twidere.twiderex.sqldelight.table.DbUser

internal data class DbStatusWithAttachments(
    val status: DbStatus,
    val user: DbUser,
    val reactions: DbStatusReactions,
    val medias: List<DbMedia>,
    val urls: List<DbUrlEntity>,
    val references: Map<ReferenceType, DbStatusWithAttachments>
) {
    companion object {
        fun DbStatus.withAttachments(database: SqlDelightCacheDatabase, accountKey: MicroBlogKey): DbStatusWithAttachments {
            return database.transactionWithResult {
                DbStatusWithAttachments(
                    status = this@withAttachments,
                    user = database.userQueries.findWithUserKey(userKey = userKey).executeAsOne(),
                    medias = database.mediaQueries.findMediaByBelongToKey(belongToKey = statusKey).executeAsList(),
                    urls = database.urlEntityQueries.findByBelongToKey(belongToKey = statusKey).executeAsList(),
                    references = refrenceStatus.list.associateBy { it.referenceType }.mapValues {
                        database.statusQueries.findWithStatusKey(statusKey = it.value.statusKey).executeAsOne().withAttachments(database, accountKey)
                    },
                    reactions = database.statusReactionsQueries.findWithStatusKey(statusKey = statusKey, accountKey = accountKey).executeAsOne()
                )
            }
        }

        fun List<DbStatusWithAttachments>.saveToDb(database: SqlDelightCacheDatabase) {
            database.transaction {
                map { it.references.map { it.value } + it }.flatten()
                    .forEach {
                        database.statusQueries.insert(it.status)
                        database.userQueries.insert(it.user)
                        database.statusReactionsQueries.insert(it.reactions)
                        it.medias.forEach { media -> database.mediaQueries.insert(media) }
                        it.urls.forEach { url -> database.urlEntityQueries.insert(url) }
                    }
            }
        }
    }
}
