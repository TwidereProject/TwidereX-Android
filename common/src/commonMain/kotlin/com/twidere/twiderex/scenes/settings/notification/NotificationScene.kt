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
package com.twidere.twiderex.scenes.settings.notification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.ColoredSwitch
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.lazy.ItemHeader
import com.twidere.twiderex.component.navigation.openLink
import com.twidere.twiderex.component.navigation.user
import moe.tlaster.precompose.navigation.Navigator
import com.twidere.twiderex.component.status.UserAvatar
import com.twidere.twiderex.component.status.UserName
import com.twidere.twiderex.component.status.UserScreenName
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.extensions.observeAsState
import com.twidere.twiderex.extensions.rememberPresenterState
import com.twidere.twiderex.navigation.Root
import com.twidere.twiderex.ui.LocalActiveAccountViewModel
import com.twidere.twiderex.ui.TwidereScene
import io.github.seiko.precompose.annotation.NavGraphDestination

@NavGraphDestination(
  route = Root.Settings.Notification,
)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NotificationScene(
  navigator: Navigator,
) {
  val activeAccountViewModel = LocalActiveAccountViewModel.current
  val accounts by activeAccountViewModel.allAccounts.observeAsState(initial = emptyList())
  val (state, channel) = rememberPresenterState { NotificationPresenter(it) }
  TwidereScene {
    InAppNotificationScaffold(
      topBar = {
        AppBar(
          title = {
            Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_notification_title))
          },
          navigationIcon = {
            AppBarNavigationButton()
          }
        )
      }
    ) {
      Column {
        Surface(
          color = MaterialTheme.colors.primary,
        ) {
          ListItem(
            modifier = Modifier.clickable {
              channel.trySend(NotificationEvent.SetEnabled(!state.enabled))
            },
            text = {
              Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_notification_notification_switch))
            },
            trailing = {
              ColoredSwitch(
                checked = state.enabled,
                onCheckedChange = {
                  channel.trySend(NotificationEvent.SetEnabled(it))
                },
                colors = SwitchDefaults.colors(
                  checkedThumbColor = MaterialTheme.colors.onPrimary,
                )
              )
            }
          )
        }
        ItemHeader {
          Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_notification_accounts))
        }
        LazyColumn {
          items(accounts) {
            val user = remember {
              it.toUi()
            }
            ListItem(
              modifier = Modifier.clickable(
                onClick = {
                  navigator.navigate(Root.Settings.AccountNotification(it.accountKey))
                },
                enabled = state.enabled,
              ),
              icon = {
                CompositionLocalProvider(
                  *if (!state.enabled) {
                    arrayOf(LocalContentAlpha provides ContentAlpha.disabled)
                  } else {
                    emptyArray()
                  }
                ) {
                  UserAvatar(
                    user = user,
                    withPlatformIcon = true,
                    toUser = {
                      navigator.user(it)
                    }
                  )
                }
              },
              text = {
                CompositionLocalProvider(
                  *if (!state.enabled) {
                    arrayOf(LocalContentAlpha provides ContentAlpha.disabled)
                  } else {
                    emptyArray()
                  }
                ) {
                  UserName(
                    user = user,
                    openLink = {
                      navigator.openLink(it)
                    }
                  )
                }
              },
              secondaryText = {
                CompositionLocalProvider(
                  *if (!state.enabled) {
                    arrayOf(LocalContentAlpha provides ContentAlpha.disabled)
                  } else {
                    emptyArray()
                  }
                ) {
                  UserScreenName(user = user)
                }
              },
            )
          }
        }
      }
    }
  }
}
