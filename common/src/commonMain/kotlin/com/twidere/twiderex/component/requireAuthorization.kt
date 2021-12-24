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
package com.twidere.twiderex.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.twidere.twiderex.navigation.Root
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.LocalNavController
import moe.tlaster.precompose.ui.LocalBackDispatcherOwner

@Composable
fun RequireAuthorization(
    content: @Composable () -> Unit,
) {
    val account = LocalActiveAccount.current
    if (account == null) {
        val navController = LocalNavController.current
        val backDispatcher = LocalBackDispatcherOwner.current?.backDispatcher
        LaunchedEffect(Unit) {
            val result = navController.navigateForResult(Root.SignIn.General)
            if (result == null) {
                backDispatcher?.onBackPress()
            }
        }
    } else {
        content.invoke()
    }
}
