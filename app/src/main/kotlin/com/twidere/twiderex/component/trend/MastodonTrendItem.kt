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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.twidere.twiderex.model.ui.UiTrend

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
            Row {
                Text(text = "${trend.dailyUses}")
                // TODO add graph
            }
        }
    ) {
        Text(text = trend.displayName, style = MaterialTheme.typography.subtitle1)
    }
}
