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
package com.twidere.twiderex.db.sqldelight.adapter

import com.squareup.sqldelight.ColumnAdapter
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.utils.fromJson
import com.twidere.twiderex.utils.json
import kotlinx.serialization.KSerializer

internal class StringListColumnAdapter(private val separator: String = ",") : ColumnAdapter<List<String>, String> {
    override fun decode(databaseValue: String) =
        if (databaseValue.isEmpty()) {
            listOf()
        } else {
            databaseValue.split(separator)
        }

    override fun encode(value: List<String>) = value.joinToString(separator = separator) { it }
}

internal class MicroBlogKeyColumnAdapter : ColumnAdapter<MicroBlogKey, String> {
    override fun decode(databaseValue: String) = MicroBlogKey.valueOf(databaseValue)

    override fun encode(value: MicroBlogKey) = value.toString()
}

internal class JsonColumnAdapter<T : Any>(private val serializer: KSerializer<T>) : ColumnAdapter<T, String> {
    override fun decode(databaseValue: String) = databaseValue.fromJson(serializer)

    override fun encode(value: T) = value.json(serializer)
}
