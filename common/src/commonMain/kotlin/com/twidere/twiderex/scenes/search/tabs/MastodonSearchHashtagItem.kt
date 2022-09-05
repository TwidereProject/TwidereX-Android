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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.items
import com.twidere.twiderex.component.foundation.SwipeToRefreshLayout
import moe.tlaster.precompose.navigation.Navigator
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.extensions.refreshOrRetry
import com.twidere.twiderex.extensions.rememberPresenter
import com.twidere.twiderex.scenes.search.tabs.presenter.MastodonSearchHashtagPresenter
import com.twidere.twiderex.scenes.search.tabs.presenter.MastodonSearchHashtagState

class MastodonSearchHashtagItem : SearchSceneItem {
  @Composable
  override fun name(): String {
    return stringResource(res = com.twidere.twiderex.MR.strings.scene_search_tabs_hashtag)
  }

  @OptIn(ExperimentalMaterialApi::class)
  @Composable
  override fun Content(
    keyword: String,
    navigator: Navigator,
  ) {

    val state by rememberPresenter {
      MastodonSearchHashtagPresenter(keyword = keyword)
    }.collectAsState()

    (state as? MastodonSearchHashtagState.Data)?.let {
      SwipeToRefreshLayout(
        refreshingState = it.data.loadState.refresh is LoadState.Loading,
        onRefresh = {
          it.data.refreshOrRetry()
        }
      ) {
        if (it.data.itemCount > 0) {
          LazyColumn {
            items(it.data) {
              it?.name?.let { name ->
                ListItem(
                  modifier = Modifier
                    .clickable {
                      // navigator.hashtag(name)
                    }
                ) {
                  Text(text = name)
                }
              }
            }
          }
        }
      }
    }
  }
}
