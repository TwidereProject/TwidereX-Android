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
package com.twidere.twiderex.repository.timeline

import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.twidere.services.microblog.TimelineService
import com.twidere.services.microblog.model.IStatus
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.db.model.TimelineType
import com.twidere.twiderex.model.UserKey
import com.twidere.twiderex.model.ui.UiUser

class UserFavouriteTimelineRepository @AssistedInject constructor(
    cache: CacheDatabase,
    database: AppDatabase,
    @Assisted userKey: UserKey,
    @Assisted private val service: TimelineService,
) : CacheUserTimelineRepository(cache, database, userKey, 20) {
    override val type: TimelineType
        get() = TimelineType.UserFavourite

    @AssistedInject.Factory
    interface AssistedFactory {
        fun create(userKey: UserKey, service: TimelineService): UserFavouriteTimelineRepository
    }

    override suspend fun loadData(
        user: UiUser,
        count: Int,
        since_id: String?,
        max_id: String?
    ): List<IStatus> {
        return service.favorites(
            user_id = user.id,
            count = count,
            since_id = since_id,
            max_id = max_id,
        )
    }
}
