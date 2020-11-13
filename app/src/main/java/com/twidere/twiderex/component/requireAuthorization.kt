/*
 *  Twidere X
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
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
package com.twidere.twiderex.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.navigation.compose.navigate
import com.twidere.twiderex.navigation.Route
import com.twidere.twiderex.ui.AmbientActiveAccount
import com.twidere.twiderex.ui.AmbientActivity
import com.twidere.twiderex.ui.AmbientNavController
import java.util.UUID

private val key = UUID.randomUUID().toString()

@Composable
fun requireAuthorization(
    content: @Composable () -> Unit,
) {
    val account = AmbientActiveAccount.current
    if (account == null) {
        val navController = AmbientNavController.current
        val activity = AmbientActivity.current
        val (isSignInShown, setIsSignInShown) = savedInstanceState(key = key) { false }
        if (!isSignInShown) {
            setIsSignInShown(true)
            navController.navigate(Route.SignIn.Twitter)
        } else {
            activity.finish()
        }
    } else {
        content.invoke()
    }
}
