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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.gesture.nestedscroll.NestedScrollConnection
import androidx.compose.ui.gesture.nestedscroll.NestedScrollSource
import androidx.compose.ui.gesture.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import dev.chrisbanes.accompanist.glide.LocalRequestManager

val LocalIsScrolling = compositionLocalOf { false }

@Composable
fun LazyColumn2(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical = if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: LazyListScope.() -> Unit
) {
    var isScrolling by remember { mutableStateOf(false) }
    val requestManager = LocalRequestManager.current
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                requestManager?.let {
                    if (!requestManager.isPaused) {
                        requestManager.pauseRequests()
                    }
                }
                isScrolling = true
                return super.onPreScroll(available, source)
            }

            override fun onPostFling(
                consumed: Velocity,
                available: Velocity,
                onFinished: (Velocity) -> Unit
            ) {
                requestManager?.let {
                    if (requestManager.isPaused) {
                        requestManager.resumeRequests()
                    }
                }
                isScrolling = false
                super.onPostFling(consumed, available, onFinished)
            }
        }
    }
    Providers(
        LocalIsScrolling provides isScrolling
    ) {
        Box(
            modifier = Modifier.nestedScroll(nestedScrollConnection)
        ) {
            LazyColumn(
                modifier = modifier,
                state = state,
                contentPadding = contentPadding,
                reverseLayout = reverseLayout,
                verticalArrangement = verticalArrangement,
                horizontalAlignment = horizontalAlignment,
                content = content,
            )
        }
    }
}
