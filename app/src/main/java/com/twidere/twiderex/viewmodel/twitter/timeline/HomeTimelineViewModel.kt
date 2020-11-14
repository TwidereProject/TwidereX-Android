/*
 *  Twidere X
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
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
package com.twidere.twiderex.viewmodel.twitter.timeline

import android.content.SharedPreferences
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.twidere.services.microblog.TimelineService
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.di.assisted.IAssistedFactory
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.paging.mediator.HomeTimelineMediator
import com.twidere.twiderex.paging.mediator.PagingWithGapMediator

class HomeTimelineViewModel @AssistedInject constructor(
    preferences: SharedPreferences,
    database: AppDatabase,
    @Assisted account: AccountDetails,
) : TimelineViewModel(preferences) {
    @AssistedInject.Factory
    interface AssistedFactory : IAssistedFactory {
        fun create(account: AccountDetails): HomeTimelineViewModel
    }

    override val pagingMediator: PagingWithGapMediator =
        HomeTimelineMediator(account.service as TimelineService, account.key, database)
    override val savedStateKey: String = "${account.key}_home"
}
