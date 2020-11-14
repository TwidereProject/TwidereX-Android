/*
 *  Twidere X
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of Twidere X.
 * 
 *  Twidere X is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Twidere X is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Twidere X. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.twidere.services.microblog.LookupService
import com.twidere.services.microblog.RelationshipService
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.mapper.toDbUser
import com.twidere.twiderex.db.model.DbUser
import com.twidere.twiderex.model.PlatformType
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.model.ui.UiUser.Companion.toUi
import javax.inject.Singleton

@Singleton
class UserRepository @AssistedInject constructor(
    private val database: AppDatabase,
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

    suspend fun lookupUserByName(name: String): DbUser? {
        val user = lookupService.lookupUserByName(name).toDbUser()
        saveUser(user)
        return user
    }

    fun getUserLiveData(name: String): LiveData<UiUser?> {
        // TODO: platform type
        return database.userDao().findWithScreenNameLiveData(name = name, platformType = PlatformType.Twitter).map {
            it?.toUi()
        }
    }

    private suspend fun saveUser(user: DbUser) {
        database.userDao().insertAll(listOf(user))
    }

    suspend fun showRelationship(target_screen_name: String) = relationshipService.showRelationship(target_screen_name)

//    suspend fun getPinnedStatus(user: UiUser): UiStatus? {
//        val result = lookupService.userPinnedStatus(user.id) ?: return null
//        val userKey = UserKey.Empty
//        val timeline = result.toDbTimeline(userKey = userKey, timelineType = TimelineType.User)
//        return timeline.toUi(userKey)
//    }

    suspend fun unfollow(screenName: String) {
        relationshipService.unfollow(screenName)
    }

    suspend fun follow(screenName: String) {
        relationshipService.follow(screenName)
    }
}
