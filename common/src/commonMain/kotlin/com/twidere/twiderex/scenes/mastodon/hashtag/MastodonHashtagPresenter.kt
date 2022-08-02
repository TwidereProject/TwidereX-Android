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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.twidere.services.mastodon.MastodonService
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.extensions.collectEvent
import com.twidere.twiderex.extensions.refreshOrRetry
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.repository.TimelineRepository
import com.twidere.twiderex.scenes.CurrentAccountPresenter
import com.twidere.twiderex.scenes.CurrentAccountState
import kotlinx.coroutines.flow.Flow

@Composable
fun MastodonHashtagPresenter(
  flow: Flow<MastodonHashtagEvent>,
  initialKeyword: String,
  repository: TimelineRepository = get(),
): MastodonHashtagState {
  val accountState = CurrentAccountPresenter()
  if (accountState !is CurrentAccountState.Account) {
    return MastodonHashtagState.NoAccount
  }
  val scope = rememberCoroutineScope()
  val source = repository.mastodonHashtagTimeline(
    initialKeyword,
    accountState.account.accountKey,
    // TODO: check if accountState.account.service is MastodonService
    accountState.account.service as MastodonService,
  ).cachedIn(scope).collectAsLazyPagingItems()

  flow.collectEvent {
    when (this) {
      MastodonHashtagEvent.Refresh -> {
        source.refreshOrRetry()
      }
    }
  }

  return MastodonHashtagState.Data(keyword = initialKeyword, source = source)
}

interface MastodonHashtagState {
  object NoAccount : MastodonHashtagState
  data class Data(
    val keyword: String,
    val source: LazyPagingItems<UiStatus>,
  ) : MastodonHashtagState
}

interface MastodonHashtagEvent {
  object Refresh : MastodonHashtagEvent
}
