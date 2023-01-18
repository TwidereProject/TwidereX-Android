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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.twidere.twiderex.utils.OperatingSystem
import com.twidere.twiderex.utils.currentOperatingSystem

private val titleBarHeight = when (currentOperatingSystem) {
  OperatingSystem.MacOS -> 24.dp
  else -> 0.dp
}
actual fun Modifier.topInsetsPadding(): Modifier = this.padding(top = titleBarHeight)
actual fun Modifier.bottomInsetsPadding(): Modifier = this
actual fun Modifier.startInsetsPadding(): Modifier = this
actual fun Modifier.endInsetsPadding(): Modifier = this

actual fun Modifier.topInsetsHeight(): Modifier = this.height(titleBarHeight)
actual fun Modifier.bottomInsetsHeight(): Modifier = this
actual fun Modifier.startInsetsWidth(): Modifier = this
actual fun Modifier.endInsetsWidth(): Modifier = this

@Composable
actual fun PlatformInsets(
  control: NativeInsetsControl,
  color: NativeInsetsColor,
  content: @Composable () -> Unit,
) {
  val nativeWindowController = LocalNativeWindowController.current
  val darkTheme = control.darkTheme
  LaunchedEffect(darkTheme) {
    nativeWindowController.isAppearanceLightTitleBar = !control.darkTheme
  }
  Box {
    Box(
      modifier = Modifier
        .padding(
          top = if (control.extendToTop) {
            0.dp
          } else {
            titleBarHeight
          }
        )
        .align(Alignment.Center)
    ) {
      content()
    }
    Spacer(
      modifier = if (!control.extendToTop) {
        Modifier
          .height(titleBarHeight)
          .zIndex(999F)
          .fillMaxWidth()
          .background(color.top)
      } else {
        Modifier
      }.align(Alignment.TopCenter)
    )
  }
}

@Composable
actual fun ImeVisibleWithInsets(
  filter: ((Boolean) -> Boolean)?,
  collectIme: ((Boolean) -> Unit)?
) {
}

@Composable
actual fun ImeHeightWithInsets(
  filter: ((Int) -> Boolean)?,
  collectIme: ((Int) -> Unit)?
) {
}

@Composable
actual fun imeBottomInsets(): Dp {
  return 0.dp
}
