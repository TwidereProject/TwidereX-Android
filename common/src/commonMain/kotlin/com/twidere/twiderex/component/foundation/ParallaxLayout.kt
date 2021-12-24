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

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.kmp.OrientationSensorManager
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun rememberParallaxLayoutState(maxRotate: Float, maxTransition: Float): ParallaxLayoutState {
    return rememberSaveable(
        saver = ParallaxLayoutState.Saver(),
    ) {
        ParallaxLayoutState(maxRotate, maxTransition)
    }
}

@Stable
class ParallaxLayoutState(
    val maxRotate: Float,
    val maxTransition: Float,
    rotateHorizontal: Float = 0f,
    rotateVertical: Float = 0f,
    transitionHorizontal: Float = 0f,
    transitionVertical: Float = 0f,
    lastHorizontalProgress: Int = 0,
    lastVerticalProgress: Int = 0
) {
    companion object {
        fun Saver(): Saver<ParallaxLayoutState, *> = listSaver(
            save = {
                listOf(
                    it.maxRotate,
                    it.maxTransition,
                    it.rotateHorizontal,
                    it.rotateVertical,
                    it.transitionHorizontal,
                    it.transitionVertical,
                    it.lastHorizontalProgress,
                    it.lastVerticalProgress
                )
            },
            restore = {
                ParallaxLayoutState(
                    maxRotate = it[0].toFloat(),
                    maxTransition = it[1].toFloat(),
                    rotateHorizontal = it[2].toFloat(),
                    rotateVertical = it[3].toFloat(),
                    transitionHorizontal = it[4].toFloat(),
                    transitionVertical = it[5].toFloat(),
                    lastHorizontalProgress = it[6].toInt(),
                    lastVerticalProgress = it[7].toInt(),
                )
            }
        )
    }
    private val minRotate = 0f
    private val minTransition = 0f
    private var _rotateHorizontal by mutableStateOf(rotateHorizontal.coerceIn(minRotate, maxRotate))
    var rotateHorizontal: Float
        get() = _rotateHorizontal
        set(value) {
            _rotateHorizontal = value.coerceIn(minRotate, maxRotate)
        }

    private var _rotateVertical by mutableStateOf(rotateVertical.coerceIn(minRotate, maxRotate))
    var rotateVertical: Float
        get() = _rotateVertical
        set(value) {
            _rotateVertical = value.coerceIn(minRotate, maxRotate)
        }

    private var _transitionHorizontal by mutableStateOf(transitionHorizontal.coerceIn(minTransition, maxTransition))
    var transitionHorizontal: Float
        get() = _transitionHorizontal
        set(value) {
            _transitionHorizontal = value.coerceIn(minTransition, maxTransition)
        }

    private var _transitionVertical by mutableStateOf(transitionVertical.coerceIn(minTransition, maxTransition))
    var transitionVertical: Float
        get() = _transitionVertical
        set(value) {
            _transitionVertical = value.coerceIn(minTransition, maxTransition)
        }

    private var _lastVerticalProgress by mutableStateOf(lastVerticalProgress)
    var lastVerticalProgress: Int
        get() = _lastVerticalProgress
        set(value) {
            _lastVerticalProgress = value
        }

    private var _lastHorizontalProgress by mutableStateOf(lastHorizontalProgress)
    var lastHorizontalProgress: Int
        get() = _lastHorizontalProgress
        set(value) {
            _lastHorizontalProgress = value
        }
}

