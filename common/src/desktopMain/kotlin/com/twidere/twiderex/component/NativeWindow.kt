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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.rememberWindowState
import com.mayakapps.compose.windowstyler.WindowBackdrop
import com.mayakapps.compose.windowstyler.WindowStyle
import com.twidere.twiderex.kmp.LocalPlatformWindow
import com.twidere.twiderex.kmp.PlatformWindow
import com.twidere.twiderex.utils.OperatingSystem
import com.twidere.twiderex.utils.currentOperatingSystem
import moe.tlaster.precompose.PreComposeWindow

@Stable
class NativeWindowController(
  initialIsAppearanceLightTitleBar: Boolean = false,
) {
  companion object {
    fun Saver(): Saver<NativeWindowController, *> = listSaver(
      save = {
        listOf(it.isAppearanceLightTitleBar)
      },
      restore = {
        NativeWindowController(
          initialIsAppearanceLightTitleBar = it[0],
        )
      }
    )
  }

  var isAppearanceLightTitleBar: Boolean by mutableStateOf(initialIsAppearanceLightTitleBar)
}

@Composable
fun rememberNativeWindowController(): NativeWindowController {
  val saver = remember {
    NativeWindowController.Saver()
  }
  return rememberSaveable(
    saver = saver
  ) {
    NativeWindowController()
  }
}

val LocalNativeWindowController =
  staticCompositionLocalOf<NativeWindowController> { error("No NativeWindowController") }

@Composable
fun NativeWindow(
  onCloseRequest: () -> Unit,
  state: WindowState = rememberWindowState(),
  visible: Boolean = true,
  title: String = "Untitled",
  icon: Painter? = null,
  resizable: Boolean = true,
  enabled: Boolean = true,
  focusable: Boolean = true,
  alwaysOnTop: Boolean = false,
  onPreviewKeyEvent: (KeyEvent) -> Boolean = { false },
  onKeyEvent: (KeyEvent) -> Boolean = { false },
  content: @Composable FrameWindowScope.() -> Unit,
) {
  val nativeWindowController = rememberNativeWindowController()
  CompositionLocalProvider(
    LocalNativeWindowController provides nativeWindowController,
    LocalPlatformWindow provides PlatformWindow(),
  ) {
    PreComposeWindow(
      state = state,
      visible = visible,
      title = title,
      icon = icon,
      undecorated = false,
      transparent = false,
      resizable = resizable,
      enabled = enabled,
      focusable = focusable,
      alwaysOnTop = alwaysOnTop,
      onPreviewKeyEvent = onPreviewKeyEvent,
      onKeyEvent = onKeyEvent,
      onCloseRequest = onCloseRequest,
      content = {
        if (currentOperatingSystem == OperatingSystem.MacOS) {
          LaunchedEffect(Unit) {
            window.rootPane.apply {
              putClientProperty("apple.awt.fullWindowContent", true)
              putClientProperty("apple.awt.transparentTitleBar", true)
            }
          }
        } else if (currentOperatingSystem == OperatingSystem.Windows) {
          WindowStyle(
            isDarkTheme = !nativeWindowController.isAppearanceLightTitleBar,
            backdropType = WindowBackdrop.Mica,
          )
        }
        content.invoke(this)
      },
    )
  }
}
