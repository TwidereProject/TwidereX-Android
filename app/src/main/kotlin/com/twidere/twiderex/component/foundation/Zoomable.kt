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

@Composable
fun Zoomable(
    minScale: Float = 1F,
    onZoomStarted: ((scale: Float) -> Unit)? = null,
    onZoomStopped: ((scale: Float) -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    BoxWithConstraints {
        var looked by remember { mutableStateOf(false) }
        var scale by remember { mutableStateOf(1f) }
        var translate by remember { mutableStateOf(Offset(0f, 0f)) }
        var childWidth by remember { mutableStateOf(0) }
        var childHeight by remember { mutableStateOf(0) }
        val observer = remember {
            object : DragObserver {
                override fun onDrag(dragDistance: Offset): Offset {
                    if (looked) {
                        val x =
                            (childWidth * scale - constraints.maxWidth)
                                .coerceAtLeast(0F) / 2F
                        val y =
                            (childHeight * scale - constraints.maxHeight)
                                .coerceAtLeast(0F) / 2F
                        translate = translate.plus(dragDistance).let {
                            it.copy(
                                it.x.coerceIn(
                                    -x,
                                    x,
                                ),
                                it.y.coerceIn(
                                    -y,
                                    y,
                                )
                            )
                        }
                    }
                    return super.onDrag(dragDistance)
                }
            }
        }
        Box(
            modifier = Modifier
                .zoomable(
                    onZoomDelta = { scale = (scale * it).coerceAtLeast(minScale) },
                    onZoomStarted = {
                        looked = true
                        onZoomStarted?.invoke(scale)
                    },
                    onZoomStopped = {
                        looked = scale != minScale
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
                            translationX = translate.x
                            translationY = translate.y
                        }
                    }
                }
        ) {
            content.invoke(this)
        }
    }
}
