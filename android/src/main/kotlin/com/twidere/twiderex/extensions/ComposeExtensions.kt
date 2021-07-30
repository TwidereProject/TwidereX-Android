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
package com.twidere.twiderex.extensions

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.twidere.twiderex.preferences.LocalAppearancePreferences
import com.twidere.twiderex.preferences.proto.AppearancePreferences

@Composable
inline fun <reified VM : ViewModel> viewModel(
    vararg dependsOn: Any,
    noinline creator: (() -> VM)? = null,
): VM {
    return viewModel(
        key = if (dependsOn.any()) {
            dependsOn.joinToString { it.hashCode().toString() } + VM::class.java.canonicalName
        } else {
            null
        },
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return creator?.invoke() as T
            }
        }
    )
}

@Composable
fun isDarkTheme(): Boolean {
    return when (LocalAppearancePreferences.current.theme) {
        AppearancePreferences.Theme.Auto -> isSystemInDarkTheme()
        AppearancePreferences.Theme.Light -> false
        AppearancePreferences.Theme.Dark -> true
        else -> false
    }
}
