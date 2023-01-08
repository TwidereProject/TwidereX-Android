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
package com.twidere.twiderex.scenes.home

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.graphics.painter.Painter
import com.twidere.twiderex.component.UserComponent
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.painterResource
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.extensions.rememberPresenterState
import com.twidere.twiderex.model.HomeNavigationItem
import com.twidere.twiderex.navigation.Root
import com.twidere.twiderex.navigation.rememberUserNavigationData
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.viewmodel.user.UserEvent
import com.twidere.twiderex.viewmodel.user.UserPresenter
import com.twidere.twiderex.viewmodel.user.UserState
import io.github.seiko.precompose.annotation.NavGraphDestination
import moe.tlaster.precompose.navigation.Navigator

class MeItem : HomeNavigationItem() {

  @Composable
  override fun name(): String = stringResource(com.twidere.twiderex.MR.strings.scene_profile_title)
  override val route: String
    get() = Root.Me

  @Composable
  override fun icon(): Painter = painterResource(res = com.twidere.twiderex.MR.files.ic_user)

  override val withAppBar: Boolean
    get() = false

  @Composable
  override fun Content(navigator: Navigator) {
    MeSceneContent(navigator)
  }
}

@NavGraphDestination(
  route = Root.Me,
)
@Composable
fun MeScene(
  navigator: Navigator,
) {
  TwidereScene {
    InAppNotificationScaffold(
      topBar = {
        AppBar(
          title = {
            Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_profile_title))
          },
          navigationIcon = {
            AppBarNavigationButton(
              onBack = {
                navigator.popBackStack()
              }
            )
          }
        )
      }
    ) {
      MeSceneContent(navigator = navigator)
    }
  }
}

@Composable
fun MeSceneContent(
  navigator: Navigator
) {
  val account = LocalActiveAccount.current
  account?.toUi()?.let { user ->
    val (state, channel) = key(user.userKey) {
      rememberPresenterState<UserState, UserEvent> {
        UserPresenter(it, userKey = user.userKey)
      }
    }
    val userNavigationData = rememberUserNavigationData(navigator)
    UserComponent(
      userKey = user.userKey,
      state = state,
      channel = channel,
      userNavigationData = userNavigationData,
    )
  }
}
