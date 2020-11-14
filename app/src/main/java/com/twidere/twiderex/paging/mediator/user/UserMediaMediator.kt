package com.twidere.twiderex.paging.mediator.user

import androidx.paging.ExperimentalPagingApi
import com.twidere.services.microblog.TimelineService
import com.twidere.services.microblog.model.IStatus
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.model.DbPagingTimelineWithStatus
import com.twidere.twiderex.db.model.UserTimelineType
import com.twidere.twiderex.db.model.pagingKey
import com.twidere.twiderex.model.UserKey
import com.twidere.twiderex.paging.mediator.PagingTimelineMediatorBase

@OptIn(ExperimentalPagingApi::class)
class UserMediaMediator(
    private val screenName: String,
    database: AppDatabase,
    userKey: UserKey,
    private val service: TimelineService,
) : PagingTimelineMediatorBase(userKey, database) {
    override val pagingKey: String
        get() = UserTimelineType.Media.pagingKey(screenName)

    override suspend fun load(pageSize: Int, max_id: String?): List<IStatus> {
        return service.userTimeline(
            screen_name = screenName,
            count = pageSize,
            max_id = max_id,
            exclude_replies = true
        )
    }

    override fun transform(data: List<DbPagingTimelineWithStatus>): List<DbPagingTimelineWithStatus> {
        return data.filter {
            val content = it.status.retweet ?: it.status.status
            content.data.hasMedia && content.user.screenName == screenName
        }
    }
}