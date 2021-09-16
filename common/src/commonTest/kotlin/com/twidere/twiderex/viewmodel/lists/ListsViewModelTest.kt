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
package com.twidere.twiderex.viewmodel.lists

import androidx.paging.PagingData
import com.twidere.twiderex.MainThreadTestBase
import com.twidere.twiderex.mock.paging.collectDataForTest
import com.twidere.twiderex.mock.service.MockListsService
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.AmUser
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiList
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.ListsRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals

internal class ListsViewModelTest : MainThreadTestBase() {

    @MockK
    private lateinit var mockRepository: ListsRepository

    @MockK
    private lateinit var mockAccountRepository: AccountRepository

    @MockK
    private lateinit var mockAccount: AccountDetails

    @MockK
    private lateinit var mockUser: AmUser

    @MockK
    private lateinit var ownerList: UiList

    @MockK
    private lateinit var subscribeList: UiList

    private lateinit var viewModel: ListsViewModel

    override fun setUp() {
        super.setUp()
        MockKAnnotations.init(this, relaxUnitFun = true)
        every { mockRepository.fetchLists(any(), any()) }.returns(
            flowOf(
                PagingData.from(
                    listOf(
                        ownerList,
                        subscribeList
                    )
                )
            )
        )
        every { mockAccountRepository.activeAccount }.returns(flowOf(mockAccount))
        every { ownerList.isOwner(any()) }.returns(true)
        every { subscribeList.isOwner(any()) }.returns(false)
        every { ownerList.title }.returns("owner")
        every { subscribeList.title }.returns("subscribe")
        every { subscribeList.isFollowed }.returns(true)
        every { mockAccount.user }.returns(mockUser)
        every { mockAccount.accountKey }.returns(MicroBlogKey.twitter("123"))
        every { mockAccount.service }.returns(MockListsService())
        every { mockUser.userId }.returns("123")
        viewModel = ListsViewModel(mockRepository, mockAccountRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun source_containsAllLists(): Unit = runBlocking(Dispatchers.Main) {
        // check the source
        viewModel.source.first().let {
            val sourceItems = it.collectDataForTest()
            assertEquals(2, sourceItems.size)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun ownerSource_containsOwnedLists(): Unit = runBlocking(Dispatchers.Main) {
        // make sure ownerSource only emit data which isOwner() returns true
        viewModel.ownerSource.first().let {
            val ownerItems = it.collectDataForTest()
            assertEquals(1, ownerItems.size)
            assertEquals("owner", ownerItems[0].title)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun subscribeSource_containsSubscribedLists(): Unit = runBlocking(Dispatchers.Main) {
        // make sure ownerSource only emit data which isOwner() returns true
        viewModel.subscribedSource.first().let {
            val subscribeItems = it.collectDataForTest()
            assertEquals(1, subscribeItems.size)
            assertEquals("subscribe", subscribeItems[0].title)
        }
    }
}