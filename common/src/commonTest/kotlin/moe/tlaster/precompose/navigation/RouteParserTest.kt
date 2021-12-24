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

import moe.tlaster.precompose.navigation.route.SceneRoute
import org.junit.Assert
import kotlin.test.Test

class RouteParserTest {
    @Test
    fun findRoute_MatchPathAndParams() {
        val parser = RouteParser()
        listOf(
            buildRoute("home"),
            buildRoute("user/{userId}"),
            buildRoute("twitter/{screenName}/status/{statusId}"),
            buildRoute(
                "twitter/status/{statusId}",
                deepLink = listOf(
                    "https://twitter.com/{screenName}/status/{statusId:[0-9]+}",
                    "twidereX://deeplink/status/{statusId}"
                )
            )
        )
            .map { route ->
                RouteParser.expandOptionalVariables(route.route).let {
                    it + route.deepLinks.flatMap {
                        RouteParser.expandOptionalVariables(it)
                    }
                } to route
            }
            .flatMap { it.first.map { route -> route to it.second } }.forEach {
                parser.insert(it.first, it.second)
            }
        Assert.assertEquals("home", parser.find("home")?.route?.route)
        val matchResultSingleParam = parser.find("user/123456")
        Assert.assertEquals("user/{userId}", matchResultSingleParam?.route?.route)
        Assert.assertEquals("123456", matchResultSingleParam?.pathMap?.get("userId"))

        val matchResultMultiParams = parser.find("twitter/testName/status/456")
        Assert.assertEquals("twitter/{screenName}/status/{statusId}", matchResultMultiParams?.route?.route)
        Assert.assertEquals("testName", matchResultMultiParams?.pathMap?.get("screenName"))
        Assert.assertEquals("456", matchResultMultiParams?.pathMap?.get("statusId"))

        val matchResultDeepLinks = parser.find("https://twitter.com/testName2/status/789")
        Assert.assertEquals("twitter/status/{statusId}", matchResultDeepLinks?.route?.route)
        Assert.assertEquals("testName2", matchResultDeepLinks?.pathMap?.get("screenName"))
        Assert.assertEquals("789", matchResultDeepLinks?.pathMap?.get("statusId"))

        val matchResultRegex = parser.find("https://twitter.com/testName2/status/789abc")
        Assert.assertEquals(null, matchResultRegex?.route)

        val matchResultMultiParamsDeepLinks = parser.find("twidereX://deeplink/status/1234")
        Assert.assertEquals("twitter/status/{statusId}", matchResultMultiParamsDeepLinks?.route?.route)
        Assert.assertEquals(null, matchResultMultiParamsDeepLinks?.pathMap?.get("screenName"))
        Assert.assertEquals("1234", matchResultMultiParamsDeepLinks?.pathMap?.get("statusId"))
    }

    @Test
    fun route_MatchGenerated() {
        val parser = RouteParser()
        listOf(
            buildRoute("/home"),
            buildRoute("/user/{userId}"),
            buildRoute("/twitter/{screenName}/status/{statusId}"),
            buildRoute(
                "/twitter/status/{statusId}",
                deepLink = listOf(
                    "https://twitter.com/{screenName}/status/{statusId:[0-9]+}",
                    "twidereX:///deeplink/status/{statusId}"
                )
            )
        )
            .map { route ->
                RouteParser.expandOptionalVariables(route.route).let {
                    it + route.deepLinks.flatMap {
                        RouteParser.expandOptionalVariables(it)
                    }
                } to route
            }
            .flatMap { it.first.map { route -> route to it.second } }.forEach {
                parser.insert(it.first, it.second)
            }

        Assert.assertEquals("/home", parser.find("/home")?.route?.route)
        val matchResultSingleParam = parser.find("/user/123456")
        Assert.assertEquals("/user/{userId}", matchResultSingleParam?.route?.route)
        Assert.assertEquals("123456", matchResultSingleParam?.pathMap?.get("userId"))

        val matchResultMultiParams = parser.find("/twitter/testName/status/456")
        Assert.assertEquals("/twitter/{screenName}/status/{statusId}", matchResultMultiParams?.route?.route)
        Assert.assertEquals("testName", matchResultMultiParams?.pathMap?.get("screenName"))
        Assert.assertEquals("456", matchResultMultiParams?.pathMap?.get("statusId"))

        val matchResultDeepLinks = parser.find("https://twitter.com/testName2/status/789")
        Assert.assertEquals("/twitter/status/{statusId}", matchResultDeepLinks?.route?.route)
        Assert.assertEquals("testName2", matchResultDeepLinks?.pathMap?.get("screenName"))
        Assert.assertEquals("789", matchResultDeepLinks?.pathMap?.get("statusId"))

        val matchResultRegex = parser.find("https://twitter.com/testName2/status/789abc")
        Assert.assertEquals(null, matchResultRegex?.route)

        val matchResultMultiParamsDeepLinks = parser.find("twidereX:///deeplink/status/1234")
        Assert.assertEquals("/twitter/status/{statusId}", matchResultMultiParamsDeepLinks?.route?.route)
        Assert.assertEquals(null, matchResultMultiParamsDeepLinks?.pathMap?.get("screenName"))
        Assert.assertEquals("1234", matchResultMultiParamsDeepLinks?.pathMap?.get("statusId"))
    }

    private fun buildRoute(route: String, deepLink: List<String> = emptyList()): SceneRoute {
        return SceneRoute(route = route, deepLinks = deepLink, navTransition = null, content = {})
    }
}
