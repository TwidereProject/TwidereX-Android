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
package com.twidere.twiderex.component.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.ListItem
import androidx.compose.material.RadioButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import com.twidere.twiderex.component.lazy.itemHeader
import com.twidere.twiderex.settings.types.RadioSettingItem

fun <T : Enum<T>> LazyListScope.radioItem(
    item: RadioSettingItem<T>,
) {
    itemHeader {
        item.title.invoke()
    }

    items(item.options) {
        val selectedItem by item.data.observeAsState(initial = item.initialValue)
        ListItem(
            modifier = Modifier.clickable(onClick = { item.apply(it) }),
            text = {
                item.itemContent.invoke(it)
            },
            trailing = {
                RadioButton(selected = it == selectedItem, onClick = { item.apply(it) })
            }
        )
    }
}
