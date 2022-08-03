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
package com.twidere.twiderex.viewmodel.timeline

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import com.twidere.services.microblog.TimelineService
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.extensions.rememberNestedPresenter
import com.twidere.twiderex.paging.mediator.timeline.HomeTimelineMediator
import com.twidere.twiderex.scenes.CurrentAccountPresenter
import com.twidere.twiderex.scenes.CurrentAccountState
import kotlinx.coroutines.flow.Flow

@Composable
fun HomeTimelinePresenter(
  event: Flow<TimeLineEvent>,
  database: CacheDatabase = get(),
): HomeTimelineState {

  val accountState = CurrentAccountPresenter()

  if (accountState !is CurrentAccountState.Account) {
    return HomeTimelineState.NoAccount
  }

  val pagingMediator by derivedStateOf {
    HomeTimelineMediator(
      accountState.account.service as TimelineService,
      accountState.account.accountKey,
      database,
    )
  }

  val savedStateKey by derivedStateOf {
    "${accountState.account.accountKey}_home"
  }

  val (state, channel) = key(accountState) {
    rememberNestedPresenter<TimelineState, TimeLineEvent> {
      TimelinePresenter(
        it,
        pagingMediator = pagingMediator,
        savedStateKey = savedStateKey
      )
    }
  }

  LaunchedEffect(Unit) {
    event.collect {
      channel.trySend(it)
    }
  }

  return HomeTimelineState.Data(state = state)
}

interface HomeTimelineState {
  data class Data(
    val state: TimelineState
  ) : HomeTimelineState
  object NoAccount : HomeTimelineState
}
