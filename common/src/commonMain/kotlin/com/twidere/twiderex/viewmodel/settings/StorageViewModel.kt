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
package com.twidere.twiderex.viewmodel.settings

import com.twidere.twiderex.repository.CacheRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class StorageViewModel(
    private val repository: CacheRepository,
) : ViewModel() {
    val loading = MutableStateFlow(false)

    fun clearImageCache() = viewModelScope.launch {
        loading.value = true
        repository.clearImageCache()
        loading.value = false
    }

    fun clearSearchHistory() = viewModelScope.launch {
        loading.value = true
        repository.clearSearchHistory()
        loading.value = false
    }

    fun clearAllCaches() = viewModelScope.launch {
        loading.value = true
        repository.clearDatabaseCache()
        repository.clearCacheDir()
        repository.clearImageCache()
        loading.value = false
    }
}
