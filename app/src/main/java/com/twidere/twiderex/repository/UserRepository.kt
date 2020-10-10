package com.twidere.twiderex.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.twidere.services.microblog.LookupService
import com.twidere.services.microblog.RelationshipService
import com.twidere.services.microblog.TimelineService
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.mapper.toDbTimeline
import com.twidere.twiderex.db.mapper.toDbUser
import com.twidere.twiderex.db.model.TimelineType
import com.twidere.twiderex.model.UserKey
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
            val name = user.screenName
            val key = UserKey(name, "twitter.com")
            val account = repository.findByAccountKey(key)
            if (account != null) {
                val detail = repository.getAccountDetails(account)
                detail.user = user
                repository.updateAccount(detail)
            }
        }
        return user?.toUi()
    }

    suspend fun showRelationship(id: String) = getRelationshipService()?.showRelationship(id)

    fun getUserTimelineLiveData(): LiveData<List<UiStatus>> {
        return database.timelineDao().getAllWithLiveData(repository.getCurrentAccount().key, TimelineType.User).map { list ->
            list.map { status ->
                status.toUi()
            }
        }
    }

    suspend fun loadTimelineBetween(
        id: String,
        max_id: String? = null,
        since_id: String? = null,
    ): List<UiStatus> {
        val timelineService = getTimelineService() ?: return emptyList()
        val result = timelineService.userTimeline(id, count = 100, max_id = max_id, since_id = since_id)
        val userKey = repository.getCurrentAccount().key
        val timeline = result.map { it.toDbTimeline(userKey, TimelineType.User) }

        val data = timeline
            .map { listOf(it.status, it.quote, it.retweet) }
            .flatten()
            .filterNotNull()
        database.userDao().insertAll(data.map { it.user })
        database.mediaDao().insertAll(data.map { it.media }.flatten())
        database.statusDao().insertAll(data.map { it.status })
        database.timelineDao().insertAll(timeline.map { it.timeline })

        return timeline.map { it.toUi() }
    }
}