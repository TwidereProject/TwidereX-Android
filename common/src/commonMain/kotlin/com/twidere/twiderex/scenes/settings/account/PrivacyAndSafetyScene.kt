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
package com.twidere.twiderex.scenes.settings.account

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.twidere.twiderex.MR
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.lazy.ItemHeader
import com.twidere.twiderex.component.settings.switchItem
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.extensions.rememberPresenterState
import com.twidere.twiderex.navigation.Root
import com.twidere.twiderex.ui.TwidereScene
import io.github.seiko.precompose.annotation.NavGraphDestination
import moe.tlaster.precompose.navigation.Navigator

@NavGraphDestination(
  route = Root.Settings.PrivacyAndSafety,
)
@Composable
fun PrivacyAndSafetyScene(
  navigator: Navigator,
) {
  TwidereScene {
    InAppNotificationScaffold(
      topBar = {
        AppBar(
          title = {
            Text(text = stringResource(res = MR.strings.scene_settings_privacy_and_safety_title))
          },
          navigationIcon = {
            AppBarNavigationButton {
              navigator.popBackStack()
            }
          },
        )
      },
    ) {
      val (state, channel) = rememberPresenterState { PrivacyAndSafetyPresenter(it) }
      Column {
        ItemHeader {
          Text(text = stringResource(res = MR.strings.scene_settings_privacy_and_safety_section_header_sensitive))
        }
        switchItem(
          value = state.account.isAlwaysShowSensitiveMedia,
          onChanged = {
            channel.trySend(PrivacyAndSafetyEvent.SetIsAlwaysShowSensitiveMedia(it))
          },
          title = {
            Text(text = stringResource(res = MR.strings.scene_settings_privacy_and_safety_always_show_sensitive_media))
          },
        )
      }
    }
  }
}
