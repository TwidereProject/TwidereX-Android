/*
 *  Twidere X
 *
 *  Copyright (C) 2020-2021 Tlaster <tlaster@outlook.com>
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

import android.content.SharedPreferences
import com.twidere.services.microblog.NotificationService
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.enums.NotificationCursorType
import com.twidere.twiderex.paging.mediator.paging.PagingWithGapMediator
import com.twidere.twiderex.paging.mediator.timeline.NotificationTimelineMediator
import com.twidere.twiderex.repository.NotificationRepository
import com.twidere.twiderex.room.db.RoomCacheDatabase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class NotificationTimelineViewModel @AssistedInject constructor(
    preferences: SharedPreferences,
    database: RoomCacheDatabase,
    notificationRepository: NotificationRepository,
    @Assisted private val account: AccountDetails
) : TimelineViewModel(preferences) {
    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(account: AccountDetails): NotificationTimelineViewModel
    }

    override val pagingMediator: PagingWithGapMediator =
        NotificationTimelineMediator(
            service = account.service as NotificationService,
            accountKey = account.accountKey,
            database = database,
            addCursorIfNeed = { data, accountKey ->
                notificationRepository.addCursorIfNeeded(
                    accountKey,
                    NotificationCursorType.General,
                    data.status.statusId,
                    data.status.timestamp
                )
            }
        )
    override val savedStateKey: String = "${account.accountKey}_notification"
}
