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

import androidx.annotation.FloatRange
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Create a [ZoomableState] that is remembered across compositions.
 *
 * @param minScale The minimum [ZoomableState.scale] value.
 * @param maxScale The maximum [ZoomableState.scale] value.
 * @param doubleTapScale The [ZoomableState.scale] Value to animate to when a double tap happens.
 * @param initialScale The initial value for [ZoomableState.scale].
 * @param initialTranslationX The initial value for [ZoomableState.translationX].
 * @param initialTranslationY The initial value for [ZoomableState.translationY].
 */
@Composable
fun rememberZoomableState(
    @FloatRange(from = 0.0) minScale: Float = ZoomableDefaults.MinScale,
    @FloatRange(from = 0.0) maxScale: Float = ZoomableDefaults.MaxScale,
    @FloatRange(from = 0.0) doubleTapScale: Float = ZoomableDefaults.DoubleTapScale,
    @FloatRange(from = 0.0) initialScale: Float = minScale,
    @FloatRange(from = 0.0) initialTranslationX: Float = 0f,
    @FloatRange(from = 0.0) initialTranslationY: Float = 0f
): ZoomableState {
    return rememberSaveable(saver = ZoomableState.Saver) {
        ZoomableState(initialScale, initialTranslationX, initialTranslationY)
    }.apply {
        this.minScale = minScale
        this.maxScale = maxScale
        this.doubleTapScale = doubleTapScale
    }
}

/**
 * A state object that can be hoisted to observe scale and translate for [Zoomable].
 *
 * @param initialScale The initial value for [scale].
 * @param initialTranslationX The initial value for [translationX].
 * @param initialTranslationY The initial value for [translationY].
 * @see rememberZoomableState
 */
