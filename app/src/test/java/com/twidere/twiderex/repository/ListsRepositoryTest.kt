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

import com.twidere.services.twitter.TwitterService
import com.twidere.services.twitter.model.TwitterList
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.db.dao.ListsDao
import com.twidere.twiderex.db.mapper.toDbList
import com.twidere.twiderex.db.model.DbList
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.MicroBlogKey
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.whenever

class ListsRepositoryTest {
    private lateinit var repository: ListsRepository

    @Mock
    private lateinit var mockDatabase: CacheDatabase

    @Mock
    private lateinit var mockListDao: ListsDao

    @Mock
    private lateinit var mockListsService: TwitterService

    @Mock
    private lateinit var mockAccountDetails: AccountDetails

    private lateinit var mocks: AutoCloseable

    @Before
    fun setUp() {
        mocks = MockitoAnnotations.openMocks(this)
        repository = ListsRepository(mockDatabase)
        whenever(mockDatabase.listsDao()).thenReturn(mockListDao)
        whenever(mockAccountDetails.accountKey).thenReturn(MicroBlogKey.twitter("123"))
        whenever(mockAccountDetails.service).thenReturn(mockListsService)
    }

    @After
    fun tearDown() {
        mocks.close()
    }

    @Test
    fun createList_saveToDatabase() {
        runBlocking {
            val listsDatabase = mutableListOf<DbList>()
            whenever(mockListsService.createList(any(), anyOrNull(), anyOrNull(), anyOrNull())).then {
                TwitterList(id = 123, idStr = "123", name = it.getArgument(0))
            }
            whenever(mockListDao.insertAll(any())).then {
                listsDatabase.addAll(it.getArgument(0))
            }
            // check if the repository save result to db after create request success
            assert(listsDatabase.isEmpty())
            repository.createLists(mockAccountDetails, "create lists")
            assert(listsDatabase.isNotEmpty())
            Assert.assertEquals("create lists", listsDatabase[0].title)
        }
    }

    @Test
    fun updateList_updateToDatabase() {
        runBlocking {
            val listsDatabase = mutableListOf(TwitterList(idStr = "123", name = "before update"))
            whenever(
                mockListsService.updateList(
                    any(),
                    anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()
                )
            ).then {
                TwitterList(idStr = it.getArgument(0), name = it.getArgument(1))
            }
            whenever(mockListDao.findWithListKey(any(), any())).then {
                listsDatabase[0].toDbList(accountKey = mockAccountDetails.accountKey)
            }
            whenever(mockListDao.update(any())).then {
                listsDatabase.replaceAll { list ->
                    val input = (it.getArgument(0) as List<DbList>)[0]
                    if (list.idStr == input.listId) {
                        TwitterList(idStr = input.listId, name = input.title)
                    } else {
                        list
                    }
                }
            }
            // check if the repository save result to db after update request success
            Assert.assertEquals("before update", listsDatabase[0].name)
            repository.updateLists(mockAccountDetails, "123", "after update")
            assert(listsDatabase.isNotEmpty())
            Assert.assertEquals("after update", listsDatabase[0].name)
        }
    }

    @Test
    fun deleteList_deleteInDatabase() {
        runBlocking {
            val listsDatabase = mutableListOf(TwitterList(idStr = "123", name = "waiting for delete"))
            // note that whenever will cause null pointer exception
            // when mock suspend method that returns Basic data type such as boolean
            `when`(mockListsService.destroyList(any())).thenReturn(Unit)
            whenever(mockListDao.findWithListKey(any(), any())).thenReturn(
                TwitterList(idStr = "123").toDbList(MicroBlogKey.twitter("123"))
            )
            whenever(mockListDao.delete(any())).then {
                listsDatabase.removeIf { list ->
                    list.idStr == (it.getArgument(0) as List<DbList>)[0].listId
                }
            }
            // check if the repository delete result in db after delete request success
            assert(listsDatabase.isNotEmpty())
            repository.deleteLists(mockAccountDetails, MicroBlogKey.twitter("123"), "123")
            assert(listsDatabase.isEmpty())
        }
    }
}
