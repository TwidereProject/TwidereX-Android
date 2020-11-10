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
package com.twidere.twiderex.repository.timeline

import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.twidere.services.microblog.TimelineService
import com.twidere.services.microblog.model.IStatus
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.model.TimelineType
import com.twidere.twiderex.model.UserKey

class UserFavouriteTimelineRepository @AssistedInject constructor(
    database: AppDatabase,
    @Assisted userKey: UserKey,
    @Assisted private val service: TimelineService,
) : CacheUserTimelineRepository(database, userKey, 20) {
    override val type: TimelineType
        get() = TimelineType.UserFavourite

    @AssistedInject.Factory
    interface AssistedFactory {
        fun create(userKey: UserKey, service: TimelineService): UserFavouriteTimelineRepository
    }

    override suspend fun loadData(
        screenName: String,
        count: Int,
        since_id: String?,
        max_id: String?
    ): List<IStatus> {
        return service.favorites(
            screen_name = screenName,
            count = count,
            since_id = since_id,
            max_id = max_id,
        )
    }
}
