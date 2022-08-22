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
package com.twidere.twiderex.viewmodel.user

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.twidere.services.microblog.TimelineService
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiMedia
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.repository.TimelineRepository
import com.twidere.twiderex.scenes.CurrentAccountPresenter
import com.twidere.twiderex.scenes.CurrentAccountState

@Composable
fun UserMediaTimelinePresenter(
  repository: TimelineRepository = get(),
  userKey: MicroBlogKey,
): UserMediaTimelineState {

  val currentAccount = CurrentAccountPresenter()

  if (currentAccount !is CurrentAccountState.Account) {
    return UserMediaTimelineState.NoAccount
  }

  val source = remember(currentAccount) {
    repository.mediaTimeline(
      userKey,
      currentAccount.account.accountKey,
      currentAccount.account.service as TimelineService
    )
  }.collectAsLazyPagingItems()

  return UserMediaTimelineState.Data(source = source)
}

interface UserMediaTimelineState {
  data class Data(
    val source: LazyPagingItems<Pair<UiMedia, UiStatus>>
  ) : UserMediaTimelineState

  object NoAccount : UserMediaTimelineState
}
