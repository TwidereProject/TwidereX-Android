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

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt

@Composable
fun rememberReorderableColumnState(
    onReorder: (oldIndex: Int, newIndex: Int) -> Unit
) = remember {
    ReorderableColumnState(
        onReorder = onReorder
    )
}

@Stable
class ReorderableColumnState(
    private val onReorder: (oldIndex: Int, newIndex: Int) -> Unit,
) {
    internal var reordering by mutableStateOf(false)
    internal var draggingItemIndex: Int = -1
    internal var newTargetIndex by mutableStateOf(-1)
    internal var offsetY by mutableStateOf(0f)
    internal var childSizes = arrayListOf<IntSize>()

    internal fun start(index: Int) {
        draggingItemIndex = index
        reordering = true
    }

    internal fun drag(y: Float) {
        offsetY += y
        if (offsetY.roundToInt() == 0) {
            return
        }

        val newOffset =
            (childSizes.subList(0, draggingItemIndex).sumOf { it.height } + offsetY).roundToInt()

        newTargetIndex = ArrayList(childSizes)
            .apply {
                removeAt(draggingItemIndex)
            }
            .map { it.height }
            .runningReduce { acc, i -> acc + i }
            .let { it + newOffset }
            .sortedBy { it }
            .indexOf(newOffset)
            .let {
                if (offsetY < 0) {
                    it + 1
                } else {
                    it
                }
            }
    }

    internal fun cancel() {
        reordering = false
        draggingItemIndex = -1
        newTargetIndex = -1
        offsetY = 0f
    }

    internal fun drop() {
        if (offsetY.roundToInt() == 0) {
            cancel()
            return
        }
        val newOffset =
            (childSizes.subList(0, draggingItemIndex).sumOf { it.height } + offsetY).roundToInt()

        val newIndex = ArrayList(childSizes)
            .apply {
                removeAt(draggingItemIndex)
            }
            .map { it.height }
            .runningReduce { acc, i -> acc + i }
            .let { it + newOffset }
            .sortedBy { it }
            .indexOf(newOffset)
            .let {
                if (offsetY < 0) {
                    it + 1
                } else {
                    it
                }
            }

        onReorder.invoke(draggingItemIndex, newIndex)

        reordering = false
        draggingItemIndex = -1
        newTargetIndex = -1
        offsetY = 0f
    }
}

@Composable
fun <T> ReorderableColumn(
    modifier: Modifier = Modifier,
    data: List<T>,
    state: ReorderableColumnState,
    dragingContent: @Composable ((T) -> Unit)? = null,
    itemContent: @Composable (T) -> Unit,
) {
    val view = getLocalView()
    Layout(
        modifier = modifier,
        content = {
            data.forEachIndexed { index, item ->
                Box(
                    modifier = Modifier
                        .pointerInput(Unit) {
                            detectDragGesturesAfterLongPress(
                                onDragCancel = {
                                    state.cancel()
                                },
                                onDragEnd = {
                                    state.drop()
                                },
                                onDragStart = {
                                    view.performHapticFeedback()
                                    state.start(index)
                                },
                                onDrag = { _, dragAmount ->
                                    state.drag(dragAmount.y)
                                }
                            )
                        }
                        .let {
                            if (state.reordering && state.draggingItemIndex == index) {
                                it.zIndex(0.1f)
                            } else {
                                it.zIndex(0f)
                            }
                        }
                ) {
                    if (state.reordering && state.draggingItemIndex == index && dragingContent != null) {
                        dragingContent.invoke(item)
                    } else {
                        itemContent.invoke(item)
                    }
                }
            }
        }
    ) { measurables, constraints ->
        val placeables = measurables.map {
            it.measure(constraints)
        }
        state.childSizes.clear()
        state.childSizes.addAll(
            placeables.map {
                IntSize(
                    width = it.measuredWidth,
                    height = it.measuredHeight
                )
            }
        )

        layout(
            width = placeables.maxOf { it.measuredWidth },
            height = placeables.sumOf { it.measuredHeight }
        ) {
            var height = 0
            placeables.forEachIndexed { index, placeable ->
                if (state.reordering && index == state.newTargetIndex && index != state.draggingItemIndex && state.offsetY < 0) {
                    height += placeables[state.draggingItemIndex].height
                }
                if (state.reordering && index == state.draggingItemIndex) {
                    placeable.place(
                        0,
                        (height + state.offsetY.roundToInt()).let {
                            if (state.newTargetIndex != -1 && index != state.newTargetIndex && state.offsetY < 0) {
                                it - placeables[state.newTargetIndex].height
                            } else {
                                it
                            }
                        }
                    )
                } else {
                    placeable.place(0, height)
                }
                if (state.reordering && index == state.newTargetIndex && index != state.draggingItemIndex && state.offsetY > 0) {
                    height += placeables[state.draggingItemIndex].height
                }
                if (!state.reordering || index != state.draggingItemIndex || state.newTargetIndex == -1 || index == state.newTargetIndex) {
                    height += placeable.measuredHeight
                }
            }
        }
    }
}
