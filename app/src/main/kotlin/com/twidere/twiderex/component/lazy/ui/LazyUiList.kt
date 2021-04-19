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
package com.twidere.twiderex.component.lazy.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.paging.LoadState
import com.twidere.twiderex.component.foundation.ErrorPlaceholder
import com.twidere.twiderex.component.foundation.LocalInAppNotification
import com.twidere.twiderex.component.lazy.LazyPagingItems
import com.twidere.twiderex.utils.generateNotificationEvent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull

@Composable
fun <T : Any> LazyUiList(
    items: LazyPagingItems<T>,
    content: @Composable () -> Unit,
) {
    val refresh = items.loadState.refresh
    val event = remember(refresh) {
        if (refresh is LoadState.Error) {
            refresh.error.generateNotificationEvent()
        } else {
            null
        }
    }
    if (items.itemCount == 0 && refresh is LoadState.Error) {
        ErrorPlaceholder(event)
    } else if (items.itemCount > 0) {
        val inAppNotification = LocalInAppNotification.current
        LaunchedEffect(event) {
            snapshotFlow { event }
                .distinctUntilChanged()
                .filterNotNull()
                .collect {
                    inAppNotification.show(it)
                }
        }
        content.invoke()
    }
}