@Stable
class ZoomableState(
    @FloatRange(from = 0.0) initialScale: Float = ZoomableDefaults.MinScale,
    @FloatRange(from = 0.0) initialTranslationX: Float = 0f,
    @FloatRange(from = 0.0) initialTranslationY: Float = 0f
) {
    /**
     * The minimum [scale] value.
     */
    @FloatRange(from = 0.0)
    var minScale: Float = ZoomableDefaults.MinScale
        set(value) {
            if (field != value) {
                field = value
                scale = scale // Make sure scale is in range
            }
        }

    /**
     * The maximum [scale] value.
     */
    @FloatRange(from = 0.0)
    var maxScale: Float = ZoomableDefaults.MaxScale
        set(value) {
            if (field != value) {
                field = value
                scale = scale // Make sure scale is in range
            }
        }

    /**
     * The [scale] value to animate to when a double tap happens.
     */
    @FloatRange(from = 0.0)
    var doubleTapScale: Float = ZoomableDefaults.DoubleTapScale

    private val velocityTracker = VelocityTracker()
    private var _scale by mutableStateOf(initialScale)
    private var _translationX = Animatable(initialTranslationX)
    private var _translationY = Animatable(initialTranslationY)
    private var _childSize by mutableStateOf(Size.Zero)

    internal var boundOffset by mutableStateOf(IntOffset.Zero)
        private set

    internal var dismissDragAbsoluteOffsetY by mutableStateOf(0f)
        private set

    internal val dismissDragOffsetY: Float
        get() {
            val maxOffset = childSize.height
            return if (maxOffset == 0f) 0f else {
                val progress = (dismissDragAbsoluteOffsetY / maxOffset).coerceIn(-1f, 1f)
                maxOffset / DismissDragResistanceFactor * sin(progress * PI.toFloat() / 2)
            }
        }

    internal val shouldDismiss: Boolean
        get() = abs(dismissDragAbsoluteOffsetY) > size.height * DismissDragThreshold

    internal var size = IntSize.Zero
        set(value) {
            if (field != value) {
                field = value
                updateBounds()
            }
        }

    internal var childSize: Size
        get() = _childSize
        set(value) {
            if (_childSize != value) {
                _childSize = value
                updateBounds()
            }
        }

    /**
     * Current scale of [Zoomable].
     */
    @get:FloatRange(from = 0.0)
    var scale: Float
        get() = _scale
        internal set(value) {
            _scale = value.coerceIn(minimumValue = minScale, maximumValue = maxScale)
            updateBounds()
        }

    /**
     * Current translationX of [Zoomable].
     */
    @get:FloatRange(from = 0.0)
    val translationX: Float
        get() = _translationX.value

    /**
     * Current translationY of [Zoomable].
     */
    @get:FloatRange(from = 0.0)
    val translationY: Float
        get() = _translationY.value

    val isZooming: Boolean
        get() = scale > minScale && scale <= maxScale

    private fun updateBounds() {
        val offsetX = childSize.width * scale - size.width
        val offsetY = childSize.height * scale - size.height
        boundOffset = IntOffset((offsetX / 2f).roundToInt(), (offsetY / 2f).roundToInt())
        val maxX = offsetX.coerceAtLeast(0f) / 2f
        val maxY = offsetY.coerceAtLeast(0f) / 2f
        _translationX.updateBounds(-maxX, maxX)
        _translationY.updateBounds(-maxY, maxY)
    }

    /**
     * Animate [scale] to [targetScale].
     *
     * @param targetScale The [scale] value to animate to.
     * @param targetTranslation The [translationX] and [translationY] value to animate to. Use the
     * default value to maintain current center point. Use [Offset.Unspecified] to leave
     * translation unchanged.
     * @param animationSpec [AnimationSpec] to be used for this scaling.
     */
    suspend fun animateScaleTo(
        targetScale: Float,
        targetTranslation: Offset = Offset(translationX, translationY) / scale * targetScale,
        animationSpec: AnimationSpec<Float> = spring()
    ) = coroutineScope {
        val initialTranslation = Offset(translationX, translationY)
        val initialScale = scale
        val range = targetScale - initialScale
        animate(
            initialValue = initialScale,
            targetValue = targetScale,
            animationSpec = animationSpec
        ) { value, _ ->
            launch {
                // Update scale here to ensure scale and translation values are updated
                // in the same snapshot
                scale = value
                if (targetTranslation != Offset.Unspecified) {
                    val fraction = if (range == 0f) 1f else (value - initialScale) / range
                    val translation = lerp(initialTranslation, targetTranslation, fraction)
                    _translationX.snapTo(translation.x)
                    _translationY.snapTo(translation.y)
                }
            }
        }
    }

    /**
     * Animate [translationX] and [translationY] to [targetTranslation].
     *
     * @param targetTranslation The [translationX] and [translationY] value to animate to.
     * @param animationSpec [AnimationSpec] to be used for this scaling.
     */
    suspend fun animateTranslateTo(
        targetTranslation: Offset,
        animationSpec: AnimationSpec<Offset> = spring()
    ) = coroutineScope {
        animate(
            typeConverter = Offset.VectorConverter,
            initialValue = Offset(translationX, translationY),
            targetValue = targetTranslation,
            animationSpec = animationSpec
        ) { value, _ ->
            launch {
                _translationX.snapTo(value.x)
                _translationY.snapTo(value.y)
            }
        }
    }

    private suspend fun fling(velocity: Offset) = coroutineScope {
        val spec = exponentialDecay<Float>()
        launch { _translationX.animateDecay(initialVelocity = velocity.x, animationSpec = spec) }
        launch { _translationY.animateDecay(initialVelocity = velocity.y, animationSpec = spec) }
    }

    internal suspend fun onDrag(dragAmount: Offset) {
        _translationX.snapTo(_translationX.value + dragAmount.x)
        _translationY.snapTo(_translationY.value + dragAmount.y)
    }

    internal suspend fun onDragEnd() {
        val velocity = velocityTracker.calculateVelocity()
        fling(Offset(velocity.x, velocity.y))
    }

    internal suspend fun onPress() {
        _translationX.stop()
        _translationY.stop()
    }

    internal fun onZoomChange(zoomChange: Float) {
        scale *= zoomChange
    }

    internal fun addPosition(timeMillis: Long, position: Offset) {
        velocityTracker.addPosition(timeMillis = timeMillis, position = position)
    }

    internal fun resetTracking() {
        velocityTracker.resetTracking()
    }

    internal fun onDismissDrag(dragAmountY: Float) {
        dismissDragAbsoluteOffsetY += dragAmountY
    }

    internal suspend fun onDismissDragEnd() {
        animate(
            initialValue = dismissDragAbsoluteOffsetY,
            targetValue = 0f
        ) { value, _ ->
            dismissDragAbsoluteOffsetY = value
        }
    }

    override fun toString(): String =
        "ZoomableState(translateX=%.1f,translateY=%.1f,scale=%.2f)".format(
            translationX, translationY, scale
        )

    companion object {
        /**
         * The default [Saver] implementation for [ZoomableState].
         */
        val Saver: Saver<ZoomableState, *> = listSaver(
            save = {
                listOf(
                    it.translationX,
                    it.translationY,
                    it.scale
                )
            },
            restore = {
                ZoomableState(
                    initialTranslationX = it[0],
                    initialTranslationY = it[1],
                    initialScale = it[2]
                )
            }
        )
    }
}

internal const val DismissDragResistanceFactor = 2f
internal const val DismissDragThreshold = 0.25f

object ZoomableDefaults {
    /**
     * The default value for [ZoomableState.minScale].
     */
    const val MinScale = 1f

    /**
     * The default value for [ZoomableState.maxScale].
     */
    const val MaxScale = 4f

    /**
     * The default value for [ZoomableState.doubleTapScale].
     */
    const val DoubleTapScale = 2f
}
