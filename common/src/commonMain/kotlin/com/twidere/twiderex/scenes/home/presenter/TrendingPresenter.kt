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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.twidere.services.microblog.TrendService
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.model.ui.UiTrend
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.TrendRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapNotNull

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun TrendingPresenter(
  repository: TrendRepository = get(),
  accountRepository: AccountRepository = get(),
): SearchItemState {

  val scope = rememberCoroutineScope()

  val pagingData = remember {
    accountRepository.activeAccount.mapNotNull { it }.flatMapLatest {
      repository.trendsSource(
        accountKey = it.accountKey,
        service = it.service as TrendService
      )
    }.cachedIn(scope)
  }

  return SearchItemState(data = pagingData.collectAsLazyPagingItems())
}

data class SearchItemState(val data: LazyPagingItems<UiTrend>)
