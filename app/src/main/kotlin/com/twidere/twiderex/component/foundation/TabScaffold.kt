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

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.gesture.nestedscroll.NestedScrollConnection
import androidx.compose.ui.gesture.nestedscroll.NestedScrollSource
import androidx.compose.ui.gesture.nestedscroll.nestedScroll
import androidx.compose.ui.gesture.util.VelocityTracker
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

private class TabScaffoldState(
    private val scope: CoroutineScope,
) : NestedScrollConnection {

    val offset: Float
        get() = _offset.value

    private var _offset = Animatable(0f)

    var maxOffset by mutableStateOf(0)

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val delta = available.y
        return if (delta < 0 && source == NestedScrollSource.Drag) {
            Offset(0f, drag(delta))
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
            Offset(0f, drag(available.y))
        } else {
            Offset.Zero
        }
    }

    override fun onPostFling(
        consumed: Velocity,
        available: Velocity,
        onFinished: (Velocity) -> Unit
    ) {
        scope.launch {
            fling(available.y) {
                onFinished.invoke(available)
            }
        }
    }

    suspend fun snapTo(value: Float) {
        _offset.snapTo(value)
    }

    suspend fun fling(velocity: Float, onFinished: () -> Unit = {}) {
        _offset.animateDecay(
            velocity,
            exponentialDecay()
        )
        onFinished.invoke()
    }

    fun drag(delta: Float): Float {
        return if (delta < 0 && offset > maxOffset || delta > 0 && offset < 0f) {
            scope.launch {
                snapTo((offset + delta).coerceIn(maxOffset.toFloat(), 0f))
            }
            delta
        } else {
            0f
        }
    }

    fun updateBounds(maxOffset: Float) {
        this.maxOffset = maxOffset.toInt()
        _offset.updateBounds(maxOffset, 0f)
    }
}

class TabScaffoldHeaderState {
    private val velocityTracker = VelocityTracker()

    fun dragEnd(): Velocity {
        return velocityTracker.calculateVelocity()
    }

    fun addPosition(uptimeMillis: Long, position: Offset) {
        velocityTracker.addPosition(uptimeMillis, position)
    }

    fun resetTracking() {
        velocityTracker.resetTracking()
    }
}

@Composable
fun TabScaffold(
    onScroll: (percent: Float) -> Unit = {},
    header: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val state = remember {
        TabScaffoldState(scope = scope)
    }
    val headerState = remember {
        TabScaffoldHeaderState()
    }
    DisposableEffect(state.maxOffset, state.offset) {
        if (state.maxOffset != 0) {
            onScroll.invoke(state.offset.absoluteValue / state.maxOffset.absoluteValue.toFloat())
        }
        onDispose { }
    }
    Layout(
        modifier = Modifier
            .nestedScroll(state),
        content = {
            Box(
                modifier = Modifier.pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onVerticalDrag = { change, dragAmount ->
                            change.consumeAllChanges()
                            state.drag(dragAmount)
                            headerState.addPosition(
                                change.uptimeMillis,
                                change.position,
                            )
                        },
                        onDragEnd = {
                            scope.launch {
                                state.fling(-headerState.dragEnd().y)
                            }
                        },
                        onDragCancel = {
                            headerState.resetTracking()
                        }
                    )
                }
            ) {
                header.invoke()
            }
            Box {
                content.invoke()
            }
        },
    ) { measurables, constraints ->
        layout(constraints.maxWidth, constraints.maxHeight) {
            val headerPlaceable = measurables[0].measure(constraints)
            state.updateBounds(-headerPlaceable.height.toFloat())
            headerPlaceable.place(0, state.offset.roundToInt())
            val contentPlaceable = measurables[1].measure(constraints)
            contentPlaceable.place(0, state.offset.roundToInt() + headerPlaceable.height)
        }
    }
}
