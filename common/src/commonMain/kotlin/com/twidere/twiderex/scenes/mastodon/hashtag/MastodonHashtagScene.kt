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
package com.twidere.twiderex.scenes.mastodon.hashtag

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.paging.LoadState
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.foundation.SwipeToRefreshLayout
import com.twidere.twiderex.component.lazy.ui.LazyUiStatusList
import com.twidere.twiderex.extensions.rememberPresenterState
import com.twidere.twiderex.ui.TwidereScene

@Composable
fun MastodonHashtagScene(keyword: String) {
  val (state, channel) = rememberPresenterState { MastodonHashtagPresenter(it, keyword) }
  if (state !is MastodonHashtagState.Data) {
    // TODO: show other states
    return
  }
  TwidereScene {
    InAppNotificationScaffold(
      topBar = {
        AppBar(
          navigationIcon = {
            AppBarNavigationButton()
          },
          title = {
            Text(text = state.keyword)
          }
        )
      }
    ) {
      SwipeToRefreshLayout(
        refreshingState = state.source.loadState.refresh is LoadState.Loading,
        onRefresh = {
          channel.trySend(MastodonHashtagEvent.Refresh)
        },
      ) {
        LazyUiStatusList(items = state.source)
      }
    }
  }
}
