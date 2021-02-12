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
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.zoomable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.gesture.util.VelocityTracker
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import com.twidere.twiderex.extensions.isInRange
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.withSign

class ZoomableState {
    val velocityTracker = VelocityTracker()

    var translateY = Animatable(0f)

    var translateX = Animatable(0f)

    private suspend fun fling(velocity: Offset) = coroutineScope {
        launch {
            translateY.animateDecay(
                velocity.y / 2f,
                exponentialDecay()
            )
        }
        launch {
            translateX.animateDecay(
                velocity.x / 2f,
                exponentialDecay()
            )
        }
    }

    suspend fun drag(dragDistance: Offset) = coroutineScope {
        launch {
            translateY.snapTo((translateY.value + dragDistance.y))
        }
        launch {
            translateX.snapTo((translateX.value + dragDistance.x))
        }
    }

    suspend fun dragEnd() {
        val velocity = velocityTracker.calculateVelocity()
        fling(Offset(velocity.x, velocity.y))
    }

    suspend fun updateBounds(maxX: Float, maxY: Float) = coroutineScope {
        translateY.updateBounds(-maxY, maxY)
        translateX.updateBounds(-maxX, maxX)
        // Workaround for https://issuetracker.google.com/issues/180031493
        if (!translateX.value.isInRange(-maxX, maxX)) {
            launch {
                translateX.snapTo(maxX.withSign(translateX.value))
            }
        }
        if (!translateY.value.isInRange(-maxY, maxY)) {
            launch {
                translateY.snapTo(maxY.withSign(translateY.value))
            }
        }
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
    val scope = rememberCoroutineScope()
    BoxWithConstraints {
        val state = remember {
            ZoomableState()
        }
        val translateY by remember { state.translateY.asState() }
        val translateX by remember { state.translateX.asState() }
        var locked by remember { mutableStateOf(false) }
        var scale by remember { mutableStateOf(1f) }
        var childWidth by remember { mutableStateOf(0) }
        var childHeight by remember { mutableStateOf(0) }
        LaunchedEffect(
            childHeight,
            childWidth,
            scale,
        ) {
            val maxX = (childWidth * scale - constraints.maxWidth)
                .coerceAtLeast(0F) / 2F
            val maxY = (childHeight * scale - constraints.maxHeight)
                .coerceAtLeast(0F) / 2F
            state.updateBounds(maxX, maxY)
        }
        Box(
            modifier = modifier
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            if (locked) {
                                change.consumePositionChange(change.position.x, change.position.y)
                                scope.launch {
                                    state.drag(dragAmount)
                                    state.velocityTracker.addPosition(
                                        change.uptimeMillis,
                                        change.position
                                    )
                                }
                            }
                        },
                        onDragCancel = {
                            state.velocityTracker.resetTracking()
                        },
                        onDragEnd = {
                            if (locked) {
                                scope.launch {
                                    state.dragEnd()
                                }
                            }
                        }
                    )
                }
                .zoomable(
                    onZoomDelta = {
                        scale = (scale * it).coerceAtLeast(minScale)
                    },
                    onZoomStarted = {
                        locked = true
                        onZoomStarted?.invoke(scale)
                    },
                    onZoomStopped = {
                        locked = scale != minScale
                        onZoomStopped?.invoke(scale)
                    },
                )
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
                            translationX = translateX
                            translationY = translateY
                        }
                    }
                }
        ) {
            content.invoke(this)
        }
    }
}
