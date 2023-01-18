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
package com.twidere.twiderex.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun <T> DoubleLiftContent(
  state: T,
  modifier: Modifier = Modifier,
  duration: Int = 300,
  content: @Composable (T) -> Unit,
) {
  AnimatedContent(
    modifier = modifier,
    targetState = state,
    transitionSpec = {
      fadeIn(animationSpec = tween(duration / 2, duration / 2)) with
        fadeOut(animationSpec = tween(duration / 2)) using
        SizeTransform { initialSize, targetSize ->
          keyframes {
            if (targetSize.width > initialSize.width) {
              IntSize(targetSize.width, initialSize.height)
            } else {
              IntSize(initialSize.width, targetSize.height)
            } at duration / 2
            durationMillis = duration
          }
        }
    }
  ) { targetExpanded ->
    content.invoke(targetExpanded)
  }
}
