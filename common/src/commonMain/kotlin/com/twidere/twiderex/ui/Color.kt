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
package com.twidere.twiderex.ui

import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.twidere.twiderex.preferences.LocalAppearancePreferences

val mediumEmphasisContentContentColor: Color
    @Composable
    get() = LocalContentColor.current.copy(alpha = ContentAlpha.medium)

val Orange: Color
    get() = Color(0XFFFF9500)

val primaryColors = listOf(
    Color(0XFF4C9EEB) to Color(0XFF5CB0FF),
    Color(0XFF1C68F3) to Color(0XFF4B85F0),
    Color(0XFF8D47EE) to Color(0XFF9254DE),
    Color(0XFFBC5077) to Color(0XFFF4769B),
    Color(0XFFFA541C) to Color(0XFFFF7A45),
    Color(0XFFFAAD14) to Color(0XFFFFC53D),
    Color(0XFF9ACB1E) to Color(0XFFBBE739),
    Color(0XFF38D29B) to Color(0XFF44ECAE),
)

@Composable
fun currentPrimaryColor(): Color {
    val appearance = LocalAppearancePreferences.current
    val colorIndex = appearance.primaryColorIndex
    return if (isDarkTheme()) {
        primaryColors[colorIndex].second
    } else {
        primaryColors[colorIndex].first
    }
}
