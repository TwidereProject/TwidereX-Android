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

@Composable
@OptIn(ExperimentalAnimationApi::class)
fun SizeChangeContent(
  modifier: Modifier = Modifier,
  expanded: Boolean,
  startContent: @Composable () -> Unit,
  endContent: @Composable () -> Unit,
) {

  AnimatedContent(
    modifier = modifier,
    targetState = expanded,
    transitionSpec = {
      fadeIn(animationSpec = tween(150, 150)) with
        fadeOut(animationSpec = tween(150)) using
        SizeTransform { initialSize, targetSize ->
          if (targetState) {
            keyframes {
              // Expand horizontally first.
              IntSize(targetSize.width, initialSize.height) at 150
              durationMillis = 300
            }
          } else {
            keyframes {
              // Shrink vertically first.
              IntSize(initialSize.width, targetSize.height) at 150
              durationMillis = 300
            }
          }
        }
    }
  ) { targetExpanded ->
    if (targetExpanded) {
      endContent()
    } else {
      startContent()
    }
  }
}
