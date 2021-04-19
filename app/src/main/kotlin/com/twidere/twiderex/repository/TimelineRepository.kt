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
package com.twidere.twiderex.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import com.twidere.services.mastodon.MastodonService
import com.twidere.services.microblog.TimelineService
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.extensions.toUi
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.paging.mediator.MastodonHashtagTimelineMediator
import com.twidere.twiderex.paging.mediator.paging.pager
import com.twidere.twiderex.paging.mediator.user.UserFavouriteMediator
import com.twidere.twiderex.paging.mediator.user.UserStatusMediator
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalPagingApi::class)
class TimelineRepository(
    private val database: CacheDatabase,
) {
    fun favouriteTimeline(
        userKey: MicroBlogKey,
        account: AccountDetails,
    ): Flow<PagingData<UiStatus>> {
        val mediator = UserFavouriteMediator(
            userKey = userKey,
            platformType = account.type,
            database = database,
            accountKey = account.accountKey,
            service = account.service as TimelineService,
        )
        return mediator.pager().toUi(accountKey = account.accountKey)
    }

    fun userTimeline(
        userKey: MicroBlogKey,
        account: AccountDetails,
        exclude_replies: Boolean,
    ): Flow<PagingData<UiStatus>> {
        return UserStatusMediator(
            userKey = userKey,
            database = database,
            accountKey = account.accountKey,
            service = account.service as TimelineService,
            exclude_replies = exclude_replies,
        ).pager().toUi(accountKey = account.accountKey)
    }

    fun mastodonHashtagTimeline(
        keyword: String,
        account: AccountDetails
    ): Flow<PagingData<UiStatus>> {
        val mediator = MastodonHashtagTimelineMediator(
            keyword = keyword,
            service = account.service as MastodonService,
            accountKey = account.accountKey,
            database = database
        )
        return mediator.pager().toUi(accountKey = account.accountKey)
    }
}
