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

import com.twidere.twiderex.mock.MockCenter
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.MicroBlogKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class ListsRepositoryTest {
    private lateinit var repository: ListsRepository

    private var mockDatabase = MockCenter.mockCacheDatabase()

    private var mockListsService = MockCenter.mockListsService()

    @Mock
    private lateinit var mockAccountDetails: AccountDetails

    private lateinit var mocks: AutoCloseable

    @Before
    fun setUp() {
        mocks = MockitoAnnotations.openMocks(this)
        repository = ListsRepository(mockDatabase)
        whenever(mockAccountDetails.accountKey).thenReturn(MicroBlogKey.twitter("123"))
        whenever(mockAccountDetails.service).thenReturn(mockListsService)
    }

    @After
    fun tearDown() {
        mocks.close()
    }

    @Test
    fun createList_saveToDatabase() = runBlocking {
        // check if the repository save result to db after create request success
        val originList = mockDatabase.listsDao().findAll()
        repository.createLists(mockAccountDetails, "create lists")
        val createList = mockDatabase.listsDao().findAll()
        Assert.assertNotNull(createList)
        Assert.assertEquals(1, (createList?.size ?: 0) - (originList?.size ?: 0))
        Assert.assertEquals("create lists", createList!![0].title)
    }

    @Test
    fun updateList_updateToDatabase() = runBlocking {
        val originData = repository.createLists(mockAccountDetails, "before title", description = "before title", mode = "public")
        repository.updateLists(
            mockAccountDetails,
            originData.id,
            "after title",
            description = "after desc",
            mode = "private"
        )
        val updateData = mockDatabase.listsDao()
            .findWithListKey(originData.listKey, accountKey = mockAccountDetails.accountKey)
        Assert.assertNotNull(updateData)
        Assert.assertEquals("after title", updateData?.title)
        Assert.assertEquals("after desc", updateData?.description)
        Assert.assertEquals("private", updateData?.mode)
    }

    @Test
    fun deleteList_deleteInDatabase() = runBlocking {
        val originData = repository.createLists(mockAccountDetails, "delete")
        val originList = mockDatabase.listsDao().findAll()
        repository.deleteLists(
            mockAccountDetails,
            originData.listKey,
            originData.id
        )
        val deleteList = mockDatabase.listsDao().findAll()
        Assert.assertEquals(1, originList!!.size - deleteList!!.size)
    }

    @Test
    fun unsubscribeList_updateFollowingInDatabase() = runBlocking {
        val originData = repository.createLists(mockAccountDetails, "delete")
        repository.unsubscribeLists(
            mockAccountDetails,
            originData.listKey,
        )
        val unsubscribeData = mockDatabase.listsDao().findWithListKey(originData.listKey, mockAccountDetails.accountKey)
        Assert.assertEquals(false, unsubscribeData?.isFollowed)
    }

    @Test
    fun subscribeList_updateFollowingInDatabase() = runBlocking {
        val originData = repository.createLists(mockAccountDetails, "delete")
        repository.subscribeLists(
            mockAccountDetails,
            originData.listKey,
        )
        val unsubscribeData = mockDatabase.listsDao().findWithListKey(originData.listKey, mockAccountDetails.accountKey)
        Assert.assertEquals(true, unsubscribeData?.isFollowed)
    }
}
