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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offsetPx
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Surface
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.onCommit
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.twidere.twiderex.annotations.IncomingComposeUpdate

private val RefreshDistance = 80.dp

@OptIn(ExperimentalMaterialApi::class)
@IncomingComposeUpdate
@Composable
fun SwipeToRefreshLayout(
    refreshingState: Boolean,
    onRefresh: () -> Unit,
    refreshIndicator: @Composable () -> Unit = {
        Surface(elevation = 10.dp, shape = CircleShape) {
            CircularProgressIndicator(
                modifier = Modifier
                    .preferredSize(36.dp)
                    .padding(4.dp)
            )
        }
    },
    content: @Composable () -> Unit
) {
    val refreshDistance = with(DensityAmbient.current) { RefreshDistance.toPx() }
    val state = rememberSwipeableState(refreshingState) { newValue ->
        // compare both copies of the swipe state before calling onRefresh(). This is a workaround.
        if (newValue && !refreshingState) onRefresh()
        true
    }

    Box(
        modifier = Modifier
            .swipeable(
                state = state,
                anchors = mapOf(
                    -refreshDistance to false,
                    refreshDistance to true
                ),
                thresholds = { _, _ -> FractionalThreshold(0.5f) },
                orientation = Orientation.Vertical
            )
            .fillMaxSize()
    ) {
        content()
        Box(
            modifier = Modifier
                .zIndex(1f)
                .align(Alignment.TopCenter)
                .offsetPx(y = state.offset)
        ) {
            if (state.offset.value != -refreshDistance) {
                refreshIndicator()
            }
        }

        // TODO (https://issuetracker.google.com/issues/164113834): This state->event trampoline is a
        //  workaround for a bug in the SwipableState API. Currently, state.value is a duplicated
        //  source of truth of refreshingState.
        onCommit(refreshingState) {
            state.animateTo(refreshingState)
        }
    }
}
