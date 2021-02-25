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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.chrisbanes.accompanist.glide.LocalRequestManager
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce

val LocalIsScrollInProgress = compositionLocalOf { false }

private class LazyColumnScrollState {
    private val _isScrollInProgress = MutableStateFlow(false)
    @OptIn(FlowPreview::class)
    val isScrollInProgress: Flow<Boolean>
        get() = _isScrollInProgress.debounce(100)

    suspend fun setIsScrollInProgress(value: Boolean) {
        _isScrollInProgress.emit(value)
    }
}

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
    content: LazyListScope.() -> Unit
) {
    val requestManager = LocalRequestManager.current
    val scrollState = remember {
        LazyColumnScrollState()
    }
    LaunchedEffect(state.isScrollInProgress) {
        scrollState.setIsScrollInProgress(state.isScrollInProgress)
    }
    val isScrollInProgress by scrollState.isScrollInProgress.collectAsState(initial = false)
    DisposableEffect(isScrollInProgress) {
        requestManager?.let {
            if (isScrollInProgress) {
                if (!requestManager.isPaused) {
                    requestManager.pauseRequests()
                }
            } else {
                if (requestManager.isPaused) {
                    requestManager.resumeRequests()
                }
            }
        }

        onDispose { }
    }
    CompositionLocalProvider(
        LocalIsScrollInProgress provides isScrollInProgress
    ) {
        LazyColumn(
            modifier = modifier,
            state = state,
            contentPadding = contentPadding,
            reverseLayout = reverseLayout,
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
            flingBehavior = flingBehavior,
            content = content,
        )
    }
}
