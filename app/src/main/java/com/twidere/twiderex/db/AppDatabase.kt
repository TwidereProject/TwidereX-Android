package com.twidere.twiderex.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.twidere.twiderex.db.dao.StatusDao
import com.twidere.twiderex.db.dao.TimelineDao
import com.twidere.twiderex.db.model.DbStatus
import com.twidere.twiderex.db.model.DbTimeline
import com.twidere.twiderex.db.model.converter.PlatformTypeConverter
import com.twidere.twiderex.db.model.converter.UserKeyConverter

@Database(
    entities = [
        DbStatus::class,
        DbTimeline::class,
    ],
    version = 1,
)
@TypeConverters(
    UserKeyConverter::class,
    PlatformTypeConverter::class,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun statusDao(): StatusDao
    abstract fun timelineDao(): TimelineDao
}