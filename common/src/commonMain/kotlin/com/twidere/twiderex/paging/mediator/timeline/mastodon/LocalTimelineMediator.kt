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
package com.twidere.twiderex.paging.mediator.timeline.mastodon

import com.twidere.services.mastodon.MastodonService
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.paging.mediator.paging.PagingWithGapMediator

class LocalTimelineMediator(
    private val service: MastodonService,
    accountKey: MicroBlogKey,
    database: CacheDatabase,
) : PagingWithGapMediator(accountKey, database) {
    override val pagingKey: String = "local:$accountKey"
    override suspend fun loadBetweenImpl(pageSize: Int, max_id: String?, since_id: String?) =
        service.localTimeline(pageSize, max_id = max_id, since_id = since_id)
}
