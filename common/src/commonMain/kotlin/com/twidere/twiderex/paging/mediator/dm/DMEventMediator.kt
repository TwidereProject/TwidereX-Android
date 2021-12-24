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
package com.twidere.twiderex.paging.mediator.dm

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.twidere.services.microblog.model.IDirectMessage
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.defaultLoadCount
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiDMEvent
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalPagingApi::class)
class DMEventMediator(
    private val conversationKey: MicroBlogKey,
    database: CacheDatabase,
    accountKey: MicroBlogKey,
    realFetch: suspend (key: String?) -> List<IDirectMessage>
) : BaseDirectMessageMediator<Int, UiDMEvent>(database, accountKey, realFetch) {

    override fun reverse() = true

    fun pager(
        config: PagingConfig = PagingConfig(
            pageSize = defaultLoadCount,
            enablePlaceholders = false
        ),
        pagingSourceFactory: () -> PagingSource<Int, UiDMEvent> = {
            database.directMessageDao()
                .getPagingSource(accountKey = accountKey, conversationKey = conversationKey)
        }
    ): Pager<Int, UiDMEvent> {
        return Pager(
            config = config,
            remoteMediator = this,
            pagingSourceFactory = pagingSourceFactory,
        )
    }

    companion object {
        fun Pager<Int, UiDMEvent>.toUi(): Flow<PagingData<UiDMEvent>> {
            return this.flow
        }
    }
}
