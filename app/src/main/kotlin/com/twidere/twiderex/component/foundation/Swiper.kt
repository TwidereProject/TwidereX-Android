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
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalAnimationClock
import androidx.compose.ui.unit.Constraints
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.math.withSign

class SwiperState(
    clock: AnimationClockObservable,
    private val constraints: Constraints,
    private val onDismiss: () -> Unit,
    private val onEnd: () -> Unit = {},
) {
    var offset: Float
        get() = _offset.value
        set(value) {
            _offset.snapTo(value)
        }

    private var _offset = AnimatedFloatModel(0f, clock = clock)

    fun fling(velocity: Float) {
        val value = _offset.value
        when {
            velocity.absoluteValue > 4000f -> {
                dismiss()
            }
            value.absoluteValue < constraints.maxHeight * 0.5 -> {
                restore()
            }
            value.absoluteValue < constraints.maxHeight -> {
                dismiss()
            }
        }
    }

    private fun dismiss() {
        _offset.animateTo(constraints.maxHeight.toFloat().withSign(_offset.value)) { _, _ ->
            onDismiss.invoke()
        }
    }

    private fun restore() {
        _offset.animateTo(0f)
        onEnd.invoke()
    }
}

@Composable
fun Swiper(
    orientation: Orientation = Orientation.Vertical,
    enabled: Boolean = true,
    reverseDirection: Boolean = false,
    onStart: () -> Unit = {},
    onEnd: () -> Unit = {},
    onDismiss: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    BoxWithConstraints {
        val clock = LocalAnimationClock.current
        val state = remember {
            SwiperState(
                clock = clock,
                constraints = constraints,
                onDismiss = onDismiss,
                onEnd = onEnd,
            )
        }
        Layout(
            modifier = Modifier.draggable(
                orientation = orientation,
                enabled = enabled,
                reverseDirection = reverseDirection,
                onDragStopped = { velocity ->
                    state.fling(velocity)
                },
                onDragStarted = {
                    onStart.invoke()
                },
            ) { dy ->
                with(state) {
                    offset += dy
                }
            },
            content = content,
        ) { measurables, constraints ->
            layout(constraints.maxWidth, constraints.maxHeight) {
                val offset = state.offset
                val childConstraints = constraints.copy(minWidth = 0, minHeight = 0)
                measurables
                    .map {
                        it.measure(childConstraints)
                    }
                    .forEach { placeable ->
                        // TODO: current this centers each page. We should investigate reading
                        //  gravity modifiers on the child, or maybe as a param to Swiper.
                        val xCenterOffset = (constraints.maxWidth - placeable.width) / 2
                        val yCenterOffset = (constraints.maxHeight - placeable.height) / 2
                        placeable.place(
                            x = xCenterOffset,
                            y = yCenterOffset + offset.roundToInt(),
                        )
                    }
            }
        }
    }
}
