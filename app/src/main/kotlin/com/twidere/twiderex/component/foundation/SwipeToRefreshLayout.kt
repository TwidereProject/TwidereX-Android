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
package com.twidere.twiderex.component.foundation

import androidx.compose.animation.AnimatedFloatModel
import androidx.compose.animation.asDisposableClock
import androidx.compose.animation.core.AnimationClockObservable
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.savedinstancestate.mapSaver
import androidx.compose.runtime.savedinstancestate.rememberSavedInstanceState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.gesture.nestedscroll.NestedScrollConnection
import androidx.compose.ui.gesture.nestedscroll.NestedScrollSource
import androidx.compose.ui.gesture.nestedscroll.nestedScroll
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.platform.AmbientAnimationClock
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.annotations.IncomingComposeUpdate
import kotlin.math.roundToInt

private val RefreshDistance = 80.dp
private val MinRefreshDistance = 32.dp

@Composable
fun rememberSwipeToRefreshState(
    initialValue: Boolean,
    initialOffset: Float,
    maxOffset: Float,
    minOffset: Float,
    onRefresh: () -> Unit,
): SwipeToRefreshState {
    val clock = AmbientAnimationClock.current.asDisposableClock()
    return rememberSavedInstanceState(
        clock,
        saver = mapSaver(
            save = {
                mapOf(
                    "value" to it.value,
                )
            },
            restore = {
                SwipeToRefreshState(
                    clock,
                    it["value"] as Boolean,
                    initialOffset = initialOffset,
                    maxOffset = maxOffset,
                    minOffset = minOffset,
                    onRefresh = onRefresh,
                )
            }
        )
    ) {
        SwipeToRefreshState(
            clock = clock,
            initialValue = initialValue,
            initialOffset = initialOffset,
            maxOffset = maxOffset,
            minOffset = minOffset,
            onRefresh = onRefresh,
        )
    }
}

class SwipeToRefreshState(
    clock: AnimationClockObservable,
    initialValue: Boolean,
    private val initialOffset: Float,
    private val minOffset: Float,
    private val maxOffset: Float,
    private val onRefresh: () -> Unit,
) : NestedScrollConnection {

    var value: Boolean by mutableStateOf(initialValue)

    var offset: Float
        get() = _offset.value
        set(value) {
            _offset.snapTo(value)
        }

    private var _offset = AnimatedFloatModel(
        if (initialValue) {
            minOffset
        } else {
            initialOffset
        },
        clock = clock
    )

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val delta = available.toFloat()
        return if (delta < 0 && source == NestedScrollSource.Drag) {
            drag(delta).toOffset()
        } else {
            Offset.Zero
        }
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        return if (source == NestedScrollSource.Drag) {
            drag(available.toFloat()).toOffset()
        } else {
            Offset.Zero
        }
    }

    override fun onPreFling(available: Velocity): Velocity {
        val toFling = Offset(available.x, available.y).toFloat()
        return if (toFling < 0) {
            Velocity.Zero
        } else {
            fling()
            Velocity.Zero
        }
    }

    override fun onPostFling(
        consumed: Velocity,
        available: Velocity,
        onFinished: (Velocity) -> Unit
    ) {
        fling() {
            // since we go to the anchor with tween settling, consume all for the best UX
            onFinished.invoke(available)
        }
    }

    fun fling(onFinished: () -> Unit = {}) {
        val offsetValue = _offset.value
        when {
            offsetValue >= 0 -> {
                if (!value) {
                    value = true
                    onRefresh.invoke()
                }
                _offset.animateTo(minOffset) { _, _ ->
                    onFinished.invoke()
                }
            }
            else -> {
                _offset.animateTo(initialOffset) { _, _ ->
                    onFinished.invoke()
                }
            }
        }
    }

    fun drag(delta: Float): Float {
        return if (!value) {
            offset = (offset + delta).coerceAtMost(maxOffset)
            delta
        } else {
            minOffset
        }
    }

    fun animateTo(refreshingState: Boolean) {
        value = refreshingState
        when {
            refreshingState -> {
                _offset.animateTo(minOffset)
            }
            else -> {
                _offset.animateTo(initialOffset)
            }
        }
    }

    private fun Float.toOffset(): Offset = Offset(0f, this)

    private fun Offset.toFloat(): Float = this.y
}

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
    val refreshDistance = with(AmbientDensity.current) { RefreshDistance.toPx() }
    val minRefreshDistance = with(AmbientDensity.current) { MinRefreshDistance.toPx() }
    val state = rememberSwipeToRefreshState(
        initialValue = refreshingState,
        initialOffset = -refreshDistance,
        maxOffset = refreshDistance,
        minOffset = minRefreshDistance,
        onRefresh = onRefresh,
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .nestedScroll(state)
            .draggable(
                orientation = Orientation.Vertical,
                onDrag = { dy ->
                    state.drag(dy)
                },
                onDragStopped = {
                    state.fling()
                },
            )
    ) {
        content()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .offset { IntOffset(0, state.offset.roundToInt()) },
            contentAlignment = Alignment.TopCenter,
        ) {
            if (state.offset != -refreshDistance) {
                refreshIndicator()
            }
        }

        DisposableEffect(refreshingState) {
            state.animateTo(refreshingState)
            onDispose { }
        }
    }
}
