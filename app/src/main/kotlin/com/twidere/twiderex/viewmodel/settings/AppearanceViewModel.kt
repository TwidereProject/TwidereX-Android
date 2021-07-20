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
import com.twidere.twiderex.preferences.proto.AppearancePreferences
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch

class AppearanceViewModel @AssistedInject constructor(
    private val appearancePreferences: DataStore<AppearancePreferences>
) : ViewModel() {

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(): AppearanceViewModel
    }

    fun setPrimaryColorIndex(index: Int) = viewModelScope.launch {
        appearancePreferences.updateData {
            it.toBuilder().setPrimaryColorIndex(index).build()
        }
    }

    fun setTabPosition(position: AppearancePreferences.TabPosition) = viewModelScope.launch {
        appearancePreferences.updateData {
            it.toBuilder().setTapPosition(position).build()
        }
    }

    fun setTheme(theme: AppearancePreferences.Theme) = viewModelScope.launch {
        appearancePreferences.updateData {
            it.toBuilder().setTheme(theme).build()
        }
    }

    fun setHideTabBarWhenScrolling(hide: Boolean) = viewModelScope.launch {
        appearancePreferences.updateData {
            it.toBuilder().setHideTabBarWhenScroll(hide).build()
        }
    }

    fun setHideFabWhenScrolling(hide: Boolean) = viewModelScope.launch {
        appearancePreferences.updateData {
            it.toBuilder().setHideFabWhenScroll(hide).build()
        }
    }

    fun setHideAppBarWhenScrolling(hide: Boolean) = viewModelScope.launch {
        appearancePreferences.updateData {
            it.toBuilder().setHideAppBarWhenScroll(hide).build()
        }
    }

    fun setIsDarkModePureBlack(value: Boolean) = viewModelScope.launch {
        appearancePreferences.updateData {
            it.toBuilder().setIsDarkModePureBlack(value).build()
        }
    }
}
