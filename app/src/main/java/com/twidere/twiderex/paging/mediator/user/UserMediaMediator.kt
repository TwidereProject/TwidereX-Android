package com.twidere.twiderex.paging.mediator.user

import androidx.paging.ExperimentalPagingApi
import com.twidere.services.microblog.TimelineService
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.mapper.toDbTimeline
import com.twidere.twiderex.db.model.DbUserTimeline.Companion.toUserDbTimeline
import com.twidere.twiderex.db.model.DbUserTimelineWithStatus
import com.twidere.twiderex.db.model.TimelineType
import com.twidere.twiderex.db.model.UserTimelineType
import com.twidere.twiderex.model.UserKey

@OptIn(ExperimentalPagingApi::class)
class UserMediaMediator(
    private val screenName: String,
    database: AppDatabase,
    private val userKey: UserKey,
    private val service: TimelineService,
) : UserTimelineMediatorBase(database) {
    override suspend fun load(pageSize: Int, max_id: String?): List<DbUserTimelineWithStatus> {
        return service.userTimeline(
            screen_name = screenName,
            count = pageSize,
            max_id = max_id,
            exclude_replies = true
        ).map {
            it.toDbTimeline(userKey, TimelineType.Custom)
                .toUserDbTimeline(screenName, UserTimelineType.Media)
        }.filter { it.status.status.data.hasMedia }
    }
    override suspend fun clearData(database: AppDatabase) {
        database.userTimelineDao().clearAll(screenName, timelineType = UserTimelineType.Media, userKey = userKey)
    }
}