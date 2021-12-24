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
package moe.tlaster.precompose.viewmodel

import kotlin.reflect.KClass

inline fun <reified T : ViewModel> ViewModelStore.getViewModel(
    noinline creator: () -> T,
): T {
    val key = T::class.qualifiedName.toString()
    return getViewModel(key, T::class, creator)
}

fun <T : ViewModel> ViewModelStore.getViewModel(
    key: String,
    clazz: KClass<T>,
    creator: () -> T,
): T {
    val existing = get(key)
    if (existing != null && clazz.isInstance(existing)) {
        @Suppress("UNCHECKED_CAST")
        return existing as T
    } else {
        @Suppress("ControlFlowWithEmptyBody")
        if (existing != null) {
            // TODO: log a warning.
        }
    }
    val viewModel = creator.invoke()
    put(key, viewModel)
    return viewModel
}
