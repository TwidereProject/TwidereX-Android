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

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.utils.OrientationSensorManager
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun ParallaxLayout(
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    rotation: Float = 10f,
    backContentTransition: Float = 10f,
    backContentOffsetX: Dp = 0.dp,
    backContentOffsetY: Dp = 0.dp,
    animateDuration: Int = 300,
    backContent: @Composable () -> Unit,
    frontContent: @Composable () -> Unit,
) {
    var rotateHorizontal by remember { mutableStateOf(0f) }
    var rotateVertical by remember { mutableStateOf(0f) }
    var transitionHorizontal by remember { mutableStateOf(0f) }
    var transitionVertical by remember { mutableStateOf(0f) }

    val animateSpec = remember {
        tween<Float>(durationMillis = animateDuration, easing = LinearEasing)
    }

    val rotateAnimateHorizontal = animateFloatAsState(targetValue = rotateHorizontal, animationSpec = animateSpec)
    val rotateAnimateVertical = animateFloatAsState(targetValue = rotateVertical, animationSpec = animateSpec)
    val transitionAnimateHorizontal = animateFloatAsState(targetValue = transitionHorizontal, animationSpec = animateSpec)
    val transitionAnimateVertical = animateFloatAsState(targetValue = transitionVertical, animationSpec = animateSpec)
    val context = LocalContext.current

    var lastVerticalProgress = remember { 0 }
    var lastHorizontalProgress = remember { 0 }

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .align(alignment)
                .wrapContentSize()
                .offset(
                    x = transitionAnimateHorizontal.value.dp + backContentOffsetX,
                    y = transitionAnimateVertical.value.dp + backContentOffsetY
                )
                .graphicsLayer(
                    rotationY = rotateAnimateHorizontal.value,
                    rotationX = rotateAnimateVertical.value
                )
        ) {
            backContent()
        }
        Box(
            modifier = Modifier
                .align(alignment)
                .wrapContentSize()
                .graphicsLayer(
                    rotationY = rotateAnimateHorizontal.value,
                    rotationX = rotateAnimateVertical.value
                )
        ) {
            frontContent()
        }
    }

    DisposableEffect(key1 = true) {
        val orientationSensorManager =
            OrientationSensorManager(context) { originValues, currentValues ->

                // calculate vertical rotation progress, make it round to int
                val currentVerticalProgress = calculateRotateProgress(
                    originValue = originValues[1],
                    currentValue = currentValues[1]
                )

                // use 1% to filter the subtle jitter of orientation sensor
                if (abs(currentVerticalProgress - lastVerticalProgress) >= 1) {
                    lastVerticalProgress = currentVerticalProgress
                    // calculate rotations and transitions and update
                    rotateVertical = currentVerticalProgress * rotation / 100
                    transitionVertical = currentVerticalProgress * backContentTransition / 100
                }

                // calculate horizontal rotation progress, make it round to int
                val currentHorizontalProgress = calculateRotateProgress(
                    originValue = originValues[2],
                    currentValue = currentValues[2]
                )
                // use 1% to filter the subtle jitter of orientation sensor
                if (abs(abs(currentHorizontalProgress) - abs(lastHorizontalProgress)) >= 1) {
                    lastHorizontalProgress = currentHorizontalProgress
                    // calculate rotations and transitions and update
                    rotateHorizontal = currentHorizontalProgress * rotation / 100
                    // when the phone is face up and bottom face to user
                    // and if the values[2] is positive means left is up
                    // in this case we want to move backContent to left
                    // so we add a "-" in front
                    transitionHorizontal = -currentHorizontalProgress * backContentTransition / 100
                }
            }
        onDispose {
            orientationSensorManager.release()
        }
    }
}

private fun calculateRotateProgress(originValue: Float, currentValue: Float, maxDegrees: Float = 90f): Int {
    return if (currentValue - originValue >= 0) (
        min(
            maxDegrees,
            currentValue - originValue
        ) / maxDegrees * 100
        ).roundToInt()
    else (
        max(
            -maxDegrees,
            currentValue - originValue
        ) / maxDegrees * 100
        ).roundToInt()
}
