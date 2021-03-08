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
package com.twidere.twiderex.di.assisted

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
inline fun <reified AF, reified VM : ViewModel> assistedViewModel(
    vararg dependsOn: Any,
    noinline creator: ((AF) -> VM)? = null,
): VM {
    val factories = LocalAssistedFactories.current
    val factory = factories.firstOrNull { AF::class.java.isInstance(it) } as? AF
    return viewModel(
        if (dependsOn.any()) {
            dependsOn.joinToString { it.hashCode().toString() } + VM::class.java.canonicalName
        } else {
            null
        },
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return factory?.let { creator?.invoke(it) } as T
            }
        }
    )
}

@Composable
fun ProvideAssistedFactory(
    factory: List<Any>,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalAssistedFactories provides factory
    ) {
        content.invoke()
    }
}

val LocalAssistedFactories = staticCompositionLocalOf<List<Any>> { emptyList() }
