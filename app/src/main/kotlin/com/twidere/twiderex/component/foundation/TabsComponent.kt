/*
 *  Twidere X
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
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
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabDefaults
import androidx.compose.material.TabDefaults.tabIndicatorOffset
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.extensions.withElevation
import com.twidere.twiderex.ui.mediumEmphasisContentContentColor

@Composable
fun IconTabsComponent(
    items: List<ImageVector>,
    selectedItem: Int,
    onItemSelected: (Int) -> Unit,
) {
    TabsComponent(
        count = items.count(),
        selectedItem = selectedItem,
        onItemSelected = onItemSelected
    ) {
        Box(
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(imageVector = items[it])
        }
    }
}

@Composable
fun TextTabsComponent(
    items: List<String>,
    selectedItem: Int,
    onItemSelected: (Int) -> Unit,
) {
    TabsComponent(
        count = items.count(),
        selectedItem = selectedItem,
        onItemSelected = onItemSelected
    ) {
        Box(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = items[it])
        }
    }
}

@Composable
fun TabsComponent(
    count: Int,
    selectedItem: Int,
    onItemSelected: (Int) -> Unit,
    tabContent: @Composable (Int) -> Unit,
) {
    TabRow(
        selectedTabIndex = selectedItem,
        backgroundColor = MaterialTheme.colors.surface.withElevation(),
        indicator = { tabPositions ->
            TabDefaults.Indicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedItem]),
                color = MaterialTheme.colors.primary,
            )
        }
    ) {
        for (i in 0 until count) {

            Tab(
                selected = selectedItem == i,
                onClick = {
                    onItemSelected(i)
                },
                selectedContentColor = MaterialTheme.colors.primary,
                unselectedContentColor = mediumEmphasisContentContentColor,
            ) {
                tabContent.invoke(i)
            }
        }
    }
}
