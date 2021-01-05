/*
 *  Twidere X
 *
 *  Copyright (C) 2020-2021 Tlaster <tlaster@outlook.com>
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
package com.twidere.services.mastodon.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = Visibility.Companion::class)
enum class Visibility(val value: String) {
    Public("public"),
    Unlisted("unlisted"),
    Private("private"),
    Direct("direct");

    companion object : KSerializer<Visibility> {
        override val descriptor: SerialDescriptor
            get() {
                return PrimitiveSerialDescriptor("quicktype.Visibility", PrimitiveKind.STRING)
            }
        override fun deserialize(decoder: Decoder): Visibility = when (val value = decoder.decodeString()) {
            "public" -> Public
            "unlisted" -> Unlisted
            "private" -> Private
            "direct" -> Direct
            else -> throw IllegalArgumentException("Visibility could not parse: $value")
        }
        override fun serialize(encoder: Encoder, value: Visibility) {
            return encoder.encodeString(value.value)
        }
    }
}
