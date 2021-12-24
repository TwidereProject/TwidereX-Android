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
package com.twidere.twiderex.paging.mediator.user

import androidx.paging.ExperimentalPagingApi
import com.twidere.services.microblog.TimelineService
import com.twidere.services.microblog.model.IStatus
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.paging.UserTimelineType
import com.twidere.twiderex.model.paging.pagingKey
import com.twidere.twiderex.paging.SinceMaxPagination
import com.twidere.twiderex.paging.mediator.paging.MaxIdPagingMediator

@OptIn(ExperimentalPagingApi::class)
class UserStatusMediator(
    private val userKey: MicroBlogKey,
    database: CacheDatabase,
    accountKey: MicroBlogKey,
    private val service: TimelineService,
    private val exclude_replies: Boolean = false,
) : MaxIdPagingMediator(accountKey, database) {
    override val pagingKey: String
        get() = UserTimelineType.Status.pagingKey(userKey) + ":exclude_replies=$exclude_replies"

    override suspend fun load(pageSize: Int, paging: SinceMaxPagination?): List<IStatus> {
        return service.userTimeline(
            user_id = userKey.id,
            count = pageSize,
            max_id = paging?.maxId,
            exclude_replies = exclude_replies,
        )
    }
}
