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
package com.twidere.twiderex.component.foundation

import android.os.Build
import androidx.annotation.DrawableRes
import androidx.compose.material.AmbientContentAlpha
import androidx.compose.material.AmbientContentColor
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.vectorResource
import com.twidere.twiderex.BuildConfig

@Composable
fun IconCompat(
    @DrawableRes id: Int,
    modifier: Modifier = Modifier,
    tint: Color = AmbientContentColor.current.copy(alpha = AmbientContentAlpha.current)
) {
    if (BuildConfig.VERSION_CODE >= Build.VERSION_CODES.N) {
        Icon(
            imageVector = vectorResource(id = id),
            modifier = modifier,
            tint = tint
        )
    } else {
        Icon(
            bitmap = imageResource(id = id),
            modifier = modifier,
            tint = tint
        )
    }
}
