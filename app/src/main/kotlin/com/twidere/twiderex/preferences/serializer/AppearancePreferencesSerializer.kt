/*
 *  Twidere X
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
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
