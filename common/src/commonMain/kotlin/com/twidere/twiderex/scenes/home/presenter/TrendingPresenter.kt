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
package com.twidere.twiderex.scenes.home.presenter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.twidere.services.microblog.TrendService
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.extensions.rememberNestedPresenter
import com.twidere.twiderex.model.ui.UiTrend
import com.twidere.twiderex.repository.TrendRepository
import com.twidere.twiderex.scenes.CurrentAccountPresenter
import com.twidere.twiderex.scenes.CurrentAccountState
import com.twidere.twiderex.scenes.search.presenter.SearchInputEvent
import com.twidere.twiderex.scenes.search.presenter.SearchInputPresenter
import com.twidere.twiderex.scenes.search.presenter.SearchInputState
import kotlinx.coroutines.flow.Flow

@Composable
fun TrendingPresenter(
  events: Flow<SearchInputEvent>,
  repository: TrendRepository = get(),
): SearchItemState {

  val accountState = CurrentAccountPresenter()

  if (accountState !is CurrentAccountState.Account) {
    return SearchItemState.NoAccount
  }

  val (state, channel) = rememberNestedPresenter <SearchInputState, SearchInputEvent> {
    SearchInputPresenter(it, keyword = "")
  }

  LaunchedEffect(Unit) {
    events.collect {
      channel.trySend(it)
    }
  }

  val pagingData = remember {
    repository.trendsSource(
      accountKey = accountState.account.accountKey,
      service = accountState.account.service as TrendService
    )
  }

  return SearchItemState.Data(
    data = pagingData.collectAsLazyPagingItems(),
    searchInputState = state
  )
}

interface SearchItemState {
  data class Data(
    val data: LazyPagingItems<UiTrend>,
    val searchInputState: SearchInputState
  ) : SearchItemState
  object NoAccount : SearchItemState
}
