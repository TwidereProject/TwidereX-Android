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
package com.twidere.twiderex.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.twidere.services.microblog.ListsService
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.defaultLoadCount
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.paging.crud.MemoryCachePagingSource
import com.twidere.twiderex.paging.crud.PagingMemoryCache
import com.twidere.twiderex.paging.mediator.list.ListsMembersMediator
import com.twidere.twiderex.paging.source.ListsSubscribersPagingSource
import kotlinx.coroutines.flow.Flow

class ListUsersRepository(private val database: CacheDatabase) {
    private val membersCaches = mutableMapOf<String, PagingMemoryCache<UiUser>>()

    @OptIn(ExperimentalPagingApi::class)
    fun fetchMembers(account: AccountDetails, listId: String): Flow<PagingData<UiUser>> {
        val cache = membersCaches[listId] ?: PagingMemoryCache()
        membersCaches[listId] = cache
        return Pager(
            config = PagingConfig(
                pageSize = defaultLoadCount,
                enablePlaceholders = false,
            ),
            remoteMediator = ListsMembersMediator(
                cache,
                account.accountKey,
                account.service as ListsService,
                listId
            )
        ) {
            MemoryCachePagingSource(cache)
        }.flow
    }

    fun fetchSubscribers(account: AccountDetails, listId: String): Flow<PagingData<UiUser>> {
        return Pager(
            config = PagingConfig(
                pageSize = defaultLoadCount,
                enablePlaceholders = false,
            ),
        ) {
            ListsSubscribersPagingSource(
                userKey = account.accountKey,
                service = account.service as ListsService,
                listId = listId
            )
        }.flow
    }

    suspend fun addMember(
        account: AccountDetails,
        listId: String,
        user: UiUser,
    ) {
        (account.service as ListsService).addMember(
            listId = listId,
            userId = user.id,
            screenName = user.screenName
        )
        membersCaches[listId]?.insert(listOf(user))
    }

    suspend fun removeMember(
        account: AccountDetails,
        listId: String,
        user: UiUser,
    ) {
        membersCaches[listId]?.delete(user)
        (account.service as ListsService).removeMember(
            listId = listId,
            userId = user.id,
            screenName = user.screenName
        )
    }

    fun insertCache(users: List<UiUser>, listId: String) {
        membersCaches[listId]?.insert(users, 0)
    }
}
