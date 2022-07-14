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
package com.twidere.twiderex.paging.mediator.search

import com.twidere.services.microblog.SearchService
import com.twidere.services.microblog.model.IStatus
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.paging.PagingTimeLineWithStatus
import com.twidere.twiderex.paging.CursorPagination
import com.twidere.twiderex.paging.mediator.paging.CursorPagingMediator
import com.twidere.twiderex.paging.mediator.paging.CursorPagingResult

class SearchStatusMediator(
    private val query: String,
    database: CacheDatabase,
    accountKey: MicroBlogKey,
    private val service: SearchService,
) : CursorPagingMediator(accountKey, database) {
    override val pagingKey = "search:$query:status"
    override suspend fun load(pageSize: Int, paging: CursorPagination?): List<IStatus> {
        val result = service.searchTweets(query, count = pageSize, nextPage = paging?.cursor)
        return CursorPagingResult(result.status, result.nextPage)
    }

    override fun hasMore(result: List<PagingTimeLineWithStatus>, pageSize: Int): Boolean {
        return result.size == pageSize
    }
}
