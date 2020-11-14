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
class UserFavouriteMediator(
    private val screenName: String,
    database: AppDatabase,
    private val userKey: UserKey,
    private val service: TimelineService,
) : UserTimelineMediatorBase(database) {
    override suspend fun load(pageSize: Int, max_id: String?): List<DbUserTimelineWithStatus> {
        return service.favorites(
            screen_name = screenName,
            count = pageSize,
            max_id = max_id,
        ).map {
            it.toDbTimeline(userKey, TimelineType.Custom)
                .toUserDbTimeline(screenName, UserTimelineType.Favourite)
        }
    }

    override suspend fun clearData(database: AppDatabase) {
        database.userTimelineDao().clearAll(screenName, timelineType = UserTimelineType.Favourite, userKey = userKey)
    }
}