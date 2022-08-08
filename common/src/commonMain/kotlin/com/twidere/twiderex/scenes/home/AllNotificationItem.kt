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
import androidx.compose.ui.graphics.painter.Painter
import com.twidere.twiderex.component.TimelineComponent
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.lazy.LazyListController
import com.twidere.twiderex.component.painterResource
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.extensions.rememberPresenterState
import com.twidere.twiderex.model.HomeNavigationItem
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.viewmodel.timeline.SavedStateKeyType
import com.twidere.twiderex.viewmodel.timeline.TimeLineEvent
import com.twidere.twiderex.viewmodel.timeline.TimelinePresenter
import com.twidere.twiderex.viewmodel.timeline.TimelineState

class AllNotificationItem : HomeNavigationItem() {
  @Composable
  override fun name(): String {
    return stringResource(res = com.twidere.twiderex.MR.strings.scene_notification_tabs_all)
  }

  override val route: String
    get() = TODO("Not yet implemented")

  @Composable
  override fun icon(): Painter = painterResource(res = com.twidere.twiderex.MR.files.ic_message_circle)

  @Composable
  override fun Content() {
    AllNotificationSceneContent(
      lazyListController = lazyListController,
    )
  }
}

@Composable
fun AllNotificationScene() {
  TwidereScene {
    InAppNotificationScaffold(
      topBar = {
        AppBar(
          title = {
            Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_notification_tabs_all))
          },
          navigationIcon = {
            AppBarNavigationButton()
          }
        )
      }
    ) {
      AllNotificationSceneContent()
    }
  }
}

@Composable
fun AllNotificationSceneContent(
  lazyListController: LazyListController? = null,
) {
  val (state, channel) = rememberPresenterState<TimelineState, TimeLineEvent> {
    TimelinePresenter(it, savedStateKeyType = SavedStateKeyType.NOTIFICATION)
  }
  TimelineComponent(
    state = state,
    channel = channel,
    lazyListController = lazyListController,
  )
}
