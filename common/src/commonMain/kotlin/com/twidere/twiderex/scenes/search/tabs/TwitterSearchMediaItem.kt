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
package com.twidere.twiderex.scenes.search.tabs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.paging.LoadState
import com.twidere.twiderex.component.foundation.SwipeToRefreshLayout
import com.twidere.twiderex.component.lazy.ui.LazyUiStatusImageList
import com.twidere.twiderex.component.navigation.media
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.extensions.refreshOrRetry
import com.twidere.twiderex.extensions.rememberPresenter
import com.twidere.twiderex.preferences.model.DisplayPreferences
import com.twidere.twiderex.scenes.search.tabs.presenter.TwitterSearchMediaPresenter
import com.twidere.twiderex.scenes.search.tabs.presenter.TwitterSearchMediaState
import com.twidere.twiderex.ui.LocalVideoPlayback
import moe.tlaster.precompose.navigation.Navigator

class TwitterSearchMediaItem : SearchSceneItem {
  @Composable
  override fun name(): String {
    return stringResource(res = com.twidere.twiderex.MR.strings.scene_search_tabs_media)
  }

  @Composable
  override fun Content(
    keyword: String,
    navigator: Navigator,
  ) {
    val state by rememberPresenter {
      TwitterSearchMediaPresenter(keyword = keyword)
    }.collectAsState()

    (state as? TwitterSearchMediaState.Data)?.let {
      CompositionLocalProvider(
        LocalVideoPlayback provides DisplayPreferences.AutoPlayback.Off
      ) {
        SwipeToRefreshLayout(
          refreshingState = it.data.loadState.refresh is LoadState.Loading,
          onRefresh = {
            it.data.refreshOrRetry()
          }
        ) {
          LazyUiStatusImageList(
            items = it.data,
            openMedia = { key, index ->
              navigator.media(key, index)
            }
          )
        }
      }
    }
  }
}
