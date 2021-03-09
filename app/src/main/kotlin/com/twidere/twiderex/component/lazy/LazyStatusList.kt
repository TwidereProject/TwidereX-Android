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
package com.twidere.twiderex.component.lazy

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import com.twidere.twiderex.model.ui.UiStatus

fun LazyListScope.statuses(
    lazyPagingItems: LazyPagingItems<UiStatus>,
    key: ((index: Int) -> Any) = { lazyPagingItems[it]?.hashCode() ?: it },
    itemContent: @Composable LazyItemScope.(value: UiStatus?) -> Unit
) {
    // this state recomposes every time the LazyPagingItems receives an update and changes the
    // value of recomposerPlaceholder
    @Suppress("UNUSED_VARIABLE")
    val recomposerPlaceholder = lazyPagingItems.recomposerPlaceholder.value

    items(lazyPagingItems.itemCount, key = key) { index ->
        val item = lazyPagingItems[index]
        itemContent(item)
    }
}

fun LazyListScope.statusesIndexed(
    lazyPagingItems: LazyPagingItems<UiStatus>,
    key: ((index: Int) -> Any) = { lazyPagingItems[it]?.hashCode() ?: it },
    itemContent: @Composable LazyItemScope.(index: Int, value: UiStatus?) -> Unit
) {
    // this state recomposes every time the LazyPagingItems receives an update and changes the
    // value of recomposerPlaceholder
    @Suppress("UNUSED_VARIABLE")
    val recomposerPlaceholder = lazyPagingItems.recomposerPlaceholder.value

    items(lazyPagingItems.itemCount, key = key) { index ->
        val item = lazyPagingItems[index]
        itemContent(index, item)
    }
}
