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
package com.twidere.twiderex.db.dao

import androidx.paging.PagingSource
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.paging.PagingTimeLine
import com.twidere.twiderex.model.paging.PagingTimeLineWithStatus

interface PagingTimelineDao {
    fun getPagingSource(
        pagingKey: String,
        accountKey: MicroBlogKey
    ): PagingSource<Int, PagingTimeLineWithStatus>

    suspend fun clearAll(pagingKey: String, accountKey: MicroBlogKey)
    suspend fun getLatest(pagingKey: String, accountKey: MicroBlogKey): PagingTimeLineWithStatus?
    suspend fun findWithStatusKey(maxStatusKey: MicroBlogKey, accountKey: MicroBlogKey): PagingTimeLine?
    suspend fun insertAll(listOf: List<PagingTimeLine>)
    suspend fun delete(statusKey: MicroBlogKey)
}
