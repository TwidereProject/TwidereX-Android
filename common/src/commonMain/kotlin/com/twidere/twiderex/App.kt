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
package com.twidere.twiderex

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import com.twidere.twiderex.action.LocalStatusActions
import com.twidere.twiderex.action.StatusActions
import com.twidere.twiderex.compose.LocalResLoader
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.extensions.observeAsState
import com.twidere.twiderex.kmp.LocalRemoteNavigator
import com.twidere.twiderex.navigation.Router
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.LocalActiveAccountViewModel
import com.twidere.twiderex.utils.LocalPlatformResolver
import moe.tlaster.precompose.navigation.NavController
import moe.tlaster.precompose.viewmodel.viewModelScope

@Composable
fun App(navController: NavController = NavController()) {
    val accountViewModel =
        com.twidere.twiderex.di.ext.getViewModel<com.twidere.twiderex.viewmodel.ActiveAccountViewModel>()
    val account by accountViewModel.account.observeAsState(null, accountViewModel.viewModelScope.coroutineContext)
    CompositionLocalProvider(
        LocalResLoader provides get(),
        LocalRemoteNavigator provides get(),
        LocalActiveAccount provides account,
        LocalActiveAccountViewModel provides accountViewModel,
        LocalStatusActions provides get<StatusActions>(),
        LocalPlatformResolver provides get(),
        LocalRemoteNavigator provides get(),
    ) {
        Router(
            navController = navController
        )
    }
}
