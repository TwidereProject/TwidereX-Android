/*
 *  Twidere X
 *
 *  Copyright (C) 2020-2021 Tlaster <tlaster@outlook.com>
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
import androidx.paging.map
import com.twidere.services.microblog.DirectMessageService
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.db.model.DbDirectMessageConversationWithMessage
import com.twidere.twiderex.db.model.DbUser
import com.twidere.twiderex.defaultLoadCount
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiDMConversationWithLatestMessage
import com.twidere.twiderex.model.ui.UiDMConversationWithLatestMessage.Companion.toUi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalPagingApi::class)
class DMConversationMediator(
    database: CacheDatabase,
    service: DirectMessageService,
    accountKey: MicroBlogKey,
    userLookup: suspend (userKey: MicroBlogKey) -> DbUser
) : BaseDirectMessageMediator<Int, DbDirectMessageConversationWithMessage>(database, service, accountKey, userLookup) {
    override fun reverse() = false

    fun pager(
        config: PagingConfig = PagingConfig(
            pageSize = defaultLoadCount,
            enablePlaceholders = false
        ),
        pagingSourceFactory: () -> PagingSource<Int, DbDirectMessageConversationWithMessage> = {
            database.directMessageConversationDao().getPagingSource(accountKey = accountKey)
        }
    ): Pager<Int, DbDirectMessageConversationWithMessage> {
        return Pager(
            config = config,
            remoteMediator = this,
            pagingSourceFactory = pagingSourceFactory,
        )
    }

    companion object {
        fun Pager<Int, DbDirectMessageConversationWithMessage>.toUi(): Flow<PagingData<UiDMConversationWithLatestMessage>> {
            return this.flow.map { pagingData ->
                pagingData.map {
                    it.toUi()
                }
            }
        }
    }
}
