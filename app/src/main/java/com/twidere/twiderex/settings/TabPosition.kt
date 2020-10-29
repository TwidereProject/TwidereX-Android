/*
 *  TwidereX
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of TwidereX.
 * 
 *  TwidereX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  TwidereX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with TwidereX. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.settings

import android.content.SharedPreferences
import androidx.compose.foundation.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ambientOf
import com.twidere.twiderex.settings.types.RadioSettingItem

enum class TabPosition {
    Top,
    Bottom,
}

class TabPositionSetting(
    private val preferences: SharedPreferences,
) : RadioSettingItem<TabPosition>(preferences) {

    override val options: List<TabPosition> = TabPosition.values().toList()
    override val itemContent: @Composable (item: TabPosition) -> Unit = {
        Text(text = it.name)
    }
    override val title: @Composable () -> Unit = {
        Text(text = "Tab Position")
    }
    override fun load(): TabPosition {
        return preferences.getString(key, TabPosition.Top.name)
            ?.let { enumValueOf<TabPosition>(it) }
            ?: TabPosition.Top
    }
}

val AmbientTabPosition = ambientOf<TabPositionSetting>()
