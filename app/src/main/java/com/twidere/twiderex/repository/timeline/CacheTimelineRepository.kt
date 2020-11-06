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

import androidx.lifecycle.map
import com.twidere.services.microblog.model.IStatus
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.mapper.toDbTimeline
import com.twidere.twiderex.db.model.TimelineType
import com.twidere.twiderex.db.model.saveToDb
import com.twidere.twiderex.defaultLoadCount
import com.twidere.twiderex.model.UserKey
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.model.ui.UiStatus.Companion.toUi
import com.twidere.twiderex.model.ui.UiUser

abstract class CacheUserTimelineRepository(
    private val database: AppDatabase,
    private val userKey: UserKey,
    private val count: Int = defaultLoadCount,
) {
    protected abstract val type: TimelineType

    val liveData by lazy {
        database.timelineDao().getAllWithLiveData(userKey, type).map { list ->
            list.map { status ->
                status.toUi(userKey)
            }
        }
    }

    suspend fun loadBetween(
        user: UiUser,
        max_id: String? = null,
        since_id: String? = null,
    ): List<UiStatus> {
        val result = runCatching {
            loadData(user, count = count, since_id = since_id, max_id = max_id)
        }.getOrElse {
            emptyList()
        }
        val timeline = result.map { it.toDbTimeline(userKey, type) }
        timeline.saveToDb(database)
        return timeline.map { it.toUi(userKey) }
    }

    protected abstract suspend fun loadData(
        user: UiUser,
        count: Int = this.count,
        since_id: String? = null,
        max_id: String? = null,
    ): List<IStatus>
}
