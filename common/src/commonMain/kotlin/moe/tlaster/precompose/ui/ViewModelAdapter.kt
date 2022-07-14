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
package moe.tlaster.precompose.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.ViewModelStoreOwner
import kotlin.reflect.KClass

@Composable
inline fun <reified T : ViewModel> viewModel(
    keys: List<Any?> = emptyList(),
    noinline creator: () -> T,
): T = viewModel(T::class, keys, creator = creator)

@Composable
fun <T : ViewModel> viewModel(
    modelClass: KClass<T>,
    keys: List<Any?> = emptyList(),
    creator: () -> T,
): T {
    val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "Require LocalViewModelStoreOwner not null for $modelClass"
    }
    return remember(
        modelClass, keys, creator, viewModelStoreOwner
    ) {
        viewModelStoreOwner.getViewModel(keys, modelClass = modelClass, creator = creator)
    }
}

private fun <T : ViewModel> ViewModelStoreOwner.getViewModel(
    keys: List<Any?> = emptyList(),
    modelClass: KClass<T>,
    creator: () -> T,
): T {
    val key = (keys.map { it.hashCode().toString() } + modelClass.qualifiedName).joinToString()
    val existing = viewModelStore[key]
    if (existing != null && modelClass.isInstance(existing)) {
        @Suppress("UNCHECKED_CAST")
        return existing as T
    } else {
        @Suppress("ControlFlowWithEmptyBody")
        if (existing != null) {
            // TODO: log a warning.
        }
    }
    val viewModel = creator.invoke()
    viewModelStore.put(key, viewModel)
    return viewModel
}
