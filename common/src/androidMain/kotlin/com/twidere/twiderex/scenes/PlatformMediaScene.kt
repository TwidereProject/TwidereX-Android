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
package com.twidere.twiderex.scenes

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.scenes.twitter.TwitterWebSignInScene
import moe.tlaster.precompose.navigation.Navigator
import java.net.URLDecoder

@Composable
actual fun PlatformStatusMediaScene(
  statusKey: String,
  selectedIndex: Int?,
  userKey: String?,
  navigator: Navigator,
) {
  StatusMediaScene(
    statusKey = statusKey,
    selectedIndex = selectedIndex ?: 0,
    userKey = userKey,
    navigator = navigator,
  )
}

@Composable
actual fun PlatformRawMediaScene(
  url: String,
  type: String,
  navigator: Navigator,
) {
  RawMediaScene(url = url, type = type, navigator = navigator)
}

@Composable
actual fun PlatformPureMediaScene(
  belongToKey: String,
  selectedIndex: Int?,
  navigator: Navigator
) {
  PureMediaScene(
    belongToKey = belongToKey,
    selectedIndex = selectedIndex ?: 0,
    navigator = navigator,
  )
}

@Composable
actual fun PlatformScene(target: String, navigator: Navigator) {
  TwitterWebSignInScene(
    target = URLDecoder.decode(target, "UTF-8"),
    navigator = navigator
  )
}

@Composable
actual fun StatusMediaSceneLayout(
  backgroundColor: Color,
  contentColor: Color,
  closeButton: @Composable () -> Unit,
  bottomView: @Composable () -> Unit,
  mediaView: @Composable () -> Unit,
  backgroundView: @Composable () -> Unit,
) {
  InAppNotificationScaffold(
    backgroundColor = backgroundColor,
    contentColor = contentColor,
    bottomBar = {
      bottomView.invoke()
    }
  ) {
    Box {
      backgroundView.invoke()
      mediaView.invoke()
      closeButton.invoke()
    }
  }
}
