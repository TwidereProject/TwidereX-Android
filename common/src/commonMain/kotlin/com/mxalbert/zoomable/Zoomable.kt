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
package com.mxalbert.zoomable

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.foundation.gestures.awaitVerticalTouchSlopOrCancellation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * A zoomable layout that supports zooming in and out, dragging, double tap and dismiss gesture.
 *
 * @param modifier The modifier to apply to this layout.
 * @param state The state object to be used to control or observe the state.
 * @param enabled Controls the enabled state. When false, all gestures will be ignored.
 * @param dismissGestureEnabled Whether to enable dismiss gesture detection.
 * @param onDismiss Will be called when dismiss gesture is detected. Should return a boolean
 * indicating whether the dismiss request is handled.
 * @param content a block which describes the content.
 */
@Composable
fun Zoomable(
    modifier: Modifier = Modifier,
    state: ZoomableState = rememberZoomableState(),
    enabled: Boolean = true,
    dismissGestureEnabled: Boolean = false,
    onDismiss: () -> Boolean = { false },
    onClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val dismissGestureEnabledState = rememberUpdatedState(dismissGestureEnabled)
    val scope = rememberCoroutineScope()
    val gesturesModifier = if (!enabled) Modifier else Modifier
        .pointerInput(Unit) {
            detectTapAndDragGestures(
                state = state,
                dismissGestureEnabled = dismissGestureEnabledState,
                onDismiss = onDismiss,
                onClick = onClick,
            )
        }
        .transformable(
            state = rememberTransformableState { zoomChange, panChange, _ ->
                if (state.dismissDragAbsoluteOffsetY == 0f) {
                    scope.launch {
                        state.onZoomChange(zoomChange)
                        state.onDrag(panChange)
                    }
                }
            }
        )

    Box(
        modifier = modifier
            .then(gesturesModifier)
            .layout { measurable, constraints ->
                val width = constraints.maxWidth
                val height = constraints.maxHeight
                val placeable = measurable.measure(
                    Constraints(
                        maxWidth = (width * state.scale).roundToInt(),
                        maxHeight = (height * state.scale).roundToInt()
                    )
                )
                state.size = IntSize(width, height)
                state.childSize = Size(
                    placeable.width / state.scale,
                    placeable.height / state.scale
                )
                layout(width, height) {
                    placeable.placeWithLayer(0, 0) {
                        translationX = state.translationX - state.boundOffset.x
                        translationY =
                            state.translationY - state.boundOffset.y + state.dismissDragOffsetY
                    }
                }
            }
    ) {
        content()
    }
}

internal suspend fun PointerInputScope.detectTapAndDragGestures(
    state: ZoomableState,
    dismissGestureEnabled: State<Boolean>,
    onDismiss: () -> Boolean,
    onClick: () -> Unit
) = coroutineScope {
    launch {
        detectTapGestures(
            onDoubleTap = { offset ->
                launch {
                    val isZooming = state.isZooming
                    val targetScale =
                        if (isZooming) state.minScale else state.doubleTapScale
                    state.animateScaleTo(
                        targetScale = targetScale,
                        targetTranslation = if (isZooming) {
                            Offset.Zero
                        } else {
                            state.calculateTargetTranslation(offset) * targetScale
                        }
                    )
                }
            },
            onPress = {
                state.onPress()
            },
            onTap = {
                onClick.invoke()
            }
        )
    }
    launch {
        detectDragGestures(
            state = state,
            dismissGestureEnabled = dismissGestureEnabled,
            onDrag = { change, dragAmount ->
                if (state.isZooming) {
                    launch {
                        state.onDrag(dragAmount)
                        state.addPosition(
                            change.uptimeMillis,
                            change.position
                        )
                    }
                } else {
                    state.onDismissDrag(dragAmount.y)
                }
            },
            onDragCancel = {
                if (state.isZooming) {
                    state.resetTracking()
                } else {
                    launch {
                        state.onDismissDragEnd()
                    }
                }
            },
            onDragEnd = {
                launch {
                    if (state.isZooming) {
                        state.onDragEnd()
                    } else {
                        if (!(state.shouldDismiss && onDismiss())) {
                            state.onDismissDragEnd()
                        }
                    }
                }
            }
        )
    }
}

private suspend fun PointerInputScope.detectDragGestures(
    state: ZoomableState,
    dismissGestureEnabled: State<Boolean>,
    onDragEnd: () -> Unit = {},
    onDragCancel: () -> Unit = {},
    onDrag: (change: PointerInputChange, dragAmount: Offset) -> Unit
) {
    forEachGesture {
        awaitPointerEventScope {
            // We have to always call this or we'll get a crash if we do nothing
            val down = awaitFirstDown(requireUnconsumed = false)
            if (state.isZooming || dismissGestureEnabled.value) {
                var overSlop = Offset.Zero
                val drag = if (state.isZooming) {
                    awaitTouchSlopOrCancellation(down.id) { change, over ->
                        change.consumePositionChange()
                        overSlop = over
                    }
                } else {
                    awaitVerticalTouchSlopOrCancellation(down.id) { change, over ->
                        change.consumePositionChange()
                        overSlop = Offset(0f, over)
                    }
                }
                if (drag != null) {
                    onDrag(drag, overSlop)
                    if (
                        !drag(drag.id) {
                            onDrag(it, it.positionChange())
                            it.consumePositionChange()
                        }
                    ) {
                        onDragCancel()
                    } else {
                        onDragEnd()
                    }
                }
            }
        }
    }
}

private fun ZoomableState.calculateTargetTranslation(doubleTapPoint: Offset): Offset =
    (size.toSize().center + Offset(translationX, translationY) - doubleTapPoint) / scale
