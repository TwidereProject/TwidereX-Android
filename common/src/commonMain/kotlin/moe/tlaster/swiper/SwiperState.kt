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
package moe.tlaster.swiper

import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlin.math.absoluteValue
import kotlin.math.withSign

@Composable
fun rememberSwiperState(
    onStart: () -> Unit = {},
    onDismiss: () -> Unit = {},
    onEnd: () -> Unit = {},
): SwiperState {
    return rememberSaveable(
        saver = SwiperState.Saver(
            onStart, onDismiss, onEnd
        )
    ) {
        SwiperState(
            onStart = onStart,
            onDismiss = onDismiss,
            onEnd = onEnd,
        )
    }
}

@Stable
class SwiperState(
    internal val onStart: () -> Unit = {},
    internal val onDismiss: () -> Unit = {},
    internal val onEnd: () -> Unit = {},
    initialOffset: Float = 0f,
) {
    internal var maxHeight: Int = 0
        set(value) {
            field = value
            _offset.updateBounds(lowerBound = -value.toFloat(), upperBound = value.toFloat())
        }
    internal var dismissed by mutableStateOf(false)
    private var _offset = Animatable(initialOffset)

    val offset: Float
        get() = _offset.value

    val progress: Float
        get() = (offset.absoluteValue / (if (maxHeight == 0) 1 else maxHeight)).coerceIn(
            maximumValue = 1f,
            minimumValue = 0f
        )

    internal suspend fun snap(value: Float) {
        _offset.snapTo(value)
    }

    internal suspend fun fling(velocity: Float) {
        val value = _offset.value
        when {
            velocity.absoluteValue > 4000f -> {
                dismiss(velocity)
            }
            value.absoluteValue < maxHeight * 0.5 -> {
                restore()
            }
            value.absoluteValue < maxHeight -> {
                dismiss(velocity)
            }
        }
    }

    private suspend fun dismiss(velocity: Float) {
        dismissed = true
        _offset.animateTo(maxHeight.toFloat().withSign(_offset.value), initialVelocity = velocity)
        onDismiss.invoke()
    }

    private suspend fun restore() {
        onEnd.invoke()
        _offset.animateTo(0f)
    }

    companion object {
        fun Saver(
            onStart: () -> Unit = {},
            onDismiss: () -> Unit = {},
            onEnd: () -> Unit = {},
        ): Saver<SwiperState, *> = listSaver(
            save = {
                listOf(
                    it.offset,
                )
            },
            restore = {
                SwiperState(
                    onStart = onStart,
                    onDismiss = onDismiss,
                    onEnd = onEnd,
                    initialOffset = it[0],
                )
            }
        )
    }
}
