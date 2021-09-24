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
package com.twidere.twiderex.sqldelight.adapter

import com.twidere.twiderex.db.sqldelight.adapter.ComposeTypeColumnAdapter
import com.twidere.twiderex.model.enums.ComposeType
import org.junit.Test
import kotlin.test.assertEquals

internal class ComposeTypeColumnAdapterTest {
    private val adapter = ComposeTypeColumnAdapter()
    @Test
    fun decode_TypeMatchesToName() {
        assertEquals(ComposeType.New, adapter.decode("New"))
        assertEquals(ComposeType.Quote, adapter.decode("Quote"))
        assertEquals(ComposeType.Reply, adapter.decode("Reply"))
        assertEquals(ComposeType.Thread, adapter.decode("Thread"))
    }

    @Test
    fun encode_NameMatchesToType() {
        assertEquals("New", adapter.encode(ComposeType.New))
        assertEquals("Quote", adapter.encode(ComposeType.Quote))
        assertEquals("Reply", adapter.encode(ComposeType.Reply))
        assertEquals("Thread", adapter.encode(ComposeType.Thread))
    }
}
