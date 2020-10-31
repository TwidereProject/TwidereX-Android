/*
 *  TwidereX
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of TwidereX.
 * 
 *  TwidereX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  TwidereX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with TwidereX. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.viewModel
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavOptions
import com.twidere.twiderex.ui.AmbientViewModelProviderFactory

@Composable
inline fun <reified VM : ViewModel> navViewModel(
    key: String? = null,
    factory: ViewModelProvider.Factory? = AmbientViewModelProviderFactory.current,
): VM = viewModel(key, factory)

// Hack for NavOptions
fun NavController.navigate(route: String, options: NavOptions? = null) {
    navigate(NavDeepLinkRequest.Builder.fromUri(createRoute(route).toUri()).build(), options)
}

internal fun createRoute(route: String) = "android-app://androidx.navigation.compose/$route"
