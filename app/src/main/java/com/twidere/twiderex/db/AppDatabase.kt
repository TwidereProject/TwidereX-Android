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
abstract class AppDatabase : RoomDatabase() {
    abstract fun statusDao(): StatusDao
    abstract fun timelineDao(): TimelineDao
    abstract fun mediaDao(): MediaDao
    abstract fun userDao(): UserDao
}