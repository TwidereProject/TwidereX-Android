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
package com.twidere.twiderex.component.foundation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.ceil
import kotlin.math.sqrt

@Composable
fun GridLayout(
    modifier: Modifier = Modifier,
    spacing: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    val pxSpacing = with(LocalDensity.current) {
        spacing.roundToPx()
    }
    val measurePolicy = gridLayoutMeasurePolicy(pxSpacing)
    Layout(
        content = { content() },
        measurePolicy = measurePolicy,
        modifier = modifier
    )
}

@Composable
private fun gridLayoutMeasurePolicy(
    spacing: Int,
) = remember(spacing) {
    MeasurePolicy { measurables, constraints ->
        val columns = ceil(sqrt(measurables.size.toDouble()))
        val rows = ceil((measurables.size.toDouble() / columns))
        val infinityWidth = ((Constraints.Infinity - spacing * (columns - 1)) / columns).toInt()
        val itemWidth =
            ((constraints.maxWidth.toDouble() - spacing * (columns - 1)) / columns).toInt().coerceAtLeast(0)
        val itemHeight = if (constraints.maxHeight != Constraints.Infinity) {
            ((constraints.maxHeight.toDouble() - spacing * (rows - 1)) / rows).toInt().coerceAtLeast(0)
        } else {
            itemWidth
        }
        val placeables = measurables.map { measurable ->
            measurable.measure(if (itemWidth >= infinityWidth) Constraints.fixed(0, 0) else Constraints.fixed(width = itemWidth, height = itemHeight))
        }
        layout(
            width = constraints.maxWidth,
            // workaround for the unknown desktop crash （Exception in thread "AWT-EventQueue-0" java.lang.IllegalArgumentException:
            // Can't represent a size of 1073741961 in Constraints at androidx.compose.ui.unit.Constraints$Companion.bitsNeedForSize(Constraints.kt:408)）
            height = minOf(GridLayoutDefault.MaxHeight, (itemHeight * rows + spacing * (rows - 1)).toInt())
        ) {
            var currentX = 0
            var currentY = 0
            placeables.forEach { placeable ->
                placeable.placeRelative(x = currentX, y = currentY)
                currentX += (itemWidth + spacing)
                if (currentX >= constraints.maxWidth) {
                    currentX = 0
                    currentY += (itemHeight + spacing)
                }
            }
        }
    }
}

object GridLayoutDefault {
    const val MaxHeight = 10000
}
