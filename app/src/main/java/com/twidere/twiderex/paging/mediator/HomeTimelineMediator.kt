package com.twidere.twiderex.paging.mediator

import androidx.paging.ExperimentalPagingApi
import com.twidere.services.microblog.TimelineService
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.model.UserKey

@OptIn(ExperimentalPagingApi::class)
class HomeTimelineMediator(
    private val service: TimelineService,
    userKey: UserKey,
    database: AppDatabase,
) : PagingWithGapMediator(userKey, database) {
    override val pagingKey: String = "home:$userKey"
    override suspend fun loadBetweenImpl(pageSize: Int, max_id: String?, since_id: String?) =
        service.homeTimeline(pageSize, max_id = max_id, since_id = since_id)
}