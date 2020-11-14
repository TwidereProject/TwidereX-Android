package com.twidere.twiderex.repository.timeline

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.model.UserKey
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.model.ui.UiStatus.Companion.toUi
import com.twidere.twiderex.paging.mediator.PagingTimelineMediatorBase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

abstract class PagingTimelineRepositoryBase constructor(
    private val database: AppDatabase,
    private val userKey: UserKey,
) {
    abstract fun createRemoteMediator(screenName: String): PagingTimelineMediatorBase

    fun getPager(screenName: String): Flow<PagingData<UiStatus>> {
        val remoteMediator = createRemoteMediator(screenName)
        return Pager(
            config = PagingConfig(pageSize = 50),
            remoteMediator = remoteMediator
        ) {
            database.pagingTimelineDao().getPagingSource(
                pagingKey = remoteMediator.pagingKey,
                userKey = userKey,
            )
        }.flow.map { it.map { it.status.toUi(userKey) } }
    }
}
