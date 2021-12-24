/*
 *  Twidere X
 *
 *  Copyright (C) TwidereProject and Contributors
 * 
 *  This file is part of Twidere X.
 * 
 *  Twidere X is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Twidere X is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Twidere X. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.services.twitter.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class MediaSize(
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
            "fit" -> Fit
            else -> throw IllegalArgumentException("Resize could not parse: $value")
        }
        override fun serialize(encoder: Encoder, value: Resize) {
            return encoder.encodeString(value.value)
        }
    }
}
