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
package com.twidere.twiderex.component.trend

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.model.ui.UiTrend
import com.twidere.twiderex.model.ui.UiTrendHistory

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MastodonTrendItem(trend: UiTrend, onClick: (UiTrend) -> Unit) {
    ListItem(
        modifier = Modifier.clickable(
            onClick = { onClick(trend) }
        ),
        secondaryText = {
            Text(text = "${trend.dailyAccounts} people talking", style = MaterialTheme.typography.body2)
        },
        trailing = {
            Row(verticalAlignment = Alignment.Top) {
                Text(text = "${trend.dailyUses}", style = MaterialTheme.typography.body1)
                Spacer(modifier = Modifier.width(16.dp))
                MastodonTrendChart(
                    trendHistories = trend.sortedHistory,
                    modifier = Modifier
                        .width(66.dp)
                        .height(27.dp)
                )
            }
        }
    ) {
        Text(text = trend.displayName, style = MaterialTheme.typography.subtitle1)
    }
}

@Composable
fun MastodonTrendChart(trendHistories: List<UiTrendHistory>, modifier: Modifier) {
    val color = MaterialTheme.colors.primary
    Canvas(modifier = modifier) {
        val maxUses = trendHistories.maxOf { it.uses }
        val yDelta = size.height / maxUses.toFloat()
        val xDelta = size.width / (trendHistories.size - 1)
        val path = Path()
        trendHistories.forEachIndexed { index, uiTrendHistory ->
            if (index == 0) {
                path.moveTo(size.width - xDelta * index, size.height - uiTrendHistory.uses * yDelta)
            } else {
                // reverse the y index
                path.lineTo(size.width - xDelta * index, size.height - uiTrendHistory.uses * yDelta)
            }
        }
        // draw the line
        drawPath(path, color = color, style = Stroke(width = 8f))
        // TODO add the gradient color  for this chart
        // draw the color bellow
        path.lineTo(0f, size.height)
        path.lineTo(size.width, size.height)
        // path.lineTo(size.width - xDelta * 0, size.height - trendHistories[0].uses * yDelta)
        path.close()
        drawPath(path, color = Color.LightGray)
    }
}
