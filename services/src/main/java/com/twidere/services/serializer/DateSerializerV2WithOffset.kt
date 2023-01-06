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
package com.twidere.services.serializer

import com.twidere.services.utils.DateFormatUtils
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.joda.time.DateTime

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = DateTime::class)
internal object DateSerializerV2WithOffset : KSerializer<DateTime> {
  override val descriptor: SerialDescriptor
    get() = PrimitiveSerialDescriptor("DateTime", PrimitiveKind.STRING)

  override fun deserialize(decoder: Decoder): DateTime {
    val str = decoder.decodeString()
    return DateFormatUtils.parse(str)
  }

  override fun serialize(encoder: Encoder, value: DateTime) {
    encoder.encodeString(DateFormatUtils.format(value))
  }
}
