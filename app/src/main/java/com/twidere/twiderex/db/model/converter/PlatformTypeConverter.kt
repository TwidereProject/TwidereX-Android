package com.twidere.twiderex.db.model.converter

import androidx.room.TypeConverter
import com.twidere.twiderex.model.PlatformType

class PlatformTypeConverter {
    @TypeConverter
    fun fromPlatformType(platformType: PlatformType?): String? {
        return platformType?.name
    }

    @TypeConverter
    fun fromString(string: String?): PlatformType? {
        return string?.let { PlatformType.valueOf(it) }
    }
}