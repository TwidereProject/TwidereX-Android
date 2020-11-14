package com.twidere.twiderex.repository.timeline.user

import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.twidere.services.microblog.TimelineService
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.model.UserTimelineType
import com.twidere.twiderex.model.UserKey
import com.twidere.twiderex.paging.mediator.user.UserMediaMediator
import com.twidere.twiderex.paging.mediator.user.UserTimelineMediatorBase

class UserMediaTimelineRepository @AssistedInject constructor(
    private val database: AppDatabase,
    @Assisted private val userKey: UserKey,
    @Assisted private val service: TimelineService,
) : UserTimelineRepositoryBase(database, userKey) {

    @AssistedInject.Factory
    interface AssistedFactory {
        fun create(userKey: UserKey, service: TimelineService): UserMediaTimelineRepository
    }

    override val timelineType: UserTimelineType
        get() = UserTimelineType.Media

    override fun createRemoteMediator(screenName: String): UserTimelineMediatorBase {
        return UserMediaMediator(screenName, database, userKey, service)
    }
}