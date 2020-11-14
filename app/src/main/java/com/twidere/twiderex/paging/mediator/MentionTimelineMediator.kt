package com.twidere.twiderex.paging.mediator

import com.twidere.services.microblog.TimelineService
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.model.UserKey

class MentionTimelineMediator(
    private val service: TimelineService, userKey: UserKey, database: AppDatabase
) : PagingWithGapMediator(userKey, database) {
    override suspend fun loadBetweenImpl(
        pageSize: Int,
        max_id: String?,
        since_id: String?
    ) = service.mentionsTimeline(pageSize, max_id = max_id, since_id = since_id)

    override val pagingKey: String = "mentions:$userKey"
}