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
package com.twidere.twiderex.scenes.search.tabs.presenter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.twidere.services.mastodon.MastodonService
import com.twidere.services.mastodon.model.Hashtag
import com.twidere.twiderex.defaultLoadCount
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.paging.source.MastodonSearchHashtagPagingSource
import com.twidere.twiderex.repository.AccountRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapNotNull

@Composable
fun MastodonSearchHashtagPresenter(
  accountRepository: AccountRepository = get(),
  keyword: String,
): MastodonSearchHashtagState {

  val scope = rememberCoroutineScope()

  @OptIn(ExperimentalCoroutinesApi::class)
  val data = remember {
    accountRepository.activeAccount.mapNotNull { it }.flatMapLatest {
      Pager(
        config = PagingConfig(
          pageSize = defaultLoadCount,
          enablePlaceholders = false,
        )
      ) {
        MastodonSearchHashtagPagingSource(
          keyword,
          it.service as MastodonService
        )
      }.flow
    }.cachedIn(scope)
  }

  return MastodonSearchHashtagState(data = data.collectAsLazyPagingItems())
}

data class MastodonSearchHashtagState(
  val data: LazyPagingItems<Hashtag>
)
