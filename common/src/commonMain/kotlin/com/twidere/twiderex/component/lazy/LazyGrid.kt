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
package com.twidere.twiderex.component.lazy

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun <T> LazyGridForIndexed(
    modifier: Modifier = Modifier,
    data: List<T>,
    rowSize: Int,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    spacing: Dp = 0.dp,
    padding: Dp = 0.dp,
    itemContent: @Composable BoxScope.(Int, T) -> Unit,
) {
    val rows = data.windowed(rowSize, rowSize, true)
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalAlignment = horizontalAlignment,
    ) {
        itemsIndexed(items = rows) { index, row ->
            Column(
                modifier = Modifier.fillParentMaxWidth().padding(horizontal = padding)
            ) {
                Row {
                    for (i in row.indices) {
                        val item = row[i]
                        Box(modifier = Modifier.weight(1f)) {
                            itemContent(data.indexOf(item), item)
                        }
                        if (i != row.lastIndex) {
                            Spacer(modifier = Modifier.width(spacing))
                        }
                    }
                }
                if (index != rows.lastIndex) {
                    Spacer(modifier = Modifier.height(spacing))
                }
            }
        }
    }
}
