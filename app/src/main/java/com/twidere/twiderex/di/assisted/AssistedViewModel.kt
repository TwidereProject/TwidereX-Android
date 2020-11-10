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
package com.twidere.twiderex.di.assisted

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.staticAmbientOf
import androidx.compose.ui.viewinterop.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Composable
inline fun <reified AF : IAssistedFactory, reified VM : ViewModel> assistedViewModel(
    key: String? = null,
    noinline creator: ((AF) -> VM)? = null,
): VM {
    val factories = AmbientAssistedFactories.current
    val factory = factories.firstOrNull { AF::class.java.isInstance(it) } as? AF
    return viewModel(
        key,
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return factory?.let { creator?.invoke(it) } as T
            }
        }
    )
}

interface IAssistedFactory

@Composable
fun ProvideAssistedFactory(
    vararg factory: IAssistedFactory,
    content: @Composable () -> Unit,
) {
    Providers(
        AmbientAssistedFactories provides factory.toList()
    ) {
        content.invoke()
    }
}

val AmbientAssistedFactories = staticAmbientOf<List<IAssistedFactory>>()
