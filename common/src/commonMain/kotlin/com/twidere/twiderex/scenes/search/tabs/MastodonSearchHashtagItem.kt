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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.twidere.services.mastodon.MastodonService
import com.twidere.services.mastodon.model.Hashtag
import com.twidere.twiderex.component.foundation.SwipeToRefreshLayout
import com.twidere.twiderex.component.navigation.LocalNavigator
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.defaultLoadCount
import com.twidere.twiderex.extensions.collectEvent
import com.twidere.twiderex.extensions.refreshOrRetry
import com.twidere.twiderex.extensions.rememberPresenterState
import com.twidere.twiderex.paging.source.MastodonSearchHashtagPagingSource
import com.twidere.twiderex.scenes.CurrentAccountPresenter
import com.twidere.twiderex.scenes.CurrentAccountState
import kotlinx.coroutines.flow.Flow

class MastodonSearchHashtagItem : SearchSceneItem {
  @Composable
  override fun name(): String {
    return stringResource(res = com.twidere.twiderex.MR.strings.scene_search_tabs_hashtag)
  }

  @OptIn(ExperimentalMaterialApi::class)
  @Composable
  override fun Content(keyword: String) {
    val (state, channel) = rememberPresenterState { MastodonSearchHashtagPresenter(it, keyword) }
    if (state !is MastodonSearchHashtagState.Data) {
      return
    }
    val navigator = LocalNavigator.current
    SwipeToRefreshLayout(
      refreshingState = state.source.loadState.refresh is LoadState.Loading,
      onRefresh = {
        channel.trySend(MastodonSearchHashtagEvent.Refresh)
      }
    ) {
      if (state.source.itemCount > 0) {
        LazyColumn {
          items(state.source) {
            it?.name?.let { name ->
              ListItem(
                modifier = Modifier
                  .clickable {
                    navigator.hashtag(name)
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

@Composable
fun MastodonSearchHashtagPresenter(
  flow: Flow<MastodonSearchHashtagEvent>,
  keyword: String,
): MastodonSearchHashtagState {
  val account = CurrentAccountPresenter()
  if (account !is CurrentAccountState.Account) {
    return MastodonSearchHashtagState.NoAccount
  }
  val scope = rememberCoroutineScope()
  val source = androidx.paging.Pager(
    config = PagingConfig(
      pageSize = defaultLoadCount,
      enablePlaceholders = false,
    )
  ) {
    MastodonSearchHashtagPagingSource(
      keyword,
      // TODO: check if accountState.account.service is MastodonService
      account.account.service as MastodonService
    )
  }.flow.cachedIn(scope).collectAsLazyPagingItems()
  flow.collectEvent {
    when (this) {
      is MastodonSearchHashtagEvent.Refresh -> {
        source.refreshOrRetry()
      }
    }
  }
  return MastodonSearchHashtagState.Data(source)
}

interface MastodonSearchHashtagState {
  object NoAccount : MastodonSearchHashtagState
  data class Data(val source: LazyPagingItems<Hashtag>) : MastodonSearchHashtagState
}

interface MastodonSearchHashtagEvent {
  object Refresh : MastodonSearchHashtagEvent
}