@Composable
fun ParallaxLayout(
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    parallaxLayoutState: ParallaxLayoutState,
    backContentOffsetX: Dp = 0.dp,
    backContentOffsetY: Dp = 0.dp,
    animateDuration: Int = 300,
    backContent: @Composable () -> Unit,
    frontContent: @Composable () -> Unit,
) {

    val animateSpec = remember {
        tween<Float>(durationMillis = animateDuration, easing = LinearEasing)
    }

    val rotateAnimateHorizontal = animateFloatAsState(targetValue = parallaxLayoutState.rotateHorizontal, animationSpec = animateSpec)
    val rotateAnimateVertical = animateFloatAsState(targetValue = parallaxLayoutState.rotateVertical, animationSpec = animateSpec)
    val transitionAnimateHorizontal = animateFloatAsState(targetValue = parallaxLayoutState.transitionHorizontal, animationSpec = animateSpec)
    val transitionAnimateVertical = animateFloatAsState(targetValue = parallaxLayoutState.transitionVertical, animationSpec = animateSpec)

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

    val orientationSensorManager = get<OrientationSensorManager>()
    DisposableEffect(Unit) {
        orientationSensorManager.onOrientationChangedListener = { originValues, currentValues ->
            // calculate vertical rotation progress, make it round to int
            val currentVerticalProgress = calculateRotateProgress(
                originValue = originValues[1],
                currentValue = currentValues[1]
            )
            // use 1% to filter the subtle jitter of orientation sensor
            if (abs(currentVerticalProgress - parallaxLayoutState.lastVerticalProgress) >= 1) {
                parallaxLayoutState.lastVerticalProgress = currentVerticalProgress
                // calculate rotations and transitions and update
                parallaxLayoutState.rotateVertical = currentVerticalProgress * parallaxLayoutState.maxRotate / 100
                parallaxLayoutState.transitionVertical = currentVerticalProgress * parallaxLayoutState.maxTransition / 100
            }
            // calculate horizontal rotation progress, make it round to int
            val currentHorizontalProgress = calculateRotateProgress(
                originValue = originValues[2],
                currentValue = currentValues[2]
            )
            // use 1% to filter the subtle jitter of orientation sensor
            if (abs(currentHorizontalProgress - parallaxLayoutState.lastHorizontalProgress) >= 1) {
                parallaxLayoutState.lastHorizontalProgress = currentHorizontalProgress
                // calculate rotations and transitions and update
                parallaxLayoutState.rotateHorizontal = currentHorizontalProgress * parallaxLayoutState.maxRotate / 100
                // when the phone is face up and bottom face to user
                // and if the values[2] is positive means left is up
                // in this case we want to move backContent to left
                // so we add a "-" in front
                parallaxLayoutState.transitionHorizontal = -currentHorizontalProgress * parallaxLayoutState.maxTransition / 100
            }
        }
        onDispose {
            orientationSensorManager.release()
        }
    }

    // DisposableEffect(key1 = true) {
    //     val orientationSensorManager =
    //         OrientationSensorManager(context) { originValues, currentValues ->
    //
    //             // calculate vertical rotation progress, make it round to int
    //             val currentVerticalProgress = calculateRotateProgress(
    //                 originValue = originValues[1],
    //                 currentValue = currentValues[1]
    //             )
    //
    //             // use 1% to filter the subtle jitter of orientation sensor
    //             if (abs(currentVerticalProgress - parallaxLayoutState.lastVerticalProgress) >= 1) {
    //                 parallaxLayoutState.lastVerticalProgress = currentVerticalProgress
    //                 // calculate rotations and transitions and update
    //                 parallaxLayoutState.rotateVertical = currentVerticalProgress * parallaxLayoutState.maxRotate / 100
    //                 parallaxLayoutState.transitionVertical = currentVerticalProgress * parallaxLayoutState.maxTransition / 100
    //             }
    //
    //             // calculate horizontal rotation progress, make it round to int
    //             val currentHorizontalProgress = calculateRotateProgress(
    //                 originValue = originValues[2],
    //                 currentValue = currentValues[2]
    //             )
    //             // use 1% to filter the subtle jitter of orientation sensor
    //             if (abs(currentHorizontalProgress - parallaxLayoutState.lastHorizontalProgress) >= 1) {
    //                 parallaxLayoutState.lastHorizontalProgress = currentHorizontalProgress
    //                 // calculate rotations and transitions and update
    //                 parallaxLayoutState.rotateHorizontal = currentHorizontalProgress * parallaxLayoutState.maxRotate / 100
    //                 // when the phone is face up and bottom face to user
    //                 // and if the values[2] is positive means left is up
    //                 // in this case we want to move backContent to left
    //                 // so we add a "-" in front
    //                 parallaxLayoutState.transitionHorizontal = -currentHorizontalProgress * parallaxLayoutState.maxTransition / 100
    //             }
    //         }
    //     onDispose {
    //         orientationSensorManager.release()
    //     }
    // }
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
