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

import moe.tlaster.precompose.navigation.route.ComposeRoute
import kotlin.math.min

internal class RouteMatch {
    var matches = false
    var route: ComposeRoute? = null
    var vars = arrayListOf<String>()
    var keys = arrayListOf<String>()
    var pathMap = linkedMapOf<String, String>()
    fun key() {
        val size = min(keys.size, vars.size)
        for (i in 0 until size) {
            pathMap[keys[i]] = vars[i]
        }
        for (i in 0 until size) {
            vars.removeFirst()
        }
    }

    fun truncate(size: Int) {
        var sizeInt = size
        while (sizeInt < vars.size) {
            vars.removeAt(sizeInt++)
        }
    }

    fun value(value: String) {
        vars.add(value)
    }

    fun pop() {
        if (vars.isNotEmpty()) {
            vars.removeLast()
        }
    }

    fun found(route: ComposeRoute): RouteMatch {
        this.route = route
        matches = true
        return this
    }
}
