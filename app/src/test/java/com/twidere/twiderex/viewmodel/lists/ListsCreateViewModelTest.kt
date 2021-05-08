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

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.twidere.services.utils.MicroBlogJsonException
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.ui.UiList
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.notification.NotificationEvent
import com.twidere.twiderex.repository.ListsRepository
import com.twidere.twiderex.viewmodel.ViewModelTestBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ListsCreateViewModelTest : ViewModelTestBase() {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockRepository: ListsRepository

    @Mock
    private lateinit var mockAppNotification: InAppNotification

    @Mock
    private lateinit var mockAccount: AccountDetails

    @Mock
    private lateinit var mockSuccessObserver: Observer<Boolean>

    @Mock
    private lateinit var mockLoadingObserver: Observer<Boolean>

    private var errorNotification: NotificationEvent? = null

    private lateinit var createViewModel: ListsCreateViewModel

    @Test
    fun createList_successExpectTrue(): Unit = runBlocking(Dispatchers.Main) {
        whenever(mockRepository.createLists(any(), any(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(UiList.sample())

        verifySuccessAndLoadingBefore(mockLoadingObserver, mockSuccessObserver)
        async {
            createViewModel.createList(title = "title", private = false)
        }.await()
        verifySuccessAndLoadingAfter(mockLoadingObserver, mockSuccessObserver, true)
    }

    @Test
    fun createList_failedExpectFalseAndShowNotification(): Unit = runBlocking(Dispatchers.Main) {
        whenever(mockRepository.createLists(any(), any(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenAnswer { throw MicroBlogJsonException("test exception") }
        verifySuccessAndLoadingBefore(mockLoadingObserver, mockSuccessObserver)
        Assert.assertNull(errorNotification)
        async {
            createViewModel.createList(title = "title", private = false)
        }.await()
        verifySuccessAndLoadingAfter(mockLoadingObserver, mockSuccessObserver, false)
        Assert.assertNotNull(errorNotification)
    }

    override fun setUp() {
        super.setUp()
        createViewModel = ListsCreateViewModel(
            mockAppNotification,
            mockRepository,
            mockAccount
        ) {
            mockSuccessObserver.onChanged(it)
        }
        whenever(mockAppNotification.show(any<NotificationEvent>())).then {
            errorNotification = it.getArgument(0) as NotificationEvent
            Unit
        }
        errorNotification = null
        mockSuccessObserver.onChanged(false)
        createViewModel.loading.observeForever(mockLoadingObserver)
    }

    private fun verifySuccessAndLoadingBefore(loadingObserver: Observer<Boolean>, successObserver: Observer<Boolean>) {
        verify(loadingObserver, times(1)).onChanged(false)
        verify(successObserver).onChanged(false)
    }

    private fun verifySuccessAndLoadingAfter(loadingObserver: Observer<Boolean>, successObserver: Observer<Boolean>, success: Boolean) {
        verify(loadingObserver, times(1)).onChanged(true)
        verify(loadingObserver, times(2)).onChanged(false)
        verify(successObserver, if (success) times(1) else times(2)).onChanged(success)
    }
}