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
package com.twidere.twiderex.paging.mediator.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.RemoteMediator
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.defaultLoadCount
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.paging.PagingTimeLineWithStatus

@OptIn(ExperimentalPagingApi::class)
abstract class PagingMediator(
    val database: CacheDatabase,
    val accountKey: MicroBlogKey,
) : RemoteMediator<Int, PagingTimeLineWithStatus>() {
    abstract val pagingKey: String

    fun pager(
        config: PagingConfig,
        pagingSourceFactory: () -> PagingSource<Int, PagingTimeLineWithStatus> = defaultPagingSourceFactory
    ): Pager<Int, PagingTimeLineWithStatus> {
        return Pager(
            config = config,
            remoteMediator = this,
            pagingSourceFactory = pagingSourceFactory,
        )
    }

    fun pager(
        pageSize: Int = defaultLoadCount,
        pagingSourceFactory: () -> PagingSource<Int, PagingTimeLineWithStatus> = defaultPagingSourceFactory
    ) = pager(
        config = PagingConfig(
            pageSize = pageSize,
            enablePlaceholders = true,
        ),
        pagingSourceFactory = pagingSourceFactory
    )

    private val defaultPagingSourceFactory: () -> PagingSource<Int, PagingTimeLineWithStatus>
        get() = {
            database.pagingTimelineDao().getPagingSource(pagingKey = pagingKey, accountKey = accountKey)
        }
}
