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
package com.twidere.twiderex.repository.timeline.user

import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.twidere.services.microblog.TimelineService
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.model.UserKey
import com.twidere.twiderex.paging.mediator.PagingTimelineMediatorBase
import com.twidere.twiderex.paging.mediator.user.UserFavouriteMediator
import com.twidere.twiderex.repository.timeline.PagingTimelineRepositoryBase

class UserFavouriteTimelineRepository @AssistedInject constructor(
    private val database: AppDatabase,
    @Assisted private val userKey: UserKey,
    @Assisted private val service: TimelineService,
) : PagingTimelineRepositoryBase(database, userKey) {
    @AssistedInject.Factory
    interface AssistedFactory {
        fun create(userKey: UserKey, service: TimelineService): UserFavouriteTimelineRepository
    }

    override fun createRemoteMediator(screenName: String): PagingTimelineMediatorBase {
        return UserFavouriteMediator(screenName, database, userKey, service)
    }
}
