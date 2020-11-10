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
package com.twidere.twiderex.viewmodel.user

import androidx.lifecycle.LiveData
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.twidere.services.microblog.TimelineService
import com.twidere.twiderex.di.assisted.IAssistedFactory
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.repository.timeline.UserFavouriteTimelineRepository

class UserFavouriteTimelineViewModel @AssistedInject constructor(
    private val factory: UserFavouriteTimelineRepository.AssistedFactory,
    @Assisted private val account: AccountDetails,
    @Assisted screenName: String,
) : UserTimelineViewModelBase(screenName = screenName) {

    @AssistedInject.Factory
    interface AssistedFactory : IAssistedFactory {
        fun create(account: AccountDetails, screenName: String): UserFavouriteTimelineViewModel
    }

    private val repository by lazy {
        account.service.let {
            it as TimelineService
        }.let {
            factory.create(account.key, it)
        }
    }

    override val source: LiveData<List<UiStatus>>
        get() = repository.liveData

    override suspend fun loadBetween(
        max_id: String?,
        since_Id: String?
    ) = repository.loadBetween(
        screenName = screenName,
        max_id = max_id,
        since_id = since_Id,
    )
}
