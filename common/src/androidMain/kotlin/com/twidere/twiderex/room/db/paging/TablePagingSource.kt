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
package com.twidere.twiderex.room.db.paging

import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.room.db.RoomCacheDatabase
import com.twidere.twiderex.room.db.dao.RoomDirectMessageConversationDao
import com.twidere.twiderex.room.db.dao.RoomDirectMessageEventDao
import com.twidere.twiderex.room.db.dao.RoomListsDao
import com.twidere.twiderex.room.db.dao.RoomPagingTimelineDao
import com.twidere.twiderex.room.db.dao.RoomTrendDao
import com.twidere.twiderex.room.db.transform.toPagingTimeline
import com.twidere.twiderex.room.db.transform.toUi

internal fun RoomDirectMessageConversationDao.getPagingSource(
    cacheDatabase: RoomCacheDatabase,
    accountKey: MicroBlogKey
) = DbPagingSource(
    roomDatabase = cacheDatabase,
    loadFromDb = { offset, limit ->
        getPagingList(
            accountKey = accountKey,
            limit = limit,
            offset = offset
        ).map { it.toUi() }
    },
    "dm_conversation",
    "dm_event"
)

internal fun RoomDirectMessageEventDao.getPagingSource(
    cacheDatabase: RoomCacheDatabase,
    accountKey: MicroBlogKey,
    conversationKey: MicroBlogKey
) = DbPagingSource(
    roomDatabase = cacheDatabase,
    loadFromDb = { offset, limit ->
        getPagingList(
            accountKey = accountKey,
            conversationKey = conversationKey,
            limit = limit,
            offset = offset
        ).map { it.toUi() }
    },
    "dm_event"
)

internal fun RoomListsDao.getPagingSource(
    cacheDatabase: RoomCacheDatabase,
    accountKey: MicroBlogKey,
) = DbPagingSource(
    roomDatabase = cacheDatabase,
    loadFromDb = { offset, limit ->
        getPagingList(
            accountKey = accountKey,
            limit = limit,
            offset = offset
        ).map { it.toUi() }
    },
    "lists"
)

internal fun RoomPagingTimelineDao.getPagingSource(
    cacheDatabase: RoomCacheDatabase,
    pagingKey: String,
    accountKey: MicroBlogKey,
) = DbPagingSource(
    roomDatabase = cacheDatabase,
    loadFromDb = { offset, limit ->
        getPagingList(
            pagingKey = pagingKey,
            accountKey = accountKey,
            offset = offset,
            limit = limit
        ).map { it.toPagingTimeline(accountKey = accountKey) }
    },
    "paging_timeline"
)

internal fun RoomTrendDao.getPagingSource(
    cacheDatabase: RoomCacheDatabase,
    accountKey: MicroBlogKey,
) = DbPagingSource(
    roomDatabase = cacheDatabase,
    loadFromDb = { offset, limit ->
        getPagingList(
            accountKey = accountKey,
            limit = limit,
            offset = offset
        ).map { it.toUi(accountKey = accountKey) }
    },
    "trends",
    "trend_histories"
)
