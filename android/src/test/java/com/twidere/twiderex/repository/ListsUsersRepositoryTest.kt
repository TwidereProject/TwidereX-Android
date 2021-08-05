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

import com.twidere.services.mastodon.model.Account
import com.twidere.twiderex.db.mapper.toDbUser
import com.twidere.twiderex.mock.MockCenter
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.transform.toUi
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.paging.crud.PagingMemoryCache
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class ListsUsersRepositoryTest {
    private lateinit var repository: ListsUsersRepository

    private var mockListsService = MockCenter.mockListsService()

    @Mock
    private lateinit var mockAccountDetails: AccountDetails

    private lateinit var mocks: AutoCloseable

    private val memoryCache = mutableMapOf<String, PagingMemoryCache<UiUser>>()

    @Before
    fun setUp() {
        mocks = MockitoAnnotations.openMocks(this)
        repository = ListsUsersRepository(memoryCache)
        whenever(mockAccountDetails.accountKey).thenReturn(MicroBlogKey.twitter("123"))
        whenever(mockAccountDetails.service).thenReturn(mockListsService)
    }

    @After
    fun tearDown() {
        mocks.close()
    }

    @Test
    fun addMembers_updateToMemoryCacheWhenSuccess() = runBlocking {
        repository.addMember(mockAccountDetails, "fake listId", Account(id = "1", displayName = "x", username = "x", acct = "x").toDbUser(mockAccountDetails.accountKey).toUi())
        Assert.assertEquals("1", memoryCache["fake listId"]?.find(0, 1)?.get(0)?.id)
    }

    @Test
    fun removeMembers_updateToMemoryCacheWhenSuccess() = runBlocking {
        val user = Account(id = "1", displayName = "x", username = "x", acct = "x").toDbUser(mockAccountDetails.accountKey).toUi()
        repository.addMember(mockAccountDetails, "fake listId", user)
        Assert.assertEquals(true, memoryCache["fake listId"]?.find(0, 1)?.isNotEmpty())
        repository.removeMember(mockAccountDetails, "fake listId", user)
        Assert.assertEquals(true, memoryCache["fake listId"]?.find(0, 1)?.isEmpty())
    }
}
