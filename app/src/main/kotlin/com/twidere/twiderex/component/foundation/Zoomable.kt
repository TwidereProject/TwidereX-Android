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
import androidx.compose.animation.core.AnimationClockObservable
import androidx.compose.animation.core.TargetAnimation
import androidx.compose.animation.core.fling
import androidx.compose.foundation.gestures.zoomable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.gesture.DragObserver
import androidx.compose.ui.gesture.rawDragGestureFilter
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.AmbientAnimationClock
import androidx.compose.ui.unit.Constraints

class ZoomableState(
    clock: AnimationClockObservable,
    private val constraints: Constraints,
) {
    var translateY: Float
        get() = _translateY.value
        set(value) {
            _translateY.snapTo(value)
        }

    private var _translateY = AnimatedFloatModel(0f, clock = clock)

    var translateX: Float
        get() = _translateX.value
        set(value) {
            _translateX.snapTo(value)
        }

    private var _translateX = AnimatedFloatModel(0f, clock = clock)

    fun fling(velocity: Offset, maxX: Float, maxY: Float) {
        _translateY.fling(
            velocity.y / 2f,
            adjustTarget = {
                TargetAnimation(it.coerceIn(-maxY, maxY))
            }
        )
        _translateX.fling(
            velocity.x / 2f,
            adjustTarget = {
                TargetAnimation(it.coerceIn(-maxX, maxX))
            }
        )
    }

    fun drag(dragDistance: Offset, x: Float, y: Float) {
        translateY = (translateY + dragDistance.y).coerceIn(-y, y)
        translateX = (translateX + dragDistance.x).coerceIn(-x, x)
    }
}

@Composable
fun Zoomable(
    modifier: Modifier = Modifier,
    minScale: Float = 1F,
    onZoomStarted: ((scale: Float) -> Unit)? = null,
    onZoomStopped: ((scale: Float) -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    BoxWithConstraints {
        val clock = AmbientAnimationClock.current
        val state = remember {
            ZoomableState(
                clock = clock,
                constraints = constraints,
            )
        }
        var locked by remember { mutableStateOf(false) }
        var scale by remember { mutableStateOf(1f) }
        var childWidth by remember { mutableStateOf(0) }
        var childHeight by remember { mutableStateOf(0) }
        val observer = remember {
            object : DragObserver {
                override fun onStop(velocity: Offset) {
                    if (locked) {
                        val maxX = (childWidth * scale - constraints.maxWidth)
                            .coerceAtLeast(0F) / 2F
                        val maxY = (childHeight * scale - constraints.maxHeight)
                            .coerceAtLeast(0F) / 2F
                        state.fling(velocity, maxX, maxY)
                    }
                }

                override fun onDrag(dragDistance: Offset): Offset {
                    if (locked) {
                        val maxX = (childWidth * scale - constraints.maxWidth)
                            .coerceAtLeast(0F) / 2F
                        val maxY = (childHeight * scale - constraints.maxHeight)
                            .coerceAtLeast(0F) / 2F
                        state.drag(dragDistance, maxX, maxY)
                    }
                    return super.onDrag(dragDistance)
                }
            }
        }
        Box(
            modifier = modifier
                .zoomable(
                    onZoomDelta = { scale = (scale * it).coerceAtLeast(minScale) },
                    onZoomStarted = {
                        locked = true
                        onZoomStarted?.invoke(scale)
                    },
                    onZoomStopped = {
                        locked = scale != minScale
                        onZoomStopped?.invoke(scale)
                    },
                )
                .rawDragGestureFilter(observer)
                .layout { measurable, constraints ->
                    val placeable =
                        measurable.measure(constraints = constraints)
                    childHeight = placeable.height
                    childWidth = placeable.width
                    layout(
                        width = constraints.maxWidth,
                        height = constraints.maxHeight
                    ) {
                        placeable.placeRelativeWithLayer(
                            (constraints.maxWidth - placeable.width) / 2,
                            (constraints.maxHeight - placeable.height) / 2
                        ) {
                            scaleX = scale
                            scaleY = scale
                            translationX = state.translateX
                            translationY = state.translateY
                        }
                    }
                }
        ) {
            content.invoke(this)
        }
    }
}
