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
import com.twidere.twiderex.sqldelight.table.DbDMEvent
import com.twidere.twiderex.sqldelight.table.DbMedia
import com.twidere.twiderex.sqldelight.table.DbUrlEntity
import com.twidere.twiderex.sqldelight.table.DbUser

internal data class DbDMEventWithAttachments(
    val event: DbDMEvent,
    val media: List<DbMedia>,
    val url: List<DbUrlEntity>,
    val sender: DbUser
) {
    companion object {
        fun DbDMEvent.withAttachments(database: SqlDelightCacheDatabase): DbDMEventWithAttachments {
            return database.transactionWithResult {
                DbDMEventWithAttachments(
                    event = this@withAttachments,
                    url = database.urlEntityQueries.findByBelongToKey(messageKey).executeAsList(),
                    media = database.mediaQueries.findMediaByBelongToKey(messageKey).executeAsList(),
                    sender = database.userQueries.findWithUserKey(senderAccountKey).executeAsOne()
                )
            }
        }

        fun List<DbDMEventWithAttachments>.saveToDb(database: SqlDelightCacheDatabase) {
            database.transaction {
                forEach {
                    database.dMEventQueries.insert(it.event)
                    it.media.forEach { media -> database.mediaQueries.insert(media) }
                    database.userQueries.insert(it.sender)
                    it.url.forEach { url -> database.urlEntityQueries.insert(url) }
                }
            }
        }
    }
}
