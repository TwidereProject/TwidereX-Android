package com.twidere.services.twitter.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class MediaSize (
    val w: Long? = null,
    val h: Long? = null,
    val resize: Resize? = null
)
@Serializable(with = Resize.Companion::class)
enum class Resize(val value: String) {
    Crop("crop"),
    Fit("fit");

    companion object : KSerializer<Resize> {
        override val descriptor: SerialDescriptor
            get() {
                return PrimitiveSerialDescriptor(
                    "quicktype.Resize",
                    PrimitiveKind.STRING
                )
            }
        override fun deserialize(decoder: Decoder): Resize = when (val value = decoder.decodeString()) {
            "crop" -> Crop
            "fit"  -> Fit
            else   -> throw IllegalArgumentException("Resize could not parse: $value")
        }
        override fun serialize(encoder: Encoder, value: Resize) {
            return encoder.encodeString(value.value)
        }
    }
}
