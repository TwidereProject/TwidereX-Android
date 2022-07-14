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

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.twidere.services.microblog.TimelineService
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.extensions.asStateIn
import com.twidere.twiderex.model.enums.NotificationCursorType
import com.twidere.twiderex.paging.mediator.timeline.MentionTimelineMediator
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.NotificationRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import moe.tlaster.precompose.viewmodel.viewModelScope

class MentionsTimelineViewModel(
    dataStore: DataStore<Preferences>,
    database: CacheDatabase,
    notificationRepository: NotificationRepository,
    private val accountRepository: AccountRepository,
) : TimelineViewModel(dataStore) {
    private val account by lazy {
        accountRepository.activeAccount.mapNotNull { it }
    }

    override val pagingMediator by lazy {
        account.map {
            MentionTimelineMediator(
                service = it.service as TimelineService,
                accountKey = it.accountKey,
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
        }.asStateIn(viewModelScope, null)
    }
    override val savedStateKey by lazy {
        account.map {
            "${it.accountKey}_mentions"
        }.asStateIn(viewModelScope, null)
    }
}
