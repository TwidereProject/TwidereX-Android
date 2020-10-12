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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.annotations.IncomingComposeUpdate
import com.twidere.twiderex.ui.standardPadding

@IncomingComposeUpdate
fun <T> LazyListScope.itemsGridIndexed(
    data: List<T>,
    rowSize: Int,
    spacing: Dp = 0.dp,
    padding: Dp = 0.dp,
    itemContent: @Composable BoxScope.(Int, T) -> Unit,
) {
    item {
        Spacer(modifier = Modifier.height(standardPadding * 2))
    }
    val rows = data.windowed(rowSize, rowSize, true)
    itemsIndexed(rows) { index, row ->
        Column(
            modifier = Modifier.fillParentMaxWidth().padding(horizontal = padding)
        ) {
            Row {
                for (i in row.indices) {
                    val item = row[i]
                    Box(modifier = Modifier.weight(1f)) {
                        itemContent(data.indexOf(item), item)
                    }
                    if (i != row.size - 1) {
                        Spacer(modifier = Modifier.width(spacing))
                    }
                }
            }
            if (index != rows.size - 1) {
                Spacer(modifier = Modifier.height(spacing))
            }
        }
    }
    item {
        Spacer(modifier = Modifier.height(standardPadding * 2))
    }
}
