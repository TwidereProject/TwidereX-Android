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

import moe.tlaster.precompose.navigation.route.Route
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RouteParserTest2 {
    @Test
    fun pathKeys() {
        pathKeys("/{lang:[a-z]{2}}") { keys -> assertEquals(listOf("lang"), keys) }

        pathKeys("/edit/{id}?") { keys -> assertEquals(listOf("id"), keys) }

        pathKeys("/path/{id}/{start}?/{end}?") { keys -> assertEquals(listOf("id", "start", "end"), keys) }

        pathKeys("/*") { keys -> assertEquals(1, keys.size) }

        pathKeys("/foo/?*") { keys -> assertEquals(1, keys.size) }

        pathKeys("/foo") { keys -> assertEquals(0, keys.size) }
        pathKeys("/") { keys -> assertEquals(0, keys.size) }
        pathKeys("/foo/bar") { keys -> assertEquals(0, keys.size) }
        pathKeys("/foo/*") { keys -> assertEquals(1, keys.size) }
        pathKeys("/foo/*name") { keys -> assertEquals(1, keys.size) }
        pathKeys("/foo/{x}") { keys -> assertEquals(1, keys.size) }

        pathKeys("aaa://{lang:[a-z]{2}}") { keys -> assertEquals(listOf("lang"), keys) }
        pathKeys("bbb://path/{id}/{start}?/{end}?") { keys -> assertEquals(listOf("id", "start", "end"), keys) }
    }

    @Test
    fun pathKeyMap() {
        pathKeyMap("/{lang:[a-z]{2}}") { map -> assertEquals("[a-z]{2}", map["lang"]) }
        pathKeyMap("/{id:[0-9]+}") { map -> assertEquals("[0-9]+", map["id"]) }
        pathKeyMap("/edit/{id}?") { keys -> assertEquals(null, keys["id"]) }
        pathKeyMap("/path/{id}/{start}?/{end}?") { keys ->
            assertEquals(null, keys["id"])
            assertEquals(null, keys["start"])
            assertEquals(null, keys["end"])
        }
        pathKeyMap("/*") { keys -> assertEquals("\\.*", keys["*"]) }
        pathKeyMap("/foo/?*") { keys -> assertEquals("\\.*", keys["*"]) }
        pathKeyMap("/foo/*name") { keys -> assertEquals("\\.*", keys["name"]) }

        pathKeyMap("aaa://foo/?*") { keys -> assertEquals("\\.*", keys["*"]) }
        pathKeyMap("bbb://foo/*name") { keys -> assertEquals("\\.*", keys["name"]) }
    }

    private fun pathKeys(pattern: String, consumer: (List<String>) -> Unit) {
        consumer.invoke(RouteParser.pathKeys(pattern))
    }

    private fun pathKeyMap(pattern: String, consumer: (Map<String, String?>) -> Unit) {
        val map: MutableMap<String, String?> = HashMap()
        RouteParser.pathKeys(pattern) { key: String, value: String? -> map[key] = value }
        consumer.invoke(map)
    }

    @Test
    fun wildOnRoot() {
        val parser = RouteParser()
        parser.insert(route("/foo/?*", "foo"))
        parser.insert(route("/bar/*", "bar"))
        parser.insert(route("/*", "root"))
        parser.find("/").let {
            assertNotNull(it)
            assertTrue(it.route is TestRoute)
            assertEquals("root", it.route.id)
        }
        parser.find("/foo").let {
            assertNotNull(it)
            assertTrue(it.route is TestRoute)
            assertEquals("foo", it.route.id)
        }
        parser.find("/bar").let {
            assertNotNull(it)
            assertTrue(it.route is TestRoute)
            assertEquals("root", it.route.id)
        }
        parser.find("/foox").let {
            assertNotNull(it)
            assertTrue(it.route is TestRoute)
            assertEquals("root", it.route.id)
        }
        parser.find("/foo/").let {
            assertNotNull(it)
            assertTrue(it.route is TestRoute)
            assertEquals("foo", it.route.id)
        }
        parser.find("/foo/x").let {
            assertNotNull(it)
            assertTrue(it.route is TestRoute)
            assertEquals("foo", it.route.id)
        }
        parser.find("/bar/x").let {
            assertNotNull(it)
            assertTrue(it.route is TestRoute)
            assertEquals("bar", it.route.id)
        }
    }

    @Test
    fun searchString() {
        val parser = RouteParser()
        parser.insert(route("/regex/{nid:[0-9]+}", "nid"))
        parser.insert(route("/regex/{zid:[0-9]+}/edit", "zid"))
        parser.insert(route("/articles/{id}", "id"))
        parser.insert(route("/articles/*", "*"))

        parser.find("/regex/678/edit").let {
            assertNotNull(it)
            assertTrue(it.route is TestRoute)
            assertEquals("zid", it.route.id)
        }
        parser.find("/articles/tail/match").let {
            assertNotNull(it)
            assertTrue(it.route is TestRoute)
            assertEquals("*", it.route.id)
        }
        parser.find("/articles/123").let {
            assertNotNull(it)
            assertTrue(it.route is TestRoute)
            assertEquals("id", it.route.id)
        }
    }

    @Test
    fun searchParam() {
        val parser = RouteParser()
        parser.insert(route("/articles/{id}", "id"))
        parser.insert(route("/articles/*", "catchall"))

        parser.find("/articles/123").let {
            assertNotNull(it)
            assertTrue(it.route is TestRoute)
            assertEquals("id", it.route.id)
        }
        parser.find("/articles/a/b").let {
            assertNotNull(it)
            assertTrue(it.route is TestRoute)
            assertEquals("catchall", it.route.id)
        }
    }

    @Test
    fun multipleRegex() {
        val parser = RouteParser()
        parser.insert(route("/{lang:[a-z][a-z]}/{page:[^.]+}/", "1515"))

        parser.find("/12/f/").let {
            assertNull(it)
        }
        parser.find("/ar/page/").let {
            assertNotNull(it)
            assertTrue(it.route is TestRoute)
            assertEquals("1515", it.route.id)
        }
        parser.find("/arx/page/").let {
            assertNull(it)
        }
    }

    @Test
    fun regexWithQuantity() {
        val parser = RouteParser()
        parser.insert(route("/{lang:[a-z]{2}}/", "qx"))
        parser.find("/12/").let {
            assertNull(it)
        }
        parser.find("/ar/").let {
            assertNotNull(it)
            assertTrue(it.route is TestRoute)
            assertEquals("qx", it.route.id)
        }
    }

    @Test
    fun withSchema() {
        val parser = RouteParser()
        parser.insert(route("aaa://home", "1"))
        parser.insert(route("bbb://home", "2"))
        parser.find("bbb://home").let {
            assertNotNull(it)
            assertTrue(it.route is TestRoute)
            assertEquals("2", it.route.id)
        }
    }

    @Test
    fun withSchemaAndRegex() {
        val parser = RouteParser()
        parser.insert(route("aaa://home", "1"))
        parser.insert(route("bbb://home", "2"))
        parser.insert(route("aaa://home/{id:[0-9]+}", "3"))
        parser.find("aaa://home/1232").let {
            assertNotNull(it)
            assertTrue(it.route is TestRoute)
            assertEquals("3", it.route.id)
        }
    }

    @Test
    fun shouldExpandOptionalParams() {
        RouteParser.expandOptionalVariables("/{lang:[a-z]{2}}?").let { paths ->
            assertEquals(2, paths.size)
            assertEquals("/", paths.get(0))
            assertEquals("/{lang:[a-z]{2}}", paths.get(1))
        }
        RouteParser.expandOptionalVariables("/{lang:[a-z]{2}}").let { paths ->
            assertEquals(1, paths.size)
            assertEquals("/{lang:[a-z]{2}}", paths.get(0))
        }
        RouteParser.expandOptionalVariables("/edit/{id:[0-9]+}?").let { paths ->
            assertEquals(2, paths.size)
            assertEquals("/edit", paths.get(0))
            assertEquals("/edit/{id:[0-9]+}", paths.get(1))
        }
        RouteParser.expandOptionalVariables("/path/{id}/{start}?/{end}?").let { paths ->
            assertEquals(3, paths.size)
            assertEquals("/path/{id}", paths.get(0))
            assertEquals("/path/{id}/{start}", paths.get(1))
            assertEquals("/path/{id}/{start}/{end}", paths.get(2))
        }
        RouteParser.expandOptionalVariables("/{id}?/suffix").let { paths ->
            assertEquals(3, paths.size)
            assertEquals("/", paths.get(0))
            assertEquals("/{id}/suffix", paths.get(1))
            assertEquals("/suffix", paths.get(2))
        }
        RouteParser.expandOptionalVariables("/prefix/{id}?").let { paths ->
            assertEquals(2, paths.size)
            assertEquals("/prefix", paths.get(0))
            assertEquals("/prefix/{id}", paths.get(1))
        }
        RouteParser.expandOptionalVariables("/{id}?").let { paths ->
            assertEquals(2, paths.size)
            assertEquals("/", paths.get(0))
            assertEquals("/{id}", paths.get(1))
        }
        RouteParser.expandOptionalVariables("/path").let { paths ->
            assertEquals(1, paths.size)
            assertEquals("/path", paths.get(0))
        }
        RouteParser.expandOptionalVariables("/path/subpath").let { paths ->
            assertEquals(1, paths.size)
            assertEquals("/path/subpath", paths.get(0))
        }
        RouteParser.expandOptionalVariables("/{id}").let { paths ->
            assertEquals(1, paths.size)
            assertEquals("/{id}", paths.get(0))
        }
        RouteParser.expandOptionalVariables("/{id}/suffix").let { paths ->
            assertEquals(1, paths.size)
            assertEquals("/{id}/suffix", paths.get(0))
        }
        RouteParser.expandOptionalVariables("/prefix/{id}").let { paths ->
            assertEquals(1, paths.size)
            assertEquals("/prefix/{id}", paths.get(0))
        }
        RouteParser.expandOptionalVariables("/").let { paths ->
            assertEquals(1, paths.size)
            assertEquals("/", paths.get(0))
        }
        RouteParser.expandOptionalVariables("").let { paths ->
            assertEquals(1, paths.size)
            assertEquals("/", paths.get(0))
        }
    }

    private fun route(path: String, id: String): Route {
        return TestRoute(path, id)
    }
}
