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
package com.twidere.twiderex.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.twidere.services.microblog.ListsService
import com.twidere.twiderex.defaultLoadCount
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.paging.crud.MemoryCachePagingSource
import com.twidere.twiderex.paging.crud.PagingMemoryCache
import com.twidere.twiderex.paging.mediator.list.ListsMembersMediator
import com.twidere.twiderex.paging.source.ListsSubscribersPagingSource
import kotlinx.coroutines.flow.Flow

class ListsUsersRepository(private val membersCaches: MutableMap<String, PagingMemoryCache<UiUser>> = mutableMapOf()) {

    @OptIn(ExperimentalPagingApi::class)
    fun fetchMembers(
        accountKey: MicroBlogKey,
        service: ListsService,
        listId: String
    ): Flow<PagingData<UiUser>> {
        val cache = membersCaches[listId] ?: PagingMemoryCache()
        membersCaches[listId] = cache
        return Pager(
            config = PagingConfig(
                pageSize = defaultLoadCount,
                enablePlaceholders = false,
            ),
            remoteMediator = ListsMembersMediator(
                cache,
                accountKey,
                service,
                listId
            )
        ) {
            MemoryCachePagingSource(cache)
        }.flow
    }

    fun fetchSubscribers(
        accountKey: MicroBlogKey,
        service: ListsService,
        listId: String
    ): Flow<PagingData<UiUser>> {
        return Pager(
            config = PagingConfig(
                pageSize = defaultLoadCount,
                enablePlaceholders = false,
            ),
        ) {
            ListsSubscribersPagingSource(
                userKey = accountKey,
                service = service,
                listId = listId
            )
        }.flow
    }

    suspend fun addMember(
        service: ListsService,
        listId: String,
        user: UiUser,
    ) {
        service.addMember(
            listId = listId,
            userId = user.id,
            screenName = user.screenName
        )
        val cache = membersCaches[listId] ?: PagingMemoryCache()
        membersCaches[listId] = cache
        cache.insert(listOf(user))
    }

    suspend fun removeMember(
        service: ListsService,
        listId: String,
        user: UiUser,
    ) {
        membersCaches[listId]?.delete(user)
        service.removeMember(
            listId = listId,
            userId = user.id,
            screenName = user.screenName
        )
    }
}
