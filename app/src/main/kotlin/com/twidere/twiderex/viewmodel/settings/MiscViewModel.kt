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
package com.twidere.twiderex.viewmodel.settings

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twidere.twiderex.preferences.proto.MiscPreferences
import dagger.assisted.AssistedInject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class MiscViewModel @AssistedInject constructor(
    private val miscPreferences: DataStore<MiscPreferences>
) : ViewModel() {
    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(): MiscViewModel
    }

    val nitter by lazy {
        MutableStateFlow("")
    }

    init {
        viewModelScope.launch {
            nitter.value = miscPreferences.data.first().nitterInstance
        }
    }

    fun setNitterInstance(value: String) {
        nitter.value = value
        viewModelScope.launch {
            miscPreferences.updateData {
                it.toBuilder()
                    .setNitterInstance(value)
                    .build()
            }
        }
    }
}
