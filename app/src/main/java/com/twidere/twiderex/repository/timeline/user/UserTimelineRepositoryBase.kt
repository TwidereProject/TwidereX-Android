package com.twidere.twiderex.repository.timeline.user

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.model.UserTimelineType
import com.twidere.twiderex.model.UserKey
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.model.ui.UiStatus.Companion.toUi
import com.twidere.twiderex.paging.mediator.user.UserTimelineMediatorBase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

abstract class UserTimelineRepositoryBase constructor(
    private val database: AppDatabase,
    private val userKey: UserKey,
) {
    abstract val timelineType: UserTimelineType
    abstract fun createRemoteMediator(screenName: String): UserTimelineMediatorBase

    fun getPager(screenName: String): Flow<PagingData<UiStatus>> {
        return Pager(
            config = PagingConfig(pageSize = 50),
            remoteMediator = createRemoteMediator(screenName)
        ) {
            database.userTimelineDao().getPagingSource(
                screenName = screenName,
                timelineType = timelineType,
                userKey = userKey,
            )
        }.flow.map { it.map { it.status.toUi(userKey) } }
    }
}
