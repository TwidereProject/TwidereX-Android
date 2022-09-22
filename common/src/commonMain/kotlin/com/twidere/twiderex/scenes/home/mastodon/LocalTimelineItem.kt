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
package com.twidere.twiderex.scenes.home.mastodon

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.twidere.services.mastodon.MastodonService
import com.twidere.twiderex.component.TimelineComponent
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.lazy.LazyListController
import com.twidere.twiderex.component.painterResource
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.model.HomeNavigationItem
import com.twidere.twiderex.navigation.Root
import com.twidere.twiderex.navigation.rememberStatusNavigationData
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.viewmodel.timeline.SavedStateKeyType
import io.github.seiko.precompose.annotation.NavGraphDestination
import moe.tlaster.precompose.navigation.Navigator

class LocalTimelineItem : HomeNavigationItem() {
  @Composable
  override fun name(): String {
    return stringResource(res = com.twidere.twiderex.MR.strings.scene_local_title)
  }

  override val route: String
    get() = Root.Mastodon.LocalTimeline

  @Composable
  override fun icon(): Painter {
    return painterResource(res = com.twidere.twiderex.MR.files.ic_users)
  }

  @Composable
  override fun Content(navigator: Navigator) {
    LocalTimelineContent(
      lazyListController = lazyListController,
      navigator = navigator,
    )
  }
}

@NavGraphDestination(
  route = Root.Mastodon.LocalTimeline,
)
@Composable
fun LocalTimelineScene(
  navigator: Navigator,
) {
  TwidereScene {
    InAppNotificationScaffold(
      topBar = {
        AppBar(
          navigationIcon = {
            AppBarNavigationButton(
              onBack = {
                navigator.popBackStack()
              }
            )
          },
          title = {
            Text(text = "Local")
          }
        )
      }
    ) {
      LocalTimelineContent(
        navigator = navigator,
      )
    }
  }
}

@Composable
fun LocalTimelineContent(
  lazyListController: LazyListController? = null,
  navigator: Navigator,
) {
  val account = LocalActiveAccount.current ?: return
  if (account.service !is MastodonService) {
    return
  }
  val statusNavigationData = rememberStatusNavigationData(navigator)
  TimelineComponent(
    lazyListController = lazyListController,
    savedStateKeyType = SavedStateKeyType.LOCAL,
    statusNavigation = statusNavigationData,
  )
}
