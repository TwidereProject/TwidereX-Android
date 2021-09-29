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

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Locale

@Stable
class SeekBarState(
    private val duration: Long,
    initPosition: Long = 0L,
    private val stepFreq: Int = 1000,
    private val onDragEnd: (position: Long) -> Unit
) {

    var dragging by mutableStateOf(false)

    var curTime by mutableStateOf(0L)

    var intSize by mutableStateOf(Size.Zero)

    var timePosition by mutableStateOf(initPosition)

    var progressPosition by mutableStateOf(0f)

    fun setSize(size: Size) {
        intSize = size
    }

    fun start() {
        dragging = true
        update()
    }

    fun onDragChange(delta: Float) {
        if (
            delta >= -progressPosition &&
            delta <= intSize.width - progressPosition
        ) {
            progressPosition += delta
            update()
        }
    }

    fun onTimeChange(timePosition: Long) {
        this.timePosition = timePosition.coerceAtMost(duration)
        update()
    }

    fun end() {
        curTime = calDragPos()
        onDragEnd(curTime)
        dragging = false
        timePosition = curTime
        update()
    }

    fun update() {
        if (!dragging)
            curTime = timePosition

        if (!dragging)
            progressPosition = calProgress()
    }

    fun progressTimeText(
        pattern: String = SeekBarDefaults.SeekBarDefaultTimeFormat
    ): String = SimpleDateFormat(
        pattern,
        Locale.getDefault()
    ).format(
        if (!dragging)
            curTime
        else calDragPos()
    )

    fun fullTimeText(
        pattern: String = SeekBarDefaults.SeekBarDefaultTimeFormat
    ): String = SimpleDateFormat(
        pattern, Locale.getDefault()
    ).format(
        duration
    )

    private fun calDragPos(): Long {
        val percent = (
            stepFreq * progressPosition / intSize.width
            ).toInt()
        return duration * percent / stepFreq
    }

    private fun calProgress(): Float {
        val percent = if (duration != 0L)
            curTime * stepFreq / duration
        else 0
        return (intSize.width * percent / stepFreq)
    }
}

@Composable
fun SeekBar(
    modifier: Modifier = Modifier,
    pointColor: Color = Color.White,
    progressLineColor: Color = Color.White,
    backgroundLineColor: Color = Color.Gray.copy(alpha = 0.4f),
    strokeWidth: Dp = SeekBarDefaults.SeekBarStrokeWidth,
    showText: Boolean = true,
    state: SeekBarState,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
    ) {
        if (showText) {
            Text(
                text = state.progressTimeText(),
                modifier = Modifier
                    .padding(horizontal = SeekBarDefaults.SeekBarInnerPaddings)
            )
        }

        Box(
            modifier = Modifier
                .height(SeekBarDefaults.SeekDefaultHeight).weight(1f)
                .draggable(
                    rememberDraggableState { delta ->
                        state.onDragChange(delta)
                    },
                    orientation = Orientation.Horizontal,
                    onDragStarted = {
                        state.start()
                    },
                    onDragStopped = {
                        state.end()
                    }
                )
        ) {
            Canvas(
                modifier = Modifier.matchParentSize()
            ) {
                state.setSize(size)
                drawLine(
                    color = backgroundLineColor,
                    start = Offset(x = 0f, y = (state.intSize.height / 2)),
                    end = Offset(x = state.intSize.width, y = (state.intSize.height / 2)),
                    strokeWidth = strokeWidth.value,
                    cap = StrokeCap.Round
                )

                drawLine(
                    color = progressLineColor,
                    start = Offset(x = 0f, y = (state.intSize.height / 2)),
                    end = Offset(x = state.progressPosition, y = (state.intSize.height / 2)),
                    strokeWidth = strokeWidth.value,
                    cap = StrokeCap.Round
                )

                drawPoints(
                    points = listOf(Offset(state.progressPosition, center.y)),
                    pointMode = PointMode.Points,
                    color = pointColor,
                    strokeWidth = (strokeWidth * 2f).toPx(),
                    cap = StrokeCap.Round
                )
            }
        }

        if (showText) {
            Text(
                text = state.fullTimeText(),
                modifier = Modifier
                    .padding(
                        horizontal = SeekBarDefaults.SeekBarInnerPaddings
                    )
            )
        }
    }
}

object SeekBarDefaults {
    val SeekBarInnerPaddings = 16.dp
    val SeekBarStrokeWidth = 8.dp
    val SeekDefaultHeight = 20.dp
    val SeekBarDefaultTimeFormat = "mm:ss"
}
