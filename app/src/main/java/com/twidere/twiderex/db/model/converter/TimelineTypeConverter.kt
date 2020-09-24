package com.twidere.twiderex.db.model.converter

import androidx.room.TypeConverter
import com.twidere.twiderex.db.model.TimelineType


class TimelineTypeConverter {
    @TypeConverter
    fun fromPlatformType(timelineType: TimelineType?): String? {
        return timelineType?.name
    }

    @TypeConverter
    fun fromString(string: String?): TimelineType? {
        return string?.let { TimelineType.valueOf(it) }
    }
}