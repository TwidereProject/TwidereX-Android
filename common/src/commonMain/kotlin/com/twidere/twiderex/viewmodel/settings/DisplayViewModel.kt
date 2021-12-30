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

import androidx.datastore.core.DataStore
import com.twidere.twiderex.preferences.model.DisplayPreferences
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class DisplayViewModel(
    private val displayPreferences: DataStore<DisplayPreferences>
) : ViewModel() {
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

    fun setMuteByDefault(value: Boolean) = viewModelScope.launch {
        displayPreferences.updateData {
            it.copy(muteByDefault = value)
        }
    }
}
