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
@file:Suppress("NAME_SHADOWING")

package me.saket.swipe

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Stable
internal class SwipeRippleState {
  private var ripple = mutableStateOf<SwipeRipple?>(null)

  fun animate(
    action: SwipeActionMeta,
    scope: CoroutineScope
  ) {
    val drawOnRightSide = action.isOnRightSide
    val action = action.value

    ripple.value = SwipeRipple(
      isUndo = action.isUndo,
      rightSide = drawOnRightSide,
      color = action.background,
      alpha = 0f,
      progress = 0f
    )

    // Reverse animation feels faster (especially for larger swipe distances) so slow it down further.
    val animationDurationMs = (animationDurationMs * (if (action.isUndo) 1.75f else 1f)).roundToInt()

    val progressAsync = scope.async {
      Animatable(initialValue = 0f).animateTo(
        targetValue = 1f,
        animationSpec = tween(durationMillis = animationDurationMs),
        block = {
          ripple.value = ripple.value!!.copy(progress = value)
        }
      )
    }
    val alphaAsync = scope.async {
      Animatable(initialValue = if (action.isUndo) 0f else 0.25f).animateTo(
        targetValue = if (action.isUndo) 0.5f else 0f,
        animationSpec = tween(
          durationMillis = animationDurationMs,
          delayMillis = if (action.isUndo) 0 else animationDurationMs / 2
        ),
        block = {
          ripple.value = ripple.value!!.copy(alpha = value)
        }
      )
    }

    scope.launch {
      progressAsync.await()
      alphaAsync.await()
    }
  }

  fun draw(scope: DrawScope) {
    ripple.value?.run {
      scope.clipRect {
        val size = scope.size
        // Start the ripple with a radius equal to the available height so that it covers the entire edge.
        val startRadius = if (isUndo) size.width + size.height else size.height
        val endRadius = if (!isUndo) size.width + size.height else size.height
        val radius = lerp(startRadius, endRadius, fraction = progress)

        drawCircle(
          color = color,
          radius = radius,
          alpha = alpha,
          center = this.center.copy(x = if (rightSide) this.size.width + this.size.height else 0f - this.size.height)
        )
      }
    }
  }
}

private data class SwipeRipple(
  val isUndo: Boolean,
  val rightSide: Boolean,
  val color: Color,
  val alpha: Float,
  val progress: Float,
)

private fun lerp(start: Float, stop: Float, fraction: Float) =
  (start * (1 - fraction) + stop * fraction)
