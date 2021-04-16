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
package com.twidere.twiderex.viewmodel.user

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.twidere.services.microblog.TimelineService
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiStatus.Companion.toUi
import com.twidere.twiderex.paging.mediator.paging.PagingMediator
import com.twidere.twiderex.paging.mediator.paging.pager
import com.twidere.twiderex.paging.mediator.user.UserFavouriteMediator
import com.twidere.twiderex.viewmodel.PagingViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.map

class UserFavouriteTimelineViewModel @AssistedInject constructor(
    database: CacheDatabase,
    @Assisted account: AccountDetails,
    @Assisted userKey: MicroBlogKey,
) : PagingViewModel() {

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(
            account: AccountDetails,
            userKey: MicroBlogKey,
        ): UserFavouriteTimelineViewModel
    }

    override val source by lazy {
        pagingMediator.pager().flow.map { pagingData ->
            pagingData.map {
                it.toUi(pagingMediator.accountKey)
            }
        }.cachedIn(viewModelScope)
    }

    override val pagingMediator: PagingMediator =
        UserFavouriteMediator(
            userKey = userKey,
            platformType = account.type,
            database = database,
            accountKey = account.accountKey,
            service = account.service as TimelineService,
        )
}
