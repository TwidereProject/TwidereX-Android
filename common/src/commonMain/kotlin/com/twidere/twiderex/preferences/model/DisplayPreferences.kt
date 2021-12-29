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
package com.twidere.twiderex.preferences.model

import com.twidere.twiderex.defaultLoadCount
import kotlinx.serialization.Serializable

@Serializable
data class DisplayPreferences(
    val useSystemFontSize: Boolean = true,
    val fontScale: Float = 1f,
    val avatarStyle: AvatarStyle = AvatarStyle.Round,
    val mediaPreview: Boolean = true,
    val autoPlayback: AutoPlayback = AutoPlayback.Auto,
    val urlPreview: Boolean = false,
    val muteByDefault: Boolean = false,
    val shareWithContent: Boolean = false,
    val loadItemLimit: Int = defaultLoadCount,
) {
    @Serializable
    enum class AvatarStyle {
        Round,
        Square,
    }
    @Serializable
    enum class AutoPlayback {
        Auto,
        Always,
        Off,
    }
}
