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
import androidx.compose.runtime.CompositionLocalProvider
import com.twidere.twiderex.component.navigation.LocalNavigator
import com.twidere.twiderex.kmp.LocalRemoteNavigator
import com.twidere.twiderex.ui.LocalNavController
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.rememberNavigator

@Composable
fun Router(
    navController: Navigator = rememberNavigator(),
    isDebug: Boolean = false
) {
    val remoteNavigator = LocalRemoteNavigator.current
    CompositionLocalProvider(
        LocalNavController provides navController,
        LocalNavigator provides com.twidere.twiderex.component.navigation.Navigator(navController, remoteNavigator),
    ) {
        NavHost(navigator = navController, initialRoute = initialRoute) {
            route()
        }
        if (isDebug) {
            ComposeDebugTool(navController)
        }
    }
}
