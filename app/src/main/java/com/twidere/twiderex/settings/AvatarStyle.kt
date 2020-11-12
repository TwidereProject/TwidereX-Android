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
package com.twidere.twiderex.settings

import android.content.SharedPreferences
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ambientOf
import com.twidere.twiderex.settings.types.RadioSettingItem

enum class AvatarStyle {
    Round,
    Square,
}

class AvatarStyleSettings(
    private val preferences: SharedPreferences
) : RadioSettingItem<AvatarStyle>(preferences) {
    override val options: List<AvatarStyle>
        get() = AvatarStyle.values().toList()
    override val itemContent: @Composable (item: AvatarStyle) -> Unit = {
        Text(text = it.name)
    }
    override val title: @Composable () -> Unit = {
        Text(text = "Avatar Style")
    }
    override fun load(): AvatarStyle {
        return preferences.getString(key, AvatarStyle.Round.name)
            ?.let { enumValueOf<AvatarStyle>(it) }
            ?: AvatarStyle.Round
    }
}

val AmbientAvatarStyle = ambientOf<AvatarStyle>()
