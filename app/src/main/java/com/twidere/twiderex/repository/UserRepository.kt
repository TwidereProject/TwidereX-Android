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
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.db.mapper.toDbTimeline
import com.twidere.twiderex.db.mapper.toDbUser
import com.twidere.twiderex.db.model.DbTimelineWithStatus
import com.twidere.twiderex.db.model.DbUser
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
    private val cache: CacheDatabase,
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
            saveUser(user)
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

    private suspend fun saveUser(user: DbUser) {
        cache.userDao().insertAll(listOf(user))
        database.userDao().insertAll(listOf(user))
    }

    suspend fun showRelationship(id: String) = getRelationshipService()?.showRelationship(id)

    fun getUserTimelineLiveData(
        timelineType: TimelineType
    ): LiveData<List<UiStatus>> {
        return cache.timelineDao().getAllWithLiveData(repository.getCurrentAccount().key, timelineType).map { list ->
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
        return load(timelineType = TimelineType.User) {
            it.userTimeline(id, count = defaultLoadCount, max_id = max_id, since_id = since_id)
        }
    }

    suspend fun loadFavouriteTimelineBetween(
        id: String,
        max_id: String? = null,
        since_id: String? = null,
    ): List<UiStatus> {
        return load(timelineType = TimelineType.UserFavourite) {
            it.favorites(id, count = defaultLoadCount, max_id = max_id, since_id = since_id)
        }
    }

    private suspend fun load(
        timelineType: TimelineType,
        func: suspend (TimelineService) -> List<IStatus>
    ): List<UiStatus> {
        val timelineService = getTimelineService() ?: return emptyList()
        val result = func.invoke(timelineService)
        val userKey = repository.getCurrentAccount().key
        val timeline = result.map { it.toDbTimeline(userKey, timelineType) }
        saveTimeline(timeline)
        return timeline.map { it.toUi() }
    }

    private suspend fun saveTimeline(timeline: List<DbTimelineWithStatus>) {
        val data = timeline
            .map { listOf(it.status, it.quote, it.retweet) }
            .flatten()
            .filterNotNull()
        data.map { it.user }.let {
            cache.userDao().insertAll(it)
            database.userDao().update(*it.toTypedArray())
        }
        cache.mediaDao().insertAll(data.map { it.media }.flatten())
        data.map { it.status }.let {
            cache.statusDao().insertAll(it)
            database.statusDao().update(*it.toTypedArray())
        }
        timeline.map { it.timeline }.let {
            cache.timelineDao().insertAll(it)
            database.timelineDao().update(*it.toTypedArray())
        }
    }
}
