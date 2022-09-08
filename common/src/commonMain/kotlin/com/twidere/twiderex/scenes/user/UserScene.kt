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
package com.twidere.twiderex.scenes.user

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.MR
import com.twidere.twiderex.component.UserComponent
import com.twidere.twiderex.component.foundation.AlertDialog
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.DropdownMenu
import com.twidere.twiderex.component.foundation.DropdownMenuItem
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import moe.tlaster.precompose.navigation.Navigator
import com.twidere.twiderex.component.painterResource
import com.twidere.twiderex.component.status.UserName
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.di.ext.getViewModel
import com.twidere.twiderex.extensions.rememberPresenterState
import com.twidere.twiderex.extensions.withElevation
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.navigation.ProvideUserPlatform
import com.twidere.twiderex.navigation.RequirePlatformAccount
import com.twidere.twiderex.navigation.Root
import com.twidere.twiderex.navigation.RootDeepLinks
import com.twidere.twiderex.navigation.rememberUserNavigationData
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.viewmodel.dm.DMNewConversationViewModel
import com.twidere.twiderex.viewmodel.user.UserEvent
import com.twidere.twiderex.viewmodel.user.UserPresenter
import com.twidere.twiderex.viewmodel.user.UserState
import io.github.seiko.precompose.annotation.NavGraphDestination
import io.github.seiko.precompose.annotation.Path

@NavGraphDestination(
  route = Root.User.route,
  deepLink = [RootDeepLinks.User.route]
)

@Composable
fun UserScene(
  @Path("userKey") key: String,
  navigator: Navigator,
) {
  val account = LocalActiveAccount.current ?: return
  MicroBlogKey.valueOf(key).let { userKey ->
    ProvideUserPlatform(userKey = userKey) { platformType ->
      RequirePlatformAccount(platformType = platformType) {
        InnerUserScene(
          userKey = userKey,
          navigator = navigator,
          account = account,
        )
      }
    }
  }
}

@Composable
fun InnerUserScene(
  userKey: MicroBlogKey,
  navigator: Navigator,
  account: AccountDetails,
) {
  val (state, channel) = rememberPresenterState<UserState, UserEvent> {
    UserPresenter(it, userKey = userKey)
  }
  if (state !is UserState.Data) {
    return
  }
  val conversationViewModel: DMNewConversationViewModel = getViewModel()
  var expanded by remember { mutableStateOf(false) }
  var showBlockAlert by remember { mutableStateOf(false) }
  val userNavigationData = rememberUserNavigationData(navigator)
  TwidereScene {
    InAppNotificationScaffold(
      // TODO: Show top bar with actions
      topBar = {
        AppBar(
          backgroundColor = MaterialTheme.colors.surface.withElevation(),
          navigationIcon = {
            AppBarNavigationButton(
              popBackStack = {
                navigator.popBackStack()
              }
            )
          },
          actions = {
            if (account.type == PlatformType.Twitter && state.user?.platformType == PlatformType.Twitter) {
              state.user.let {
                if (userKey != account.accountKey) {
                  IconButton(
                    onClick = {
                      conversationViewModel.createNewConversation(
                        it,
                        onResult = { conversationKey ->
                          conversationKey?.let {
                            navigator.navigate(Root.Messages.Conversation(it))
                          }
                        }
                      )
                    }
                  ) {
                    Icon(
                      painter = painterResource(res = com.twidere.twiderex.MR.files.ic_mail),
                      contentDescription = stringResource(
                        res = com.twidere.twiderex.MR.strings.scene_messages_title
                      ),
                      tint = MaterialTheme.colors.onSurface
                    )
                  }
                }
              }
            }
            Box {
              if (userKey != account.accountKey) {
                IconButton(
                  onClick = {
                    expanded = true
                  }
                ) {
                  Icon(
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = stringResource(
                      res = MR.strings.accessibility_common_more
                    ),
                    tint = MaterialTheme.colors.onSurface
                  )
                }
              }

              DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
              ) {
                state.relationship.takeIf { !state.loadingRelationship }
                  ?.blocking?.let { blocking ->
                    DropdownMenuItem(
                      onClick = {
                        if (blocking)
                          channel.trySend(UserEvent.UnBlock)
                        else
                          showBlockAlert = true
                        expanded = false
                      }
                    ) {
                      Text(
                        text = stringResource(
                          res = if (blocking) MR.strings.common_controls_friendship_actions_unblock
                          else MR.strings.common_controls_friendship_actions_block
                        )
                      )
                    }
                  }
              }
            }
          },
          elevation = 0.dp,
          title = {
            state.user?.let {
              UserName(
                user = it,
                openLink = userNavigationData.statusNavigation.openLink,
              )
            }
          }
        )
      }
    ) {
      Box {
        UserComponent(
          userKey = userKey,
          state = state,
          channel = channel,
          userNavigationData = userNavigationData,
        )
        if (showBlockAlert) {
          state.user?.let {
            BlockAlert(
              screenName = it.getDisplayScreenName(it.userKey.host),
              onDismissRequest = { showBlockAlert = false },
              onConfirm = {
                channel.trySend(UserEvent.Block)
              }
            )
          }
        }
      }
    }
  }
}

@Composable
fun BlockAlert(
  screenName: String,
  onDismissRequest: () -> Unit,
  onConfirm: () -> Unit
) {
  AlertDialog(
    onDismissRequest = {
      onDismissRequest.invoke()
    },
    title = {
      Text(
        text = stringResource(res = MR.strings.common_alerts_block_user_confirm_title, screenName),
        style = MaterialTheme.typography.subtitle1
      )
    },
    dismissButton = {
      TextButton(
        onClick = {
          onDismissRequest.invoke()
        }
      ) {
        Text(text = stringResource(res = MR.strings.common_controls_actions_cancel))
      }
    },
    confirmButton = {
      TextButton(
        onClick = {
          onConfirm()
          onDismissRequest.invoke()
        }
      ) {
        Text(text = stringResource(res = MR.strings.common_controls_actions_yes))
      }
    },
  )
}
