package com.twidere.twiderex.db.model.converter

import androidx.room.TypeConverter
import com.twidere.twiderex.model.MediaType


class MediaTypeConverter {
    @TypeConverter
    fun fromMediaType(mediaType: MediaType?): String? {
        return mediaType?.name
    }

    @TypeConverter
    fun fromString(string: String?): MediaType? {
        return string?.let { MediaType.valueOf(it) }
    }
}