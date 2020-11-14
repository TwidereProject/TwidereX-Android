package com.twidere.twiderex.paging.mediator.search

import com.twidere.services.microblog.SearchService
import com.twidere.services.microblog.model.IStatus
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.model.UserKey
import com.twidere.twiderex.paging.mediator.PagingTimelineMediatorBase

class SearchStatusMediator(
    private val query: String,
    database: AppDatabase,
    userKey: UserKey,
    private val service: SearchService,
) : PagingTimelineMediatorBase(userKey, database) {
    override val pagingKey = "search:$query:status"
    private var nextPage: String? = null
    override suspend fun load(pageSize: Int, max_id: String?): List<IStatus> {
        val result = service.searchTweets("$query -is:retweet", count = pageSize, nextPage = nextPage)
        nextPage = result.nextPage
        return result.status
    }
}
