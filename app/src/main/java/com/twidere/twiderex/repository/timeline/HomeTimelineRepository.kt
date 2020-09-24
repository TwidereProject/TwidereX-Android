package com.twidere.twiderex.repository.timeline

import com.twidere.services.microblog.HomeTimelineService
import com.twidere.services.microblog.model.IStatus
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.model.TimelineType
import com.twidere.twiderex.model.UserKey

class HomeTimelineRepository(
    userKey: UserKey,
    private val service: HomeTimelineService,
    database: AppDatabase,
    count: Int = 20,
) : TimelineRepository(userKey, database, count) {
    override val type: TimelineType
        get() = TimelineType.Home

    override suspend fun loadData(count: Int, since_id: String?, max_id: String?): List<IStatus> {
        return service.homeTimeline(count = count, since_id = since_id, max_id = max_id)
    }
}