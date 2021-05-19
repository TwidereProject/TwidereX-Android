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

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.twidere.twiderex.ui.LocalIsActiveEdgeToEdge

@Composable
fun EdgeToEdgeBox(
    modifier: Modifier = Modifier,
    edgePadding: EdgePadding = EdgePadding(),
    content: @Composable () -> Unit,
) {
    val layoutDirection = LocalLayoutDirection.current
    val edgeModifier = if (LocalIsActiveEdgeToEdge.current) {
        var temp = modifier.navigationBarsPadding(
            bottom = edgePadding.bottom,
            left = when (layoutDirection) {
                LayoutDirection.Ltr -> edgePadding.start
                LayoutDirection.Rtl -> edgePadding.end
            },
            right = when (layoutDirection) {
                LayoutDirection.Ltr -> edgePadding.end
                LayoutDirection.Rtl -> edgePadding.start
            }
        )
        if (edgePadding.top) {
            temp = temp.statusBarsPadding()
        }
        temp
    } else {
        modifier
    }
    Box(
        modifier = edgeModifier
    ) {
        content.invoke()
    }
}

data class EdgePadding(
    val top: Boolean = true,
    val bottom: Boolean = true,
    val start: Boolean = true,
    val end: Boolean = true,
)
