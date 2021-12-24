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
package com.twidere.twiderex.component.trend

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.model.ui.UiTrend
import com.twidere.twiderex.model.ui.UiTrendHistory

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MastodonTrendItem(trend: UiTrend, onClick: (UiTrend) -> Unit) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(
                onClick = { onClick(trend) },
            )
        ) {
            ListItem(
                modifier = Modifier.weight(1f),
                secondaryText = {
                    Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_trends_accounts, trend.dailyAccounts), style = MaterialTheme.typography.body2)
                },
                text = {
                    Text(text = trend.displayName, style = MaterialTheme.typography.subtitle1)
                },
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = MastodonTrendItemDefaults.ContentPadding)
            ) {
                Text(text = "${trend.dailyUses}", style = MaterialTheme.typography.body1)
                Spacer(modifier = Modifier.width(MastodonTrendItemDefaults.ContentSpacing))
                MastodonTrendChart(
                    trendHistories = trend.sortedHistory,
                    modifier = Modifier
                        .size(MastodonTrendItemDefaults.ChartWidth, MastodonTrendItemDefaults.ChartHeight),
                    lineChartPaddingTop = MastodonTrendItemDefaults.ContentPadding,
                    lineChartPaddingBottom = MastodonTrendItemDefaults.ContentPadding
                )
            }
        }
    }
}

private object MastodonTrendItemDefaults {
    val ContentSpacing = 16.dp
    val ContentPadding = 12.dp
    val ChartWidth = 66.dp
    val ChartHeight = 40.dp
}

@Composable
fun MastodonTrendChart(
    trendHistories: List<UiTrendHistory>,
    modifier: Modifier,
    lineChartPaddingTop: Dp = 0.dp,
    lineChartPaddingBottom: Dp = 0.dp,
) {
    val color = MaterialTheme.colors.primary.copy(0.75f)
    Canvas(modifier = modifier) {
        val maxUses = trendHistories.maxOfOrNull { it.uses } ?: 0
        val yDelta = (size.height - (lineChartPaddingTop + lineChartPaddingBottom).toPx()) / maxUses.toFloat()
        val xDelta = size.width / (trendHistories.size - 1)
        val linePath = Path()
        val gradientPath = Path()
        val lineWidth = MastodonTrendChartDefaults.ChartWidth.toPx()
        trendHistories.forEachIndexed { index, uiTrendHistory ->
            val x = size.width - xDelta * index
            val y = size.height - lineChartPaddingBottom.toPx() - uiTrendHistory.uses * yDelta
            when (index) {
                0 -> {
                    linePath.moveTo(x, y)
                    gradientPath.moveTo(x, y)
                }
                trendHistories.size - 1 -> {
                    // reverse the y index
                    linePath.lineTo(0f, y)
                    gradientPath.lineTo(0f, y)
                }
                else -> {
                    linePath.lineTo(x, y)
                    gradientPath.lineTo(x, y)
                }
            }
        }

        // draw the gradient color bellow
        gradientPath.lineTo(0f, size.height)
        gradientPath.lineTo(size.width, size.height)
        gradientPath.close()
        drawPath(
            gradientPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    color,
                    color.copy(0f)
                )
            ),
        )
        // draw the chart linen
        drawPath(
            linePath,
            color = color,
            style = Stroke(
                width = lineWidth,
                join = StrokeJoin.Round,
                cap = StrokeCap.Round,
                pathEffect = PathEffect.cornerPathEffect(MastodonTrendChartDefaults.CornerPathRadius.toPx())
            )
        )
    }
}

private object MastodonTrendChartDefaults {
    val ChartWidth = 1.dp
    val CornerPathRadius = 2.dp
}
