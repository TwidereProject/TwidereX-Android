package com.twidere.services.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.text.SimpleDateFormat
import java.util.*

@Serializer(forClass = Date::class)
internal object DateSerializer : KSerializer<Date> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Date", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Date {
        val str = decoder.decodeString()
        return getDateFormat().parse(str)
    }

    override fun serialize(encoder: Encoder, value: Date) {
        encoder.encodeString(getDateFormat().format(value))
    }

    private fun getDateFormat(): SimpleDateFormat {
        val format = SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.ENGLISH)
        format.isLenient = true
        format.timeZone = TimeZone.getTimeZone("UTC")
        return format
    }
}