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
package com.twidere.twiderex.component.foundation

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitVerticalTouchSlopOrCancellation
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.gestures.verticalDrag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val RefreshDistance = 80.dp
private val MinRefreshDistance = 32.dp

@Composable
private fun rememberSwipeToRefreshState(
    scope: CoroutineScope,
    initialValue: Boolean,
    initialOffset: Float,
    maxOffset: Float,
    minOffset: Float,
    onRefresh: () -> Unit,
): SwipeToRefreshState {
    // Avoid creating a new instance every invocation
    val currentOnRefresh = rememberUpdatedState(newValue = onRefresh)
    val saver = remember(
        scope,
        initialValue,
        initialOffset,
        maxOffset,
        minOffset,
        onRefresh,
    ) {
        Saver<SwipeToRefreshState, Boolean>(
            save = {
                it.value
            },
            restore = {
                SwipeToRefreshState(
                    it,
                    scope = scope,
                    initialOffset = initialOffset,
                    maxOffset = maxOffset,
                    minOffset = minOffset,
                    onRefresh = currentOnRefresh,
                )
            }
        )
    }
    return rememberSaveable(
        saver = saver,
    ) {
        SwipeToRefreshState(
            scope = scope,
            initialValue = initialValue,
            initialOffset = initialOffset,
            maxOffset = maxOffset,
            minOffset = minOffset,
            onRefresh = currentOnRefresh,
        )
    }
}

@Stable
private class SwipeToRefreshState(
    initialValue: Boolean,
    private val scope: CoroutineScope,
    private val initialOffset: Float,
    private val minOffset: Float,
    private val maxOffset: Float,
    private val onRefresh: State<() -> Unit>,
) : NestedScrollConnection {

    var value: Boolean by mutableStateOf(initialValue)

    val offset: Float
        get() = _offset.value

    private var _offset = Animatable(
        if (initialValue) {
            minOffset
        } else {
            initialOffset
        },
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

    override suspend fun onPreFling(available: Velocity): Velocity {
        val toFling = Offset(available.x, available.y).toFloat()
        return if (toFling < 0) {
            Velocity.Zero
        } else {
            fling()
            Velocity.Zero
        }
    }

    override suspend fun onPostFling(
        consumed: Velocity,
        available: Velocity,
    ): Velocity {
        fling()
        return available
    }

    suspend fun snapTo(value: Float) {
        _offset.snapTo(value)
    }

    suspend fun fling() {
        val offsetValue = _offset.value
        when {
            offsetValue >= 0 -> {
                if (!value) {
                    value = true
                    onRefresh.value.invoke()
                }
                _offset.animateTo(minOffset)
            }
            else -> {
                _offset.animateTo(initialOffset)
            }
        }
    }

    fun drag(delta: Float): Float {
        return if (!value && (delta > 0 || offset > initialOffset)) {
            scope.launch {
                snapTo((offset + delta).coerceAtMost(maxOffset))
            }
            delta
        } else {
            0f
        }
    }

    suspend fun animateTo(refreshingState: Boolean) {
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
@Composable
fun SwipeToRefreshLayout(
    modifier: Modifier = Modifier,
    refreshingState: Boolean,
    refreshIndicatorPadding: PaddingValues = PaddingValues(0.dp),
    onRefresh: () -> Unit,
    refreshIndicator: @Composable () -> Unit = {
        Surface(elevation = 10.dp, shape = CircleShape) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(36.dp)
                    .padding(4.dp)
            )
        }
    },
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val refreshDistance = with(LocalDensity.current) { RefreshDistance.toPx() }
    val minRefreshDistance = with(LocalDensity.current) { MinRefreshDistance.toPx() }
    val state = rememberSwipeToRefreshState(
        scope = scope,
        initialValue = refreshingState,
        initialOffset = -refreshDistance,
        maxOffset = refreshDistance,
        minOffset = minRefreshDistance,
        onRefresh = onRefresh,
    )
    Box(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(state)
            .pointerInput(Unit) {
                detectVerticalDrag(
                    onVerticalDrag = { change, dragAmount ->
                        if (state.drag(dragAmount) != 0f) {
                            change.consumePositionChange()
                        }
                    },
                    onDragEnd = {
                        scope.launch {
                            state.fling()
                        }
                    }
                )
            }
    ) {
        content()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(refreshIndicatorPadding)
                .offset { IntOffset(0, state.offset.roundToInt()) },
            contentAlignment = Alignment.TopCenter,
        ) {
            if (state.offset != -refreshDistance) {
                refreshIndicator()
            }
        }

        LaunchedEffect(refreshingState) {
            scope.launch {
                state.animateTo(refreshingState)
            }
        }
    }
}

private suspend fun PointerInputScope.detectVerticalDrag(
    onDragStart: (Offset) -> Unit = { },
    onDragEnd: () -> Unit = { },
    onDragCancel: () -> Unit = { },
    onVerticalDrag: (change: PointerInputChange, dragAmount: Float) -> Unit
) {
    forEachGesture {
        awaitPointerEventScope {
            val down = awaitFirstDown(requireUnconsumed = false)
            val drag = awaitVerticalTouchSlopOrCancellation(down.id, onVerticalDrag)
            if (drag != null) {
                onDragStart.invoke(drag.position)
                if (
                    verticalDrag(drag.id) {
                        onVerticalDrag(it, it.positionChange().y)
                    }
                ) {
                    onDragEnd()
                } else {
                    onDragCancel()
                }
            }
        }
    }
}
