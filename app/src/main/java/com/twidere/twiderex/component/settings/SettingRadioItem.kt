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
package com.twidere.twiderex.component.settings

import androidx.compose.foundation.ProvideTextStyle
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

fun <T> LazyListScope.radioItem(
    title: @Composable () -> Unit,
    items: List<T>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    itemContent: @Composable (item: T) -> Unit,
) {
    item {
        ProvideTextStyle(value = MaterialTheme.typography.button) {
            title.invoke()
        }
    }

    itemsIndexed(items) { index, item ->
        ListItem(
            modifier = Modifier.clickable(onClick = { onSelected(index) }),
            text = {
                itemContent.invoke(item)
            },
            trailing = {
                RadioButton(selected = index == selectedIndex, onClick = { onSelected(index) })
            }
        )
    }
}
