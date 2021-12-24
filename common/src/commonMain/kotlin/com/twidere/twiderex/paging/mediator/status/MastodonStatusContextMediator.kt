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
package com.twidere.twiderex.paging.mediator.status

import com.twidere.services.mastodon.MastodonService
import com.twidere.services.microblog.model.IStatus
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.paging.PagingTimeLineWithStatus
import com.twidere.twiderex.paging.mediator.paging.CursorWithCustomOrderPagination
import com.twidere.twiderex.paging.mediator.paging.CursorWithCustomOrderPagingMediator
import com.twidere.twiderex.paging.mediator.paging.CursorWithCustomOrderPagingResult

class MastodonStatusContextMediator(
    private val service: MastodonService,
    private val statusKey: MicroBlogKey,
    accountKey: MicroBlogKey,
    database: CacheDatabase,
) : CursorWithCustomOrderPagingMediator(accountKey, database) {
    override suspend fun load(
        pageSize: Int,
        paging: CursorWithCustomOrderPagination?
    ): List<IStatus> {
        val result = service.context(statusKey.id)
        val status = service.lookupStatus(statusKey.id)
        return CursorWithCustomOrderPagingResult(
            (result.ancestors ?: emptyList()) + status + (result.descendants ?: emptyList()),
            cursor = null,
            nextOrder = 0,
        )
    }

    override val pagingKey: String = "status:$statusKey"

    override fun hasMore(result: List<PagingTimeLineWithStatus>, pageSize: Int): Boolean {
        return false
    }
}
