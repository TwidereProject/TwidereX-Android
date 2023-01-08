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
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.map
import com.twidere.services.microblog.SearchService
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.paging.mediator.paging.pager
import com.twidere.twiderex.paging.mediator.search.SearchStatusMediator
import com.twidere.twiderex.scenes.CurrentAccountPresenter
import com.twidere.twiderex.scenes.CurrentAccountState
import kotlinx.coroutines.flow.map

@Composable
fun SearchTweetsPresenter(
  database: CacheDatabase = get(),
  keyword: String,
): SearchTweetsState {
  val scope = rememberCoroutineScope()
  val accountState = CurrentAccountPresenter()
  if (accountState !is CurrentAccountState.Account) {
    return SearchTweetsState.NoAccount
  }
  val data = remember(accountState) {
    SearchStatusMediator(
      keyword,
      database,
      accountState.account.accountKey,
      accountState.account.service as SearchService
    ).pager().flow.map { it.map { it.status } }.cachedIn(scope)
  }
  return SearchTweetsState.Data(
    data = data.collectAsLazyPagingItems()
  )
}

interface SearchTweetsState {
  data class Data(
    val data: LazyPagingItems<UiStatus>
  ) : SearchTweetsState
  object NoAccount : SearchTweetsState
}
