/*
 *  TwidereX
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of TwidereX.
 * 
 *  TwidereX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  TwidereX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with TwidereX. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.component

import androidx.compose.foundation.AmbientContentColor
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AmbientEmphasisLevels
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabConstants
import androidx.compose.material.TabConstants.defaultTabIndicatorOffset
import androidx.compose.material.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.extensions.withElevation

@Composable
fun TabsComponent(
    items: List<VectorAsset>,
    selectedItem: Int,
    onItemSelected: (Int) -> Unit,
) {
    TabRow(
        selectedTabIndex = selectedItem,
        backgroundColor = MaterialTheme.colors.surface.withElevation(),
        indicator = { tabPositions ->
            TabConstants.DefaultIndicator(
                modifier = Modifier.defaultTabIndicatorOffset(tabPositions[selectedItem]),
                color = MaterialTheme.colors.primary,
            )
        }
    ) {
        for (i in 0 until items.count()) {
            Tab(
                selected = selectedItem == i,
                onClick = {
                    onItemSelected(i)
                },
                selectedContentColor = MaterialTheme.colors.primary,
                unselectedContentColor = AmbientEmphasisLevels.current.medium.applyEmphasis(
                    AmbientContentColor.current
                ),
            ) {
                Box(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(asset = items[i])
                }
            }
        }
    }
}
