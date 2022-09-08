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
package com.twidere.twiderex.scenes.lists

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.twidere.twiderex.component.UserListComponent
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import moe.tlaster.precompose.navigation.Navigator
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.extensions.rememberPresenterState
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.navigation.Root
import com.twidere.twiderex.navigation.rememberUserNavigationData
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.viewmodel.user.UserListEvent
import com.twidere.twiderex.viewmodel.user.UserListPresenter
import com.twidere.twiderex.viewmodel.user.UserListState
import com.twidere.twiderex.viewmodel.user.UserListType
import io.github.seiko.precompose.annotation.NavGraphDestination
import io.github.seiko.precompose.annotation.Path

@NavGraphDestination(
  route = Root.Lists.Subscribers.route,
)
@Composable
fun ListsSubscribersScene(
  @Path("listKey") listKey: String,
  navigator: Navigator,
) {
  ListsSubscribersScene(
    listKey = MicroBlogKey.valueOf(listKey),
    navigator = navigator,
  )
}

@Composable
fun ListsSubscribersScene(
  listKey: MicroBlogKey,
  navigator: Navigator,
) {
  val (state) = rememberPresenterState<UserListState, UserListEvent> {
    UserListPresenter(
      it,
      userType = UserListType.Followers(
        listKey
      )
    )
  }
  val userNavigationData = rememberUserNavigationData(navigator)
  (state as? UserListState.Data)?.let { data ->
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
              Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_lists_details_tabs_subscriber))
            }
          )
        },
      ) {
        UserListComponent(
          source = data.source,
          userNavigationData = userNavigationData,
        )
      }
    }
  }
}
