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
package com.twidere.twiderex.paging.mediator.search

import com.twidere.services.microblog.model.IStatus
import com.twidere.services.twitter.TwitterService
import com.twidere.services.twitter.model.exceptions.TwitterApiExceptionV2
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.paging.mediator.PagingTimelineMediatorBase

class SearchMediaMediator(
    private val query: String,
    database: CacheDatabase,
    accountKey: MicroBlogKey,
    private val service: TwitterService,
    inAppNotification: InAppNotification,
) : PagingTimelineMediatorBase(accountKey, database, inAppNotification) {
    override val pagingKey = "search:$query:media"
    private var nextPage: String? = null
    override suspend fun load(pageSize: Int, max_id: String?): List<IStatus> {
        val result = try {
            service.searchTweets("$query has:media -is:retweet", count = pageSize, nextPage = nextPage)
        } catch (e: TwitterApiExceptionV2) {
            service.searchTweetsV1("$query filter:media -filter:retweets", count = pageSize, max_id = nextPage)
        }
        nextPage = result.nextPage
        return result.status
    }
}
