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
package com.twidere.twiderex.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.flatMap
import com.twidere.services.mastodon.MastodonService
import com.twidere.services.microblog.TimelineService
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.ui.UiMedia
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.paging.mediator.list.ListsTimelineMediator
import com.twidere.twiderex.paging.mediator.paging.pager
import com.twidere.twiderex.paging.mediator.paging.toUi
import com.twidere.twiderex.paging.mediator.timeline.MastodonHashtagTimelineMediator
import com.twidere.twiderex.paging.mediator.user.UserFavouriteMediator
import com.twidere.twiderex.paging.mediator.user.UserMediaMediator
import com.twidere.twiderex.paging.mediator.user.UserStatusMediator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalPagingApi::class)
class TimelineRepository(
    private val database: CacheDatabase,
) {
    fun favouriteTimeline(
        userKey: MicroBlogKey,
        accountKey: MicroBlogKey,
        platformType: PlatformType,
        service: TimelineService,
    ): Flow<PagingData<UiStatus>> {
        val mediator = UserFavouriteMediator(
            userKey = userKey,
            platformType = platformType,
            database = database,
            accountKey = accountKey,
            service = service,
        )
        return mediator.pager().toUi()
    }

    fun userTimeline(
        userKey: MicroBlogKey,
        accountKey: MicroBlogKey,
        service: TimelineService,
        exclude_replies: Boolean,
    ): Flow<PagingData<UiStatus>> {
        return UserStatusMediator(
            userKey = userKey,
            database = database,
            accountKey = accountKey,
            service = service,
            exclude_replies = exclude_replies,
        ).pager().toUi()
    }

    fun listTimeline(
        accountKey: MicroBlogKey,
        service: TimelineService,
        listKey: MicroBlogKey,
    ): Flow<PagingData<UiStatus>> {
        return ListsTimelineMediator(
            accountKey = accountKey,
            database = database,
            listKey = listKey,
            service = service
        ).pager().toUi()
    }

    fun mastodonHashtagTimeline(
        keyword: String,
        accountKey: MicroBlogKey,
        service: MastodonService,
    ): Flow<PagingData<UiStatus>> {
        val mediator = MastodonHashtagTimelineMediator(
            keyword = keyword,
            service = service,
            accountKey = accountKey,
            database = database
        )
        return mediator.pager().toUi()
    }

    fun mediaTimeline(
        userKey: MicroBlogKey,
        accountKey: MicroBlogKey,
        service: TimelineService
    ): Flow<PagingData<Pair<UiMedia, UiStatus>>> {
        val mediator = UserMediaMediator(
            userKey = userKey,
            database = database,
            accountKey = accountKey,
            service = service
        )
        return mediator.pager().toUi().map {
            it.flatMap {
                it.media.map { media -> media to it }
            }
        }
    }
}
