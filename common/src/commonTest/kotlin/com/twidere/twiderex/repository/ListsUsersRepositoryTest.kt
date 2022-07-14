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

import com.twidere.twiderex.dataprovider.mapper.toUi
import com.twidere.twiderex.mock.model.mockIUser
import com.twidere.twiderex.mock.service.MockListsService
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.paging.crud.PagingMemoryCache
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ListsUsersRepositoryTest {

    @Test
    fun addMembers_updateToMemoryCacheWhenSuccess() = runBlocking {
        val memoryCache = mutableMapOf<String, PagingMemoryCache<UiUser>>()
        val accountKey = MicroBlogKey.twitter("test")
        val repository = ListsUsersRepository(memoryCache)
        val user = mockIUser().toUi(accountKey)
        repository.addMember(MockListsService(), "fake listId", user)
        assertEquals(user.id, memoryCache["fake listId"]?.find(0, 1)?.get(0)?.id)
    }

    @Test
    fun removeMembers_updateToMemoryCacheWhenSuccess() = runBlocking {
        val memoryCache = mutableMapOf<String, PagingMemoryCache<UiUser>>()
        val accountKey = MicroBlogKey.twitter("test")
        val repository = ListsUsersRepository(memoryCache)
        val mockListsService = MockListsService()
        val user = mockIUser().toUi(accountKey)
        repository.addMember(mockListsService, "fake listId", user)
        assertEquals(true, memoryCache["fake listId"]?.find(0, 1)?.isNotEmpty())

        repository.removeMember(mockListsService, "fake listId", user)
        assertEquals(true, memoryCache["fake listId"]?.find(0, 1)?.isEmpty())
    }
}
