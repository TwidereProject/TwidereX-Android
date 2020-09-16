package com.twidere.twiderex.db.model.converter

import androidx.room.TypeConverter
import com.twidere.twiderex.model.UserKey

class UserKeyConverter {
    @TypeConverter
    fun fromUserKey(userKey: UserKey?): String? {
        return userKey?.toString()
    }

    @TypeConverter
    fun fromString(string: String?): UserKey? {
        return string?.let { UserKey.valueOf(it) }
    }
}