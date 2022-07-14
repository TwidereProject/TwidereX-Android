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

import com.twidere.twiderex.db.dao.StatusDao
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.room.db.RoomCacheDatabase
import com.twidere.twiderex.room.db.model.DbStatusReaction
import com.twidere.twiderex.room.db.model.saveToDb
import com.twidere.twiderex.room.db.transform.toDbStatusWithReference
import com.twidere.twiderex.room.db.transform.toUi
import kotlinx.coroutines.flow.map
import java.util.UUID

internal class StatusDaoImpl(
    private val roomCacheDatabase: RoomCacheDatabase
) : StatusDao {
    override suspend fun insertAll(listOf: List<UiStatus>, accountKey: MicroBlogKey) {
        listOf.map { it.toDbStatusWithReference(accountKey) }
            .saveToDb(roomCacheDatabase)
    }

    override suspend fun findWithStatusKey(
        statusKey: MicroBlogKey,
        accountKey: MicroBlogKey
    ) = roomCacheDatabase.statusDao().findWithStatusKeyWithReference(statusKey)?.toUi(accountKey)

    override fun findWithStatusKeyWithFlow(
        statusKey: MicroBlogKey,
        accountKey: MicroBlogKey
    ) = roomCacheDatabase.statusDao().findWithStatusKeyWithReferenceFlow(statusKey).map { it?.toUi(accountKey) }

    override suspend fun delete(statusKey: MicroBlogKey) {
        roomCacheDatabase.statusDao().delete(statusKey)
        roomCacheDatabase.statusReferenceDao().delete(statusKey)
        roomCacheDatabase.reactionDao().delete(statusKey)
    }

    override suspend fun updateAction(
        statusKey: MicroBlogKey,
        accountKey: MicroBlogKey,
        liked: Boolean?,
        retweet: Boolean?
    ) {
        roomCacheDatabase.reactionDao().findWithStatusKey(statusKey, accountKey).let {
            it ?: DbStatusReaction(
                _id = UUID.randomUUID().toString(),
                statusKey = statusKey,
                accountKey = accountKey,
                liked = false,
                retweeted = false,
            )
        }.let {
            roomCacheDatabase.reactionDao().insertAll(
                listOf(
                    it.copy(
                        liked = liked ?: it.liked,
                        retweeted = retweet ?: it.retweeted
                    )
                )
            )
        }
    }
}
