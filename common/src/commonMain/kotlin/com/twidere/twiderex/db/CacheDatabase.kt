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
package com.twidere.twiderex.db

import com.twidere.twiderex.db.dao.DirectMessageConversationDao
import com.twidere.twiderex.db.dao.DirectMessageEventDao
import com.twidere.twiderex.db.dao.ListsDao
import com.twidere.twiderex.db.dao.MediaDao
import com.twidere.twiderex.db.dao.NotificationCursorDao
import com.twidere.twiderex.db.dao.PagingTimelineDao
import com.twidere.twiderex.db.dao.ReactionDao
import com.twidere.twiderex.db.dao.StatusDao
import com.twidere.twiderex.db.dao.StatusReferenceDao
import com.twidere.twiderex.db.dao.TrendDao
import com.twidere.twiderex.db.dao.TrendHistoryDao
import com.twidere.twiderex.db.dao.UrlEntityDao
import com.twidere.twiderex.db.dao.UserDao

interface CacheDatabase : Database {
    abstract fun statusDao(): StatusDao
    abstract fun mediaDao(): MediaDao
    abstract fun userDao(): UserDao
    abstract fun reactionDao(): ReactionDao
    abstract fun pagingTimelineDao(): PagingTimelineDao
    abstract fun urlEntityDao(): UrlEntityDao
    abstract fun statusReferenceDao(): StatusReferenceDao
    abstract fun listsDao(): ListsDao
    abstract fun notificationCursorDao(): NotificationCursorDao
    abstract fun trendDao(): TrendDao
    abstract fun trendHistoryDao(): TrendHistoryDao
    abstract fun directMessageConversationDao(): DirectMessageConversationDao
    abstract fun directMessageDao(): DirectMessageEventDao
}
