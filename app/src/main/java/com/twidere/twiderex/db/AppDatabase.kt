/*
 *  TwidereX
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of TwidereX.
 * 
 *  TwidereX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  TwidereX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with TwidereX. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.twidere.twiderex.db.dao.MediaDao
import com.twidere.twiderex.db.dao.StatusDao
import com.twidere.twiderex.db.dao.TimelineDao
import com.twidere.twiderex.db.dao.UserDao
import com.twidere.twiderex.db.model.DbMedia
import com.twidere.twiderex.db.model.DbStatus
import com.twidere.twiderex.db.model.DbTimeline
import com.twidere.twiderex.db.model.DbUser
import com.twidere.twiderex.db.model.converter.MediaTypeConverter
import com.twidere.twiderex.db.model.converter.PlatformTypeConverter
import com.twidere.twiderex.db.model.converter.TimelineTypeConverter
import com.twidere.twiderex.db.model.converter.UserKeyConverter
import javax.inject.Singleton

@Singleton
@Database(
    entities = [
        DbStatus::class,
        DbTimeline::class,
        DbMedia::class,
        DbUser::class,
    ],
    version = 1,
)
@TypeConverters(
    UserKeyConverter::class,
    PlatformTypeConverter::class,
    MediaTypeConverter::class,
    TimelineTypeConverter::class,
)
abstract class AppDatabase : RoomDatabase(), ITimelineDatabase {
    abstract override fun statusDao(): StatusDao
    abstract override fun timelineDao(): TimelineDao
    abstract override fun mediaDao(): MediaDao
    abstract override fun userDao(): UserDao
}
