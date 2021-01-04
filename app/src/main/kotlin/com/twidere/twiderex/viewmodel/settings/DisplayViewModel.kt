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
package com.twidere.twiderex.viewmodel.settings

import androidx.datastore.core.DataStore
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twidere.twiderex.preferences.proto.DisplayPreferences
import kotlinx.coroutines.launch

class DisplayViewModel @ViewModelInject constructor(
    private val displayPreferences: DataStore<DisplayPreferences>
) : ViewModel() {
    fun setUseSystemFontSize(value: Boolean) = viewModelScope.launch {
        displayPreferences.updateData {
            it.toBuilder().setUseSystemFontSize(value).build()
        }
    }

    fun setFontScale(value: Float) = viewModelScope.launch {
        displayPreferences.updateData {
            it.toBuilder().setFontScale(value).build()
        }
    }

    fun setAvatarStyle(value: DisplayPreferences.AvatarStyle) = viewModelScope.launch {
        displayPreferences.updateData {
            it.toBuilder().setAvatarStyle(value).build()
        }
    }

    fun setMediaPreview(value: Boolean) = viewModelScope.launch {
        displayPreferences.updateData {
            it.toBuilder().setMediaPreview(value).build()
        }
    }

    fun setAutoPlayback(value: DisplayPreferences.AutoPlayback) = viewModelScope.launch {
        displayPreferences.updateData {
            it.toBuilder().setAutoPlayback(value).build()
        }
    }
}
