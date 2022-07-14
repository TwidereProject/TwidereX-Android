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
package com.twidere.twiderex.navigation

import androidx.compose.runtime.Composable
import moe.tlaster.precompose.navigation.BackStackEntry
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.transition.NavTransition

interface IRoute {
    val route: String
}

fun List<Any>.mapToString() = map {
    when (it) {
        is String -> it
        is IRoute -> it.route
        else -> it.toString()
    }
}

fun RouteBuilder.scene(
    route: IRoute,
    deepLinks: List<Any> = emptyList(),
    navTransition: NavTransition? = null,
    content: @Composable (BackStackEntry) -> Unit,
) = scene(
    route = route.route,
    deepLinks = deepLinks.mapToString(),
    navTransition = navTransition,
    content = content
)

fun RouteBuilder.authorizedScene(
    route: IRoute,
    deepLinks: List<Any> = emptyList(),
    navTransition: NavTransition? = null,
    content: @Composable (BackStackEntry) -> Unit,
) = authorizedScene(
    route = route.route,
    deepLinks = deepLinks.mapToString(),
    navTransition = navTransition,
    content = content
)

fun RouteBuilder.authorizedDialog(
    route: IRoute,
    content: @Composable (BackStackEntry) -> Unit,
) = authorizedDialog(route.route, content)
