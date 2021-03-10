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
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.layout
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
private fun rememberZoomableState(): ZoomableState {
    val saver = remember {
        ZoomableState.Saver()
    }

    return rememberSaveable(
        saver = saver
    ) {
        ZoomableState()
    }
}

private class ZoomableState(
    initialTranslateX: Float = 0f,
    initialTranslateY: Float = 0f,
    initialScale: Float = 1f,
) {
    companion object {
        fun Saver(): Saver<ZoomableState, *> = listSaver(
            save = {
                listOf(it.translateX.value, it.translateY.value, it.scale)
            },
            restore = {
                ZoomableState(
                    initialTranslateX = it[0],
                    initialTranslateY = it[1],
                    initialScale = it[2],
                )
            }
        )
    }

    val velocityTracker = VelocityTracker()

    var translateY = Animatable(initialTranslateY)

    var translateX = Animatable(initialTranslateX)

    var scale by mutableStateOf(initialScale)

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
    }
}

@Composable
fun Zoomable(
    modifier: Modifier = Modifier,
    minScale: Float = 1F,
    onZooming: (scale: Float) -> Unit = {},
    content: @Composable BoxScope.() -> Unit,
) {
    val scope = rememberCoroutineScope()
    BoxWithConstraints {
        val state = rememberZoomableState()
        val translateY by remember { state.translateY.asState() }
        val translateX by remember { state.translateX.asState() }
        var locked by remember { mutableStateOf(false) }
        var childWidth by remember { mutableStateOf(0) }
        var childHeight by remember { mutableStateOf(0) }
        LaunchedEffect(
            childHeight,
            childWidth,
            state.scale,
        ) {
            val maxX = (childWidth * state.scale - constraints.maxWidth)
                .coerceAtLeast(0F) / 2F
            val maxY = (childHeight * state.scale - constraints.maxHeight)
                .coerceAtLeast(0F) / 2F
            state.updateBounds(maxX, maxY)
        }
        LaunchedEffect(state.scale) {
            onZooming.invoke(state.scale)
            locked = state.scale != minScale
        }
        val transformableState =
            rememberTransformableState { zoomChange, _, _ ->
                state.scale = (state.scale * zoomChange).coerceAtLeast(minScale)
            }
        Box(
            modifier = modifier
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            if (locked) {
                                change.consumePositionChange()
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
                        },
                    )
                }
                .transformable(state = transformableState)
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
                            scaleX = state.scale
                            scaleY = state.scale
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
