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
package com.twidere.twiderex.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.navigate
import com.twidere.twiderex.navigation.Route
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.LocalActivity
import com.twidere.twiderex.ui.LocalNavController
import java.util.UUID

private val authorizationKey = UUID.randomUUID().toString()

@Composable
fun requireAuthorization(
    content: @Composable () -> Unit,
) {
    val account = LocalActiveAccount.current
    if (account == null) {
        val navController = LocalNavController.current
        val activity = LocalActivity.current
        val (isSignInShown, setIsSignInShown) = rememberSaveable(
            key = authorizationKey,
            // FIXME: 2021/2/18 Workaround for https://issuetracker.google.com/issues/180513115
            saver = Saver(
                save = {
                    it.value
                },
                restore = {
                    mutableStateOf(it)
                },
            )
        ) { mutableStateOf(false) }
        if (!isSignInShown) {
            setIsSignInShown(true)
            navController.navigate(Route.SignIn.Default)
        } else {
            activity.finish()
        }
    } else {
        content.invoke()
    }
}
