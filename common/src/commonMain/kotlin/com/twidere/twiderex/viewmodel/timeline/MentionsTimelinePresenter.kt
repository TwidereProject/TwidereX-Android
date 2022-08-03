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
import com.twidere.twiderex.model.enums.NotificationCursorType
import com.twidere.twiderex.paging.mediator.timeline.MentionTimelineMediator
import com.twidere.twiderex.repository.NotificationRepository
import com.twidere.twiderex.scenes.CurrentAccountPresenter
import com.twidere.twiderex.scenes.CurrentAccountState
import kotlinx.coroutines.flow.Flow

@Composable
fun MentionsTimelinePresenter(
  event: Flow<TimeLineEvent>,
  database: CacheDatabase = get(),
  notificationRepository: NotificationRepository = get(),
): MentionsTimelineState {

  val accountState = CurrentAccountPresenter()

  if (accountState !is CurrentAccountState.Account) {
    return MentionsTimelineState.NoAccount
  }

  val pagingMediator by derivedStateOf {
    MentionTimelineMediator(
      service = accountState.account.service as TimelineService,
      accountKey = accountState.account.accountKey,
      database = database,
      addCursorIfNeed = { data, accountKey ->
        notificationRepository.addCursorIfNeeded(
          accountKey,
          NotificationCursorType.Mentions,
          data.status.statusId,
          data.status.timestamp,
        )
      }
    )
  }

  val savedStateKey by derivedStateOf {
    "${accountState.account.accountKey}_mentions"
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

  return MentionsTimelineState.Data(state)
}

interface MentionsTimelineState {
  data class Data(
    val state: TimelineState
  ) : MentionsTimelineState
  object NoAccount : MentionsTimelineState
}
