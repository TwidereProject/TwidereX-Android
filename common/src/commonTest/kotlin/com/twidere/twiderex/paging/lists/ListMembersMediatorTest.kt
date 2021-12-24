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
package com.twidere.twiderex.paging.lists

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.twidere.twiderex.mock.service.MockListsService
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.paging.crud.PagingMemoryCache
import com.twidere.twiderex.paging.mediator.list.ListsMembersMediator
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class ListMembersMediatorTest {

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun refresh_LoadReturnsSuccessResultWhenSuccess() = runBlocking {
        val userKey = MicroBlogKey.twitter("test")
        val mediator = ListsMembersMediator(
            memoryCache = PagingMemoryCache(),
            userKey = userKey,
            service = MockListsService(),
            listId = "list id"
        )
        val pagingState = PagingState<Int, UiUser>(emptyList(), config = PagingConfig(20), anchorPosition = 0, leadingPlaceholderCount = 0)
        val result = mediator.load(LoadType.REFRESH, pagingState)
        assert(result is RemoteMediator.MediatorResult.Success)
        assert(!(result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun refresh_LoadReturnsErrorResultWhenErrorOccurs() = runBlocking {
        val userKey = MicroBlogKey.twitter("test")
        val mediator = ListsMembersMediator(
            memoryCache = PagingMemoryCache(),
            userKey = userKey,
            service = MockListsService().apply { errorMsg = "throw test error" },
            listId = "list id"
        )
        val pagingState = PagingState<Int, UiUser>(emptyList(), config = PagingConfig(20), anchorPosition = 0, leadingPlaceholderCount = 0)
        val result = mediator.load(LoadType.REFRESH, pagingState)
        assert(result is RemoteMediator.MediatorResult.Error)
    }
}
