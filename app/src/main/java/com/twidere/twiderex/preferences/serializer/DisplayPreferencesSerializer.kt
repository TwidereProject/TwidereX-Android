package com.twidere.twiderex.preferences.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.twidere.twiderex.preferences.proto.DisplayPreferences
import java.io.InputStream
import java.io.OutputStream

object DisplayPreferencesSerializer : Serializer<DisplayPreferences> {
    override val defaultValue: DisplayPreferences
        get() = DisplayPreferences
            .getDefaultInstance()
            .toBuilder()
            .setMediaPreview(true)
            .setUseSystemFontSize(true)
            .setFontScale(1F)
            .build()

    override fun readFrom(input: InputStream): DisplayPreferences {
        try {
            return DisplayPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override fun writeTo(t: DisplayPreferences, output: OutputStream) = t.writeTo(output)
}