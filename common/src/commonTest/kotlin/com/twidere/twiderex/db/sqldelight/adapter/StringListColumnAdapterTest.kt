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

import org.junit.Test
import kotlin.test.assertEquals

internal class StringListColumnAdapterTest {
    @Test
    fun decode_splitStringWithGivenSeparator() {
        var adapter = StringListColumnAdapter(";")
        val originString = "a,b,c,d"
        var list = adapter.decode(originString)
        assertEquals(1, list.size)
        assertEquals(originString, list.first())
        adapter = StringListColumnAdapter(",")
        list = adapter.decode(originString)
        list.forEachIndexed { index, s ->
            when (index) {
                0 -> assertEquals("a", s)
                1 -> assertEquals("b", s)
                2 -> assertEquals("c", s)
                3 -> assertEquals("d", s)
            }
        }
    }

    @Test
    fun encode_combineListContentToStringWithGivenSeparator() {
        var adapter = StringListColumnAdapter(";")
        val originList = listOf("a", "b", "c", "d")
        var string = adapter.encode(originList)
        assertEquals("a;b;c;d", string)
        adapter = StringListColumnAdapter("|")
        string = adapter.encode(originList)
        assertEquals("a|b|c|d", string)
    }
}
