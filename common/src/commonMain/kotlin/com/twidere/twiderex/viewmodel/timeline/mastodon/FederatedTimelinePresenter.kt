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
package com.twidere.twiderex.viewmodel.timeline.mastodon

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import com.twidere.services.mastodon.MastodonService
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.extensions.rememberNestedPresenter
import com.twidere.twiderex.paging.mediator.timeline.mastodon.FederatedTimelineMediator
import com.twidere.twiderex.scenes.CurrentAccountPresenter
import com.twidere.twiderex.scenes.CurrentAccountState
import com.twidere.twiderex.viewmodel.timeline.TimeLineEvent
import com.twidere.twiderex.viewmodel.timeline.TimelinePresenter
import com.twidere.twiderex.viewmodel.timeline.TimelineState
import kotlinx.coroutines.flow.Flow

@Composable
fun FederatedTimelinePresenter(
  event: Flow<TimeLineEvent>,
  database: CacheDatabase = get()
): FederatedTimelineState {

  val accountState = CurrentAccountPresenter()

  if (accountState !is CurrentAccountState.Account) {
    return FederatedTimelineState.NoAccount
  }

  val pagingMediator by derivedStateOf {
    (accountState.account.service as? MastodonService)?.let {
      FederatedTimelineMediator(
        it,
        accountState.account.accountKey,
        database,
      )
    }
  }

  if (pagingMediator == null) {
    return FederatedTimelineState.NoMastodon
  }

  val savedStateKey by derivedStateOf {
    "${accountState.account.accountKey}_federated"
  }

  val (state, channel) = key(accountState) {
    rememberNestedPresenter<TimelineState, TimeLineEvent> {
      TimelinePresenter(
        it,
        pagingMediator = pagingMediator!!,
        savedStateKey = savedStateKey
      )
    }
  }

  LaunchedEffect(Unit) {
    event.collect {
      channel.trySend(it)
    }
  }

  return FederatedTimelineState.Data(state)
}

interface FederatedTimelineState {
  data class Data(
    val state: TimelineState
  ) : FederatedTimelineState
  object NoAccount : FederatedTimelineState
  object NoMastodon : FederatedTimelineState
}
