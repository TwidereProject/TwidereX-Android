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

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.twidere.twiderex.db.dao.MediaDao
import com.twidere.twiderex.db.dao.PagingTimelineDao
import com.twidere.twiderex.db.dao.ReactionDao
import com.twidere.twiderex.db.dao.SearchDao
import com.twidere.twiderex.db.dao.StatusDao
import com.twidere.twiderex.db.dao.TimelineDao
import com.twidere.twiderex.db.dao.UrlEntityDao
import com.twidere.twiderex.db.dao.UserDao
import com.twidere.twiderex.db.model.DbMedia
import com.twidere.twiderex.db.model.DbPagingTimeline
import com.twidere.twiderex.db.model.DbSearch
import com.twidere.twiderex.db.model.DbStatusReaction
import com.twidere.twiderex.db.model.DbStatusV2
import com.twidere.twiderex.db.model.DbTimeline
import com.twidere.twiderex.db.model.DbUrlEntity
import com.twidere.twiderex.db.model.DbUser
import com.twidere.twiderex.db.model.converter.MediaTypeConverter
import com.twidere.twiderex.db.model.converter.MicroBlogKeyConverter
import com.twidere.twiderex.db.model.converter.MicroBlogTypeConverter
import com.twidere.twiderex.db.model.converter.PlatformTypeConverter
import com.twidere.twiderex.db.model.converter.StringListConverter
import com.twidere.twiderex.db.model.converter.TimelineTypeConverter
import com.twidere.twiderex.db.model.converter.UserTimelineTypeConverter
import javax.inject.Singleton

@Singleton
@Database(
    entities = [
        DbStatusV2::class,
        DbTimeline::class,
        DbMedia::class,
        DbUser::class,
        DbStatusReaction::class,
        DbPagingTimeline::class,
        DbUrlEntity::class,
        DbSearch::class,
    ],
    version = 3,
)
@TypeConverters(
    MicroBlogKeyConverter::class,
    PlatformTypeConverter::class,
    MediaTypeConverter::class,
    TimelineTypeConverter::class,
    UserTimelineTypeConverter::class,
    StringListConverter::class,
    MicroBlogTypeConverter::class,
)
abstract class CacheDatabase : RoomDatabase() {
    abstract fun statusDao(): StatusDao
    abstract fun timelineDao(): TimelineDao
    abstract fun mediaDao(): MediaDao
    abstract fun userDao(): UserDao
    abstract fun reactionDao(): ReactionDao
    abstract fun pagingTimelineDao(): PagingTimelineDao
    abstract fun urlEntityDao(): UrlEntityDao
    abstract fun searchDao(): SearchDao
}
