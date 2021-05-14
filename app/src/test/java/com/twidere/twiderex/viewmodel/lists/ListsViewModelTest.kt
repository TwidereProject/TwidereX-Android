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

import androidx.compose.runtime.Composable
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.AmUser
import com.twidere.twiderex.model.ui.UiList
import com.twidere.twiderex.repository.ListsRepository
import com.twidere.twiderex.viewmodel.ViewModelTestBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class ListsViewModelTest : ViewModelTestBase() {

    @Mock
    private lateinit var mockRepository: ListsRepository

    @Mock
    private lateinit var mockAccount: AccountDetails

    @Mock
    private lateinit var mockUser: AmUser

    @Mock
    private lateinit var ownerList: UiList

    @Mock
    private lateinit var subscribeList: UiList

    private lateinit var viewModel: ListsViewModel

    override fun setUp() {
        super.setUp()
        whenever(mockRepository.fetchLists(any())).thenReturn(
            flow {
                emit(PagingData.from(listOf(ownerList, subscribeList)))
            }
        )

        whenever(ownerList.isOwner(any())).thenReturn(true)
        whenever(subscribeList.isOwner(any())).thenReturn(false)
        whenever(ownerList.title).thenReturn("owner")
        whenever(subscribeList.title).thenReturn("subscribe")
        whenever(subscribeList.isFollowed).thenReturn(true)
        whenever(mockAccount.user).thenReturn(mockUser)
        whenever(mockUser.userId).thenReturn("123")
        viewModel = ListsViewModel(mockRepository, mockAccount)
    }

    @Test
    @Composable
    fun source_containsAllLists(): Unit = runBlocking(Dispatchers.Main) {
        // check the source
        val sourceItems = viewModel.source.collectAsLazyPagingItems()
        Assert.assertEquals(2, sourceItems.itemCount)
    }

    @Test
    @Composable
    fun ownerSource_containsOwnedLists(): Unit = runBlocking(Dispatchers.Main) {
        // make sure ownerSource only emit data which isOwner() returns true
        val ownerItems = viewModel.ownerSource.collectAsLazyPagingItems()
        Assert.assertEquals(1, ownerItems.itemCount)
        Assert.assertEquals("owner", ownerItems[0]?.title)
    }

    @Test
    @Composable
    fun subscribeSource_containsSubscribedLists(): Unit = runBlocking(Dispatchers.Main) {
        // make sure ownerSource only emit data which isOwner() returns true
        val subscribeItems = viewModel.subscribedSource.collectAsLazyPagingItems()
        Assert.assertEquals(1, subscribeItems.itemCount)
        Assert.assertEquals("subscribe", subscribeItems[0]?.title)
    }
}
