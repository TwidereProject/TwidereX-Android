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

import kotlinx.serialization.Serializable
import org.junit.Test
import kotlin.test.assertEquals
@Serializable
private data class JsonObject(
    val arg1: String,
    val arg2: Int,
    val arg3: Boolean
)
class JsonColumnAdapterTest {

    @Test
    fun decodeAndEncodeDataClass() {
        val adapter = JsonColumnAdapter(JsonObject.serializer())
        val origin = JsonObject(arg1 = "test", arg2 = 2, arg3 = true)
        val string = adapter.encode(origin)
        val obj = adapter.decode(string)
        assertEquals(origin.arg1, obj.arg1)
        assertEquals(origin.arg2, obj.arg2)
        assertEquals(origin.arg3, obj.arg3)
    }
}
