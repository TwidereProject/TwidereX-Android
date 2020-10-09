package com.twidere.twiderex.repository

import com.twidere.services.microblog.LookupService
import com.twidere.services.microblog.RelationshipService
import com.twidere.services.microblog.TimelineService
import com.twidere.twiderex.db.mapper.toDbTimeline
import com.twidere.twiderex.db.mapper.toDbUser
import com.twidere.twiderex.db.model.TimelineType
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.model.ui.UiStatus.Companion.toUi
import com.twidere.twiderex.model.ui.UiUser.Companion.toUi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val repository: AccountRepository,
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

    suspend fun lookupUser(id: String) = getLookupService()?.lookupUser(id)?.toDbUser()?.toUi()

    suspend fun showRelationship(id: String) = getRelationshipService()?.showRelationship(id)

    suspend fun loadBetween(
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