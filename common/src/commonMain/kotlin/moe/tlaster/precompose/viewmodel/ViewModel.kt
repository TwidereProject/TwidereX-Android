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

import java.io.Closeable

abstract class ViewModel {
    @Volatile
    private var disposed = false
    private val bagOfTags = hashMapOf<String, Any>()

    protected open fun onCleared() {}

    fun clear() {
        disposed = true
        bagOfTags.let {
            for (value in it.values) {
                disposeWithRuntimeException(value)
            }
        }
        onCleared()
    }

    open fun <T> setTagIfAbsent(key: String, newValue: T): T {
        @Suppress("UNCHECKED_CAST")
        return bagOfTags.getOrPut(key) {
            newValue as Any
        }.also {
            if (disposed) {
                disposeWithRuntimeException(it)
            }
        } as T
    }

    open fun <T> getTag(key: String): T? {
        @Suppress("UNCHECKED_CAST")
        return bagOfTags[key] as T?
    }

    private fun disposeWithRuntimeException(obj: Any) {
        if (obj is Closeable) {
            obj.close()
        }
    }
}
