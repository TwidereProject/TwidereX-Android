package com.twidere.twiderex.repository

import com.twidere.services.microblog.LookupService
import com.twidere.services.microblog.RelationshipService
import com.twidere.services.microblog.TimelineService
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.mapper.toDbTimeline
import com.twidere.twiderex.db.mapper.toDbUser
import com.twidere.twiderex.db.model.TimelineType
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.model.ui.UiStatus.Companion.toUi
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.model.ui.UiUser.Companion.toUi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val repository: AccountRepository,
    private val database: AppDatabase,
) {
    private fun getLookupService() =
        repository.getCurrentAccount().let { account ->
            account.service.let {
                it as? LookupService
            }
        }

    private fun getRelationshipService() =
        repository.getCurrentAccount().let { account ->
            account.service.let {
                it as? RelationshipService
            }
        }

    private fun getTimelineService() =
        repository.getCurrentAccount().let { account ->
            account.service.let {
                it as? TimelineService
            }
        }

    suspend fun lookupUser(id: String): UiUser? {
        val user = getLookupService()?.lookupUser(id)?.toDbUser()
        if (user != null) {
            val db = database.userDao().findWithUserId(user.userId)
            if (db != null) {
                user._id = db._id
                database.userDao().update(user)
            }
        }
        return user?.toUi()
    }

    suspend fun showRelationship(id: String) = getRelationshipService()?.showRelationship(id)

    suspend fun loadTimelineBetween(
        id: String,
        max_id: String? = null,
        since_id: String? = null,
    ): List<UiStatus> {
        val timelineService = getTimelineService() ?: return emptyList()
        val result = timelineService.userTimeline(id, count = 100, max_id = max_id, since_id = since_id)
        val userKey = repository.getCurrentAccount().key
        val timeline = result.map { it.toDbTimeline(userKey, TimelineType.User) }
        return timeline.map { it.toUi() }
    }
}