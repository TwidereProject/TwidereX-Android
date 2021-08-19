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
package com.twidere.twiderex.viewmodel.timeline.mastodon

import android.content.SharedPreferences
import com.twidere.services.mastodon.MastodonService
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.paging.mediator.timeline.mastodon.LocalTimelineMediator
import com.twidere.twiderex.room.db.RoomCacheDatabase
import com.twidere.twiderex.viewmodel.timeline.TimelineViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class LocalTimelineViewModel @AssistedInject constructor(
    preferences: SharedPreferences,
    database: RoomCacheDatabase,
    @Assisted account: AccountDetails,
) : TimelineViewModel(preferences) {
    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(account: AccountDetails): LocalTimelineViewModel
    }

    override val pagingMediator by lazy {
        LocalTimelineMediator(
            account.service as MastodonService,
            account.accountKey,
            database,
        )
    }

    override val savedStateKey = "${account.accountKey}_local"
}
