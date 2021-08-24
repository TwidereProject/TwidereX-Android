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
import com.twidere.twiderex.preferences.model.DisplayPreferences
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class DisplayViewModel @AssistedInject constructor(
    private val displayPreferences: DataStore<DisplayPreferences>
) : ViewModel() {

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(): DisplayViewModel
    }

    fun setUseSystemFontSize(value: Boolean) = viewModelScope.launch {
        displayPreferences.updateData {
            it.copy(useSystemFontSize = value)
        }
    }

    fun setAvatarStyle(value: DisplayPreferences.AvatarStyle) = viewModelScope.launch {
        displayPreferences.updateData {
            it.copy(avatarStyle = value)
        }
    }

    fun setMediaPreview(value: Boolean) = viewModelScope.launch {
        displayPreferences.updateData {
            it.copy(mediaPreview = value)
        }
    }

    fun setUrlPreview(value: Boolean) = viewModelScope.launch {
        displayPreferences.updateData {
            it.copy(urlPreview = value)
        }
    }

    fun setAutoPlayback(value: DisplayPreferences.AutoPlayback) = viewModelScope.launch {
        displayPreferences.updateData {
            it.copy(autoPlayback = value)
        }
    }

    fun commitFontScale(value: Float) = viewModelScope.launch {
        displayPreferences.updateData {
            it.copy(fontScale = value)
        }
    }
}
