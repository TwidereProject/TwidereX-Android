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
package com.twidere.twiderex.component.status

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun StatusLineComponent(
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
    lineWidth: Dp = StatusLineDefaults.lineWidth,
    startPadding: Dp = StatusLineDefaults.startPadding(lineWidth = lineWidth),
    topPoint: Dp = StatusLineDefaults.TopPoint,
    lineDown: Boolean = false,
    lineUp: Boolean = false,
    child: @Composable () -> Unit
) {
    Box(
        modifier = modifier,
    ) {
        if (lineDown) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(
                        start = startPadding,
                        top = topPoint,
                    )
            ) {
                Box(
                    modifier = Modifier
                        .width(lineWidth)
                        .fillMaxHeight()
                        .align(Alignment.BottomStart)
                        .background(lineColor)

                )
            }
        }

        if (lineUp) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(y = (-1).dp)
                    .padding(
                        start = startPadding,
                    )
            ) {
                Box(
                    modifier = Modifier
                        .width(lineWidth)
                        .height(topPoint + 1.dp)
                        .align(Alignment.TopStart)
                        .background(lineColor)

                )
            }
        }
        child.invoke()
    }
}

object StatusLineDefaults {
    val lineWidth = 2.dp
    val TopPoint = 16.dp + UserAvatarDefaults.AvatarSize / 2
    @Composable
    fun startPadding(lineWidth: Dp) = 16.dp + UserAvatarDefaults.AvatarSize / 2 - lineWidth / 2
}
