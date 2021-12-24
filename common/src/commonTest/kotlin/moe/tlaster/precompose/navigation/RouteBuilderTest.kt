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
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class RouteBuilderTest {
    @Test
    fun testInitialRouteNotInRoutes() {
        assertFailsWith(IllegalArgumentException::class, "No initial route target fot this route graph") {
            RouteBuilder("/home").build()
        }
        assertFailsWith(IllegalArgumentException::class, "No initial route target fot this route graph") {
            RouteBuilder("/home").apply {
                testRoute("/detail", "1")
            }.build()
        }
    }

    @Test
    fun testEmptyRoute() {
        val graph = RouteBuilder("").build()
        assertTrue(graph.routes.isEmpty())
    }

    @Test
    fun testSingleRoute() {
        RouteBuilder("/home").apply {
            testRoute("/home", "home")
        }.build().apply {
            assertTrue(routes.size == 1)
            routes.first().let {
                assertTrue(it is TestRoute)
                assertEquals("/home", it.route)
                assertEquals("home", it.id)
            }
        }
    }

    @Test
    fun testMultipleRouteWithSameRoute() {
        assertFailsWith(IllegalArgumentException::class, "Duplicate route can not be applied") {
            RouteBuilder("/home").apply {
                testRoute("/home", "home")
                testRoute("/home", "home")
            }.build()
        }
    }
}
