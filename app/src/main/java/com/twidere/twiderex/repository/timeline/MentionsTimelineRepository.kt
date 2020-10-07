package com.twidere.twiderex.repository.timeline

import com.twidere.services.microblog.TimelineService
import com.twidere.services.microblog.model.IStatus
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.model.TimelineType
import com.twidere.twiderex.model.UserKey

class MentionsTimelineRepository(
    userKey: UserKey,
    val service: TimelineService,
    database: AppDatabase,
    count: Int = 20
) :
    TimelineRepository(userKey, database, count) {
    override val type: TimelineType
        get() = TimelineType.Mentions

    override suspend fun loadData(count: Int, since_id: String?, max_id: String?): List<IStatus> {
        return service.mentionsTimeline(count, since_id, max_id)
    }
}