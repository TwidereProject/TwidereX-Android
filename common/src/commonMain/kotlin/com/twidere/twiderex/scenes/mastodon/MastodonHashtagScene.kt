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
package com.twidere.twiderex.scenes.mastodon

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.foundation.SwipeToRefreshLayout
import com.twidere.twiderex.component.lazy.ui.LazyUiStatusList
import com.twidere.twiderex.di.ext.getViewModel
import com.twidere.twiderex.extensions.refreshOrRetry
import com.twidere.twiderex.navigation.Root
import com.twidere.twiderex.navigation.RootDeepLinks
import com.twidere.twiderex.navigation.rememberStatusNavigationData
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.viewmodel.mastodon.MastodonHashtagViewModel
import io.github.seiko.precompose.annotation.NavGraphDestination
import io.github.seiko.precompose.annotation.Path
import moe.tlaster.precompose.navigation.Navigator
import org.koin.core.parameter.parametersOf

@NavGraphDestination(
  route = Root.Mastodon.Hashtag.route,
  deepLink = [RootDeepLinks.Mastodon.Hashtag.route]
)
@Composable
fun MastodonHashtagScene(
  @Path("keyword") keyword: String,
  navigator: Navigator,
) {
  val viewModel: MastodonHashtagViewModel = getViewModel {
    parametersOf(keyword)
  }
  val source = viewModel.source.collectAsLazyPagingItems()
  val statusNavigationData = rememberStatusNavigationData(navigator)
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
            Text(text = keyword)
          }
        )
      }
    ) {
      SwipeToRefreshLayout(
        refreshingState = source.loadState.refresh is LoadState.Loading,
        onRefresh = {
          source.refreshOrRetry()
        },
      ) {
        LazyUiStatusList(
          items = source,
          statusNavigation = statusNavigationData,
        )
      }
    }
  }
}
