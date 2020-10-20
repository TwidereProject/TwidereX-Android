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

import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.twidere.services.microblog.LookupService
import com.twidere.services.microblog.RelationshipService
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.db.mapper.toDbTimeline
import com.twidere.twiderex.db.mapper.toDbUser
import com.twidere.twiderex.db.model.DbUser
import com.twidere.twiderex.db.model.TimelineType
import com.twidere.twiderex.model.UserKey
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.model.ui.UiStatus.Companion.toUi
import com.twidere.twiderex.model.ui.UiUser
import javax.inject.Singleton

@Singleton
class UserRepository @AssistedInject constructor(
    private val database: AppDatabase,
    private val cache: CacheDatabase,
    @Assisted private val lookupService: LookupService,
    @Assisted private val relationshipService: RelationshipService,
) {

    @AssistedInject.Factory
    interface AssistedFactory {
        fun create(
            lookupService: LookupService,
            relationshipService: RelationshipService,
        ): UserRepository
    }

    suspend fun lookupUser(id: String): DbUser? {
        val user = lookupService.lookupUser(id).toDbUser()
        saveUser(user)
        return user
    }

    private suspend fun saveUser(user: DbUser) {
        cache.userDao().insertAll(listOf(user))
        database.userDao().insertAll(listOf(user))
    }

    suspend fun showRelationship(id: String) = relationshipService.showRelationship(id)

    suspend fun getPinnedStatus(user: UiUser): UiStatus? {
        val result = lookupService.userPinnedStatus(user.id) ?: return null
        val userKey = UserKey.Empty
        val timeline = result.toDbTimeline(userKey = userKey, timelineType = TimelineType.User)
        return timeline.toUi()
    }
}
