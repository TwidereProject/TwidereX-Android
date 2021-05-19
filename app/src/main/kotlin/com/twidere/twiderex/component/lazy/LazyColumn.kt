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

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.twidere.twiderex.component.foundation.EdgePadding
import com.twidere.twiderex.ui.LocalIsActiveEdgeToEdge
import kotlinx.coroutines.FlowPreview

@OptIn(FlowPreview::class)
@Composable
fun LazyColumn2(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical =
        if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    edgePadding: EdgePadding = EdgePadding(top = false),
    content: LazyListScope.() -> Unit
) {
    with(LocalDensity.current) {
        // support top and bottom edge padding, must use content padding for scroll
        val edgeContentPadding = if (LocalIsActiveEdgeToEdge.current) PaddingValues(
            top = if (edgePadding.top)
                LocalWindowInsets.current.statusBars.top.toDp() + contentPadding.calculateTopPadding()
            else contentPadding.calculateTopPadding(),
            bottom = if (edgePadding.bottom)
                LocalWindowInsets.current.systemBars.bottom.toDp() + contentPadding.calculateBottomPadding()
            else contentPadding.calculateBottomPadding()
        ) else contentPadding

        LazyColumn(
            modifier = modifier.navigationBarsPadding(bottom = false),
            state = state,
            contentPadding = edgeContentPadding,
            reverseLayout = reverseLayout,
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
            flingBehavior = flingBehavior,
            content = content,
        )
    }
}
