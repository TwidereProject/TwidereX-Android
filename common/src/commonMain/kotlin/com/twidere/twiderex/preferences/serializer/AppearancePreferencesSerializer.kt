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
package com.twidere.twiderex.preferences.serializer

import androidx.datastore.core.Serializer
import com.twidere.twiderex.preferences.model.AppearancePreferences
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.InputStream
import java.io.OutputStream

@OptIn(ExperimentalSerializationApi::class)
object AppearancePreferencesSerializer : Serializer<AppearancePreferences> {
    override suspend fun readFrom(input: InputStream): AppearancePreferences {
        return ProtoBuf.decodeFromByteArray(input.readBytes())
    }
    override suspend fun writeTo(
        t: AppearancePreferences,
        output: OutputStream
    ) = output.write(ProtoBuf.encodeToByteArray(t))

    override val defaultValue: AppearancePreferences
        get() = AppearancePreferences()
}
