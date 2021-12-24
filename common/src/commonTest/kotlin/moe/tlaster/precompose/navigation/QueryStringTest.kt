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
package moe.tlaster.precompose.navigation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class QueryStringTest {
    @Test
    fun simpleQueryString() {
        QueryString("&foo=bar").let {
            assertTrue(it.map.size == 1)
            assertTrue(it.map.containsKey("foo"))
            assertTrue(it.map.containsValue(listOf("bar")))
            assertEquals("bar", it.query("foo"))
        }
        QueryString("foo=bar&").let {
            assertTrue(it.map.size == 1)
            assertTrue(it.map.containsKey("foo"))
            assertTrue(it.map.containsValue(listOf("bar")))
            assertEquals("bar", it.query("foo"))
        }
        QueryString("foo=bar&&").let {
            assertTrue(it.map.size == 1)
            assertTrue(it.map.containsKey("foo"))
            assertTrue(it.map.containsValue(listOf("bar")))
            assertEquals("bar", it.query("foo"))
        }
        QueryString("foo=bar").let {
            assertTrue(it.map.size == 1)
            assertTrue(it.map.containsKey("foo"))
            assertTrue(it.map.containsValue(listOf("bar")))
            assertEquals("bar", it.query("foo"))
        }
        QueryString("a=1&b=2").let {
            assertTrue(it.map.size == 2)
            assertTrue(it.map.containsKey("a"))
            assertTrue(it.map.containsValue(listOf("1")))
            assertTrue(it.map.containsKey("b"))
            assertTrue(it.map.containsValue(listOf("2")))
            assertEquals("1", it.query("a"))
            assertEquals("2", it.query("b"))
        }
        QueryString("a=1&b=2&").let {
            assertTrue(it.map.size == 2)
            assertTrue(it.map.containsKey("a"))
            assertTrue(it.map.containsValue(listOf("1")))
            assertTrue(it.map.containsKey("b"))
            assertTrue(it.map.containsValue(listOf("2")))
            assertEquals("1", it.query("a"))
            assertEquals("2", it.query("b"))
        }
        QueryString("a=1&&b=2&").let {
            assertTrue(it.map.size == 2)
            assertTrue(it.map.containsKey("a"))
            assertTrue(it.map.containsValue(listOf("1")))
            assertTrue(it.map.containsKey("b"))
            assertTrue(it.map.containsValue(listOf("2")))
            assertEquals("1", it.query("a"))
            assertEquals("2", it.query("b"))
        }
        QueryString("a=1&a=2").let {
            assertTrue(it.map.size == 1)
            assertTrue(it.map.containsKey("a"))
            assertTrue(it.map.containsValue(listOf("1", "2")))
            assertEquals(listOf("1", "2"), it.queryList<String>("a").filterNotNull())
        }
        QueryString("a=1;a=2").let {
            assertTrue(it.map.isEmpty())
        }
        QueryString("a=").let {
            assertTrue(it.map.size == 1)
            assertTrue(it.map.containsKey("a"))
            assertEquals(emptyList<String>(), it.queryList<String>("a").filterNotNull())
        }
        QueryString("a=&").let {
            assertTrue(it.map.size == 1)
            assertTrue(it.map.containsKey("a"))
            assertEquals(emptyList<String>(), it.queryList<String>("a").filterNotNull())
        }
        QueryString("a=&&").let {
            assertTrue(it.map.size == 1)
            assertTrue(it.map.containsKey("a"))
            assertEquals(emptyList<String>(), it.queryList<String>("a").filterNotNull())
        }
        QueryString("").let {
            assertTrue(it.map.isEmpty())
        }
    }
}
