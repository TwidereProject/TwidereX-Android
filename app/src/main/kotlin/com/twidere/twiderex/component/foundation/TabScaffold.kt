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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Velocity
import com.twidere.twiderex.extensions.isInRange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
private fun rememberTabScaffoldState(): TabScaffoldState {
    val scope = rememberCoroutineScope()
    val saver = remember {
        TabScaffoldState.Saver(scope = scope)
    }
    return rememberSaveable(
        saver = saver
    ) {
        TabScaffoldState(scope = scope)
    }
}

private class TabScaffoldState(
    private val scope: CoroutineScope,
    initialOffset: Float = 0f,
    initialMaxOffset: Float = 0f,
) : NestedScrollConnection {

    companion object {
        fun Saver(
            scope: CoroutineScope,
        ): Saver<TabScaffoldState, *> = listSaver(
            save = {
                listOf(it.offset, it.maxOffset)
            },
            restore = {
                TabScaffoldState(
                    scope = scope,
                    initialOffset = it[0],
                    initialMaxOffset = it[1],
                )
            }
        )
    }

    fun offsetState() = _offset.asState()

    private val offset: Float
        get() = _offset.value

    private var _offset = Animatable(initialOffset)

    var maxOffset by mutableStateOf(initialMaxOffset)

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

    override suspend fun onPreFling(available: Velocity): Velocity {
        return if (offset == 0f || offset.isInRange(maxOffset, 0f)) {
            fling(-available.y * 2f)
            available
        } else {
            Velocity.Zero
        }
    }

    override suspend fun onPostFling(
        consumed: Velocity,
        available: Velocity,
    ): Velocity {
        available.y.takeIf { it != 0f }?.let { velocity ->
            fling(velocity)
        }
        return available
    }

    suspend fun snapTo(value: Float) {
        _offset.snapTo(value)
    }

    suspend fun fling(velocity: Float) {
        _offset.animateDecay(
            velocity,
            exponentialDecay()
        )
    }

    fun drag(delta: Float): Float {
        return if (delta < 0 && offset > maxOffset || delta > 0 && offset < 0f) {
            scope.launch {
                snapTo((offset + delta).coerceIn(maxOffset, 0f))
            }
            delta
        } else {
            0f
        }
    }

    fun updateBounds(maxOffset: Float) {
        this.maxOffset = maxOffset
        _offset.updateBounds(maxOffset, 0f)
    }
}

private class TabScaffoldHeaderState {
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
    modifier: Modifier = Modifier,
    onScroll: (percent: Float) -> Unit = {},
    appbar: @Composable () -> Unit = {},
    header: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val state = rememberTabScaffoldState()
    val headerState = remember {
        TabScaffoldHeaderState()
    }
    val offset by state.offsetState()
    LaunchedEffect(state.maxOffset, offset) {
        snapshotFlow {
            if (state.maxOffset != 0f) {
                offset.absoluteValue / state.maxOffset.absoluteValue
            } else {
                0f
            }
        }
            .distinctUntilChanged()
            .collect {
                onScroll.invoke(it)
            }
    }
    Layout(
        modifier = modifier
            .nestedScroll(state)
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onVerticalDrag = { change, dragAmount ->
                        if (state.drag(dragAmount) != 0f) {
                            change.consumePositionChange()
                            headerState.addPosition(
                                change.uptimeMillis,
                                change.position,
                            )
                        }
                    },
                    onDragEnd = {
                        scope.launch {
                            state.fling(headerState.dragEnd().y)
                        }
                    },
                    onDragCancel = {
                        headerState.resetTracking()
                    }
                )
            },
        content = {
            Box {
                header.invoke()
            }
            Box {
                content.invoke()
            }
            Box {
                appbar.invoke()
            }
        },
    ) { measurables, constraints ->
        layout(constraints.maxWidth, constraints.maxHeight) {
            val headerPlaceable =
                measurables[0].measure(constraints.copy(maxHeight = Constraints.Infinity))
            headerPlaceable.place(0, offset.roundToInt())
            val appbarPlaceable =
                measurables[2].measure(constraints = constraints.copy(maxHeight = Constraints.Infinity))
            appbarPlaceable.place(0, 0)
            state.updateBounds(-(headerPlaceable.height.toFloat() - appbarPlaceable.height))
            val contentPlaceable =
                measurables[1].measure(constraints.copy(maxHeight = constraints.maxHeight - appbarPlaceable.height))
            contentPlaceable.place(
                0,
                max(offset.roundToInt() + headerPlaceable.height, appbarPlaceable.height)
            )
        }
    }
}
