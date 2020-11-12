package com.twidere.twiderex.preferences.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.twidere.twiderex.preferences.proto.AppearancePreferences
import java.io.InputStream
import java.io.OutputStream

object AppearancePreferencesSerializer : Serializer<AppearancePreferences> {
    override fun readFrom(input: InputStream): AppearancePreferences {
        try {
            return AppearancePreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override fun writeTo(
        t: AppearancePreferences,
        output: OutputStream
    ) = t.writeTo(output)

    override val defaultValue: AppearancePreferences
        get() = AppearancePreferences
            .getDefaultInstance()
            .toBuilder()
            .setTapPosition(AppearancePreferences.TabPosition.Bottom)
            .build()
}
