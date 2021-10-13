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
package com.twidere.twiderex.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// TODO Make it private after all android code migrate to common
val JSON by lazy {
    Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }
}
// TODO Make it internal after all android code migrate to common
@OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
inline fun <reified T> T.json(): String =
    JSON.encodeToString<T>(this)

@OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
inline fun <reified T> String.fromJson() =
    JSON.decodeFromString<T>(this)

fun <T> T.json(serializer: KSerializer<T>) = JSON.encodeToString(serializer, this)

fun <T> String.fromJson(serializer: KSerializer<T>) = JSON.decodeFromString(serializer, this)
