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
package com.twidere.services.api.proxy

import com.twidere.services.proxy.ReverseProxyHandler
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ReverseProxyInterceptorTest {
    @Test
    fun testReplaceUrl() {
        val format1 = "https://proxy.com/[SCHEME]/[AUTHORITY]/[PATH][?QUERY][#FRAGMENT]"
        val format2 = "https://proxy.com/[AUTHORITY]/[PATH][?QUERY][#FRAGMENT]"
        val format3 = "https://proxy.com/[AUTHORITY][/PATH][?QUERY][#FRAGMENT]"
        val url1 = "https://example.com:8080/path?query=value#fragment".toHttpUrlOrNull()!!
        val url2 = "https://example.com:8080/path?query=value".toHttpUrlOrNull()!!
        val url3 = "https://example.com:8080/path#fragment".toHttpUrlOrNull()!!
        val url4 = "https://example.com:8080/path".toHttpUrlOrNull()!!
        val url5 = "https://example.com/path".toHttpUrlOrNull()!!

        assertEquals(
            "https://proxy.com/https/example.com%3A8080/path?query=value#fragment",
            ReverseProxyHandler.replaceUrl(url1, format1)
        )
        assertEquals(
            "https://proxy.com/example.com%3A8080/path?query=value#fragment",
            ReverseProxyHandler.replaceUrl(url1, format2)
        )
        assertEquals(
            "https://proxy.com/example.com%3A8080/path?query=value#fragment",
            ReverseProxyHandler.replaceUrl(url1, format3)
        )
        assertEquals(
            "https://proxy.com/https/example.com%3A8080/path?query=value",
            ReverseProxyHandler.replaceUrl(url2, format1)
        )
        assertEquals(
            "https://proxy.com/https/example.com%3A8080/path#fragment",
            ReverseProxyHandler.replaceUrl(url3, format1)
        )
        assertEquals(
            "https://proxy.com/https/example.com%3A8080/path",
            ReverseProxyHandler.replaceUrl(url4, format1)
        )
        assertEquals(
            "https://proxy.com/https/example.com/path",
            ReverseProxyHandler.replaceUrl(url5, format1)
        )
    }
}
