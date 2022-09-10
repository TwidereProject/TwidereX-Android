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
package com.twidere.twiderex.scenes.settings.storage

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.Dialog
import com.twidere.twiderex.component.foundation.DialogProperties
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.extensions.rememberPresenterState
import com.twidere.twiderex.navigation.Root
import com.twidere.twiderex.ui.TwidereScene
import io.github.seiko.precompose.annotation.NavGraphDestination
import moe.tlaster.precompose.navigation.Navigator

@NavGraphDestination(
  route = Root.Settings.Storage,
)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StorageScene(
  navigator: Navigator,
) {
  val (state, channel) = rememberPresenterState { StoragePresenter(it) }
  if (state.loading) {
    Dialog(
      onDismissRequest = { },
      properties = DialogProperties(
        dismissOnBackPress = false,
        dismissOnClickOutside = false,
      )
    ) {
      CircularProgressIndicator()
    }
  }

  TwidereScene {
    InAppNotificationScaffold(
      topBar = {
        AppBar(
          navigationIcon = {
            AppBarNavigationButton(
              popBackStack = {
                navigator.popBackStack()
              }
            )
          },
          title = {
            Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_storage_title))
          }
        )
      }
    ) {
      Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
      ) {
        ListItem(
          modifier = Modifier
            .clickable {
              channel.trySend(StorageEvent.ClearSearchHistory)
            },
        ) {
          Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_storage_search_title))
        }
        ListItem(
          modifier = Modifier
            .clickable {
              channel.trySend(StorageEvent.ClearImageCache)
            },
          text = {
            Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_storage_media_title))
          },
          secondaryText = {
            Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_storage_media_sub_title))
          },
        )
        ListItem(
          modifier = Modifier
            .clickable {
              channel.trySend(StorageEvent.ClearAllCaches)
            },
          text = {
            Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_storage_all_title), color = Color.Red)
          },
          secondaryText = {
            Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_storage_all_sub_title))
          },
        )
      }
    }
  }
}