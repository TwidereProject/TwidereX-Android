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
package com.twidere.twiderex.room.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.twidere.twiderex.room.db.dao.RoomDirectMessageConversationDao
import com.twidere.twiderex.room.db.dao.RoomDirectMessageEventDao
import com.twidere.twiderex.room.db.dao.RoomListsDao
import com.twidere.twiderex.room.db.dao.RoomMediaDao
import com.twidere.twiderex.room.db.dao.RoomNotificationCursorDao
import com.twidere.twiderex.room.db.dao.RoomPagingTimelineDao
import com.twidere.twiderex.room.db.dao.RoomReactionDao
import com.twidere.twiderex.room.db.dao.RoomStatusDao
import com.twidere.twiderex.room.db.dao.RoomStatusReferenceDao
import com.twidere.twiderex.room.db.dao.RoomTrendDao
import com.twidere.twiderex.room.db.dao.RoomTrendHistoryDao
import com.twidere.twiderex.room.db.dao.RoomUrlEntityDao
import com.twidere.twiderex.room.db.dao.RoomUserDao
import com.twidere.twiderex.room.db.model.DbDMConversation
import com.twidere.twiderex.room.db.model.DbDMEvent
import com.twidere.twiderex.room.db.model.DbList
import com.twidere.twiderex.room.db.model.DbMedia
import com.twidere.twiderex.room.db.model.DbNotificationCursor
import com.twidere.twiderex.room.db.model.DbPagingTimeline
import com.twidere.twiderex.room.db.model.DbStatusReaction
import com.twidere.twiderex.room.db.model.DbStatusReference
import com.twidere.twiderex.room.db.model.DbStatusV2
import com.twidere.twiderex.room.db.model.DbTrend
import com.twidere.twiderex.room.db.model.DbTrendHistory
import com.twidere.twiderex.room.db.model.DbUrlEntity
import com.twidere.twiderex.room.db.model.DbUser
import com.twidere.twiderex.room.db.model.converter.ExtraConverter
import com.twidere.twiderex.room.db.model.converter.MastodonVisibilityConverter
import com.twidere.twiderex.room.db.model.converter.MediaTypeConverter
import com.twidere.twiderex.room.db.model.converter.MicroBlogKeyConverter
import com.twidere.twiderex.room.db.model.converter.NotificationCursorTypeConverter
import com.twidere.twiderex.room.db.model.converter.NotificationTypeConverter
import com.twidere.twiderex.room.db.model.converter.PlatformTypeConverter
import com.twidere.twiderex.room.db.model.converter.StringListConverter
import com.twidere.twiderex.room.db.model.converter.TwitterReplySettingsConverter
import com.twidere.twiderex.room.db.model.converter.UserTimelineTypeConverter

@Database(
    entities = [
        DbStatusV2::class,
        DbMedia::class,
        DbUser::class,
        DbStatusReaction::class,
        DbPagingTimeline::class,
        DbUrlEntity::class,
        DbStatusReference::class,
        DbList::class,
        DbNotificationCursor::class,
        DbTrend::class,
        DbTrendHistory::class,
        DbDMConversation::class,
        DbDMEvent::class
    ],
    version = 21,
)
@TypeConverters(
    MicroBlogKeyConverter::class,
    PlatformTypeConverter::class,
    MediaTypeConverter::class,
    UserTimelineTypeConverter::class,
    StringListConverter::class,
    NotificationTypeConverter::class,
    ExtraConverter::class,
    NotificationCursorTypeConverter::class,
    TwitterReplySettingsConverter::class,
    MastodonVisibilityConverter::class
)
internal abstract class RoomCacheDatabase : RoomDatabase() {
    abstract fun statusDao(): RoomStatusDao
    abstract fun mediaDao(): RoomMediaDao
    abstract fun userDao(): RoomUserDao
    abstract fun reactionDao(): RoomReactionDao
    abstract fun pagingTimelineDao(): RoomPagingTimelineDao
    abstract fun urlEntityDao(): RoomUrlEntityDao
    abstract fun statusReferenceDao(): RoomStatusReferenceDao
    abstract fun listsDao(): RoomListsDao
    abstract fun notificationCursorDao(): RoomNotificationCursorDao
    abstract fun trendDao(): RoomTrendDao
    abstract fun trendHistoryDao(): RoomTrendHistoryDao
    abstract fun directMessageConversationDao(): RoomDirectMessageConversationDao
    abstract fun directMessageDao(): RoomDirectMessageEventDao
}
