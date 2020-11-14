package com.twidere.twiderex.db.model.converter

import androidx.room.TypeConverter
import com.twidere.twiderex.db.model.UserTimelineType

class UserTimelineTypeConverter {
    @TypeConverter
    fun fromPlatformType(timelineType: UserTimelineType?): String? {
        return timelineType?.name
    }

    @TypeConverter
    fun fromString(string: String?): UserTimelineType? {
        return string?.let { UserTimelineType.valueOf(it) }
    }
}