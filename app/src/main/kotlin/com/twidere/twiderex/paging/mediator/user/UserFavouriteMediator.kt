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
package com.twidere.twiderex.paging.mediator.user

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import com.twidere.services.mastodon.model.MastodonPaging
import com.twidere.services.microblog.TimelineService
import com.twidere.services.microblog.model.IStatus
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.db.model.DbPagingTimelineWithStatus
import com.twidere.twiderex.db.model.UserTimelineType
import com.twidere.twiderex.db.model.pagingKey
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.PlatformType
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.paging.PagingList
import com.twidere.twiderex.paging.SinceMaxPagination
import com.twidere.twiderex.paging.mediator.paging.MaxIdPagingMediator

@OptIn(ExperimentalPagingApi::class)
class UserFavouriteMediator(
    private val userKey: MicroBlogKey,
    private val platformType: PlatformType,
    database: CacheDatabase,
    accountKey: MicroBlogKey,
    private val service: TimelineService,
    inAppNotification: InAppNotification,
) : MaxIdPagingMediator(accountKey, database, inAppNotification) {
    override val pagingKey: String
        get() = UserTimelineType.Favourite.pagingKey(userKey)

    override fun transform(
        type: LoadType,
        state: PagingState<Int, DbPagingTimelineWithStatus>,
        data: List<DbPagingTimelineWithStatus>
    ): List<DbPagingTimelineWithStatus> {
        val lastId = state.lastItemOrNull()?.timeline?.sortId ?: 0
        return data.mapIndexed { index, dbPagingTimelineWithStatus ->
            dbPagingTimelineWithStatus.copy(
                timeline = dbPagingTimelineWithStatus.timeline.copy(
                    sortId = lastId + index
                )
            )
        }
    }

    override suspend fun load(pageSize: Int, paging: SinceMaxPagination?): List<IStatus> {
        val result = service.favorites(
            user_id = userKey.id,
            count = pageSize,
            max_id = paging?.maxId,
        )
        return if (platformType == PlatformType.Mastodon && result is MastodonPaging<*>) {
            PagingList(result, nextPage = SinceMaxPagination(maxId = result.next))
        } else {
            result
        }
    }

    override fun hasMore(result: List<DbPagingTimelineWithStatus>, pageSize: Int): Boolean {
        return if (platformType == PlatformType.Mastodon) {
            result.size == pageSize
        } else {
            super.hasMore(result, pageSize)
        }
    }
}
