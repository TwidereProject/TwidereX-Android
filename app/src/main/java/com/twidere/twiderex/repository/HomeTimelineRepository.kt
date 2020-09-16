package com.twidere.twiderex.repository

import com.twidere.services.microblog.HomeTimelineService
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.mapper.toDbTimeline
import com.twidere.twiderex.db.model.DbTimelineWithStatus
import com.twidere.twiderex.model.UserKey

class HomeTimelineRepository(
    private val userKey: UserKey,
    private val service: HomeTimelineService,
    private val database: AppDatabase,
    private val count: Int = 20,
) {
    suspend fun loadInitial(): List<DbTimelineWithStatus> {
        return database.timelineDao().getAll()
    }

    val liveData by lazy {
        database.timelineDao().getAllWithLiveData()
    }

    suspend fun refresh(since_id: String?): List<DbTimelineWithStatus> {
        return loadBetween(since_id = since_id)
    }

    suspend fun loadBetween(
        max_id: String? = null,
        since_id: String? = null,
        withGap: Boolean = true,
    ): List<DbTimelineWithStatus> {
        val result = service.homeTimeline(count = count, since_id = since_id, max_id = max_id)
        val timeline = result.map { it.toDbTimeline(userKey) }
        if (withGap) {
            timeline.lastOrNull()?.timeline?.isGap = result.size >= count
        }
        database.statusDao().insertAll(
            timeline
                .map { listOf(it.status, it.quote, it.retweet) }
                .flatten()
                .filterNotNull()
        )
        database.timelineDao().insertAll(timeline.map { it.timeline })
        return timeline
    }

    suspend fun loadMore(max_id: String): List<DbTimelineWithStatus> {
        return loadBetween(max_id = max_id, withGap = false)
    }
}