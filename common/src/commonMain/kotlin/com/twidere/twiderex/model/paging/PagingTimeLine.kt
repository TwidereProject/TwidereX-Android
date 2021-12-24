/*
 *  Twidere X
 *
 *  Copyright (C) TwidereProject and Contributors
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
package com.twidere.twiderex.model.paging

import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiStatus

data class PagingTimeLine(
    val accountKey: MicroBlogKey,
    val pagingKey: String,
    val statusKey: MicroBlogKey,
    val timestamp: Long,
    val sortId: Long,
    var isGap: Boolean,
)

data class PagingTimeLineWithStatus(
    val timeline: PagingTimeLine,
    val status: UiStatus,
)

enum class UserTimelineType {
    Status,
    Media,
    Favourite
}

fun UserTimelineType.pagingKey(accountKey: MicroBlogKey) = "user:$accountKey:$this"

suspend fun List<PagingTimeLineWithStatus>.saveToDb(
    database: CacheDatabase,
) {
    this.groupBy { it.timeline.accountKey }.forEach {
        database.statusDao().insertAll(it.value.map { it.status }, it.key)
    }
    this.map { it.timeline }.let {
        database.pagingTimelineDao().insertAll(it)
    }
}
