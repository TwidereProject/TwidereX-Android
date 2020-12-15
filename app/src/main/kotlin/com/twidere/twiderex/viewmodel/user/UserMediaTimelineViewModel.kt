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

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.twidere.services.microblog.TimelineService
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.di.assisted.IAssistedFactory
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.model.ui.UiStatus.Companion.toUi
import com.twidere.twiderex.paging.mediator.PagingMediator
import com.twidere.twiderex.paging.mediator.pager
import com.twidere.twiderex.paging.mediator.user.UserMediaMediator
import com.twidere.twiderex.viewmodel.PagingViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserMediaTimelineViewModel @AssistedInject constructor(
    database: AppDatabase,
    @Assisted account: AccountDetails,
    @Assisted screenName: String,
    @Assisted userKey: MicroBlogKey,
) : PagingViewModel() {

    @AssistedInject.Factory
    interface AssistedFactory : IAssistedFactory {
        fun create(
            account: AccountDetails,
            screenName: String,
            userKey: MicroBlogKey,
        ): UserMediaTimelineViewModel
    }

    override val source: Flow<PagingData<UiStatus>> by lazy {
        pagingMediator.pager(pageSize = 200).flow.map { pagingData ->
            pagingData.map {
                it.toUi(pagingMediator.accountKey)
            }
        }.cachedIn(viewModelScope)
    }

    override val pagingMediator: PagingMediator =
        UserMediaMediator(
            screenName = screenName,
            userKey = userKey,
            database,
            account.accountKey,
            account.service as TimelineService
        )
}
