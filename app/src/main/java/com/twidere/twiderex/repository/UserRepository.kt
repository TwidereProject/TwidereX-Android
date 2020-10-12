/*
 *  TwidereX
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of TwidereX.
 * 
 *  TwidereX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  TwidereX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with TwidereX. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.twidere.services.microblog.LookupService
import com.twidere.services.microblog.RelationshipService
import com.twidere.services.microblog.TimelineService
import com.twidere.services.microblog.model.IStatus
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.mapper.toDbTimeline
import com.twidere.twiderex.db.mapper.toDbUser
import com.twidere.twiderex.db.model.DbTimelineWithStatus
import com.twidere.twiderex.db.model.TimelineType
import com.twidere.twiderex.defaultLoadCount
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
                database.userDao().insertAll(listOf(user))
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

    suspend fun getPinnedStatus(user: UiUser): UiStatus? {
        val service = getLookupService() ?: return null
        val result = service.userPinnedStatus(user.id) ?: return null
        val userKey = repository.getCurrentAccount().key
        val timeline = result.toDbTimeline(userKey = userKey, timelineType = TimelineType.User)
        saveTimeline(listOf(timeline))
        return timeline.toUi()
    }

    suspend fun loadTimelineBetween(
        id: String,
        max_id: String? = null,
        since_id: String? = null,
    ): List<UiStatus> {
        return load {
            it.userTimeline(id, count = defaultLoadCount, max_id = max_id, since_id = since_id)
        }
    }

    suspend fun loadFavouriteTimelineBetween(
        id: String,
        max_id: String? = null,
        since_id: String? = null,
    ): List<UiStatus> {
        return load {
            it.favorites(id, count = defaultLoadCount, max_id = max_id, since_id = since_id)
        }
    }

    private suspend fun load(
        func: suspend (TimelineService) -> List<IStatus>
    ): List<UiStatus> {
        val timelineService = getTimelineService() ?: return emptyList()
        val result = func.invoke(timelineService)
        val userKey = repository.getCurrentAccount().key
        val timeline = result.map { it.toDbTimeline(userKey, TimelineType.User) }
        saveTimeline(timeline)
        return timeline.map { it.toUi() }
    }

    private suspend fun saveTimeline(timeline: List<DbTimelineWithStatus>) {
        val data = timeline
            .map { listOf(it.status, it.quote, it.retweet) }
            .flatten()
            .filterNotNull()
        database.userDao().insertAll(data.map { it.user })
        database.mediaDao().insertAll(data.map { it.media }.flatten())
        database.statusDao().insertAll(data.map { it.status })
        database.timelineDao().insertAll(timeline.map { it.timeline })
    }
}
