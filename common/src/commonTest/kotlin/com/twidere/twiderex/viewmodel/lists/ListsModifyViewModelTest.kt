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

import com.twidere.services.microblog.MicroBlogService
import com.twidere.twiderex.mock.Observer
import com.twidere.twiderex.mock.db.MockCacheDatabase
import com.twidere.twiderex.mock.service.MockListsService
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.notification.NotificationEvent
import com.twidere.twiderex.repository.ListsRepository
import com.twidere.twiderex.viewmodel.AccountViewModelTestBase
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class ListsModifyViewModelTest : AccountViewModelTestBase() {
    override val mockService: MicroBlogService
        get() = MockListsService()

    private var mockRepository: ListsRepository = ListsRepository(MockCacheDatabase())

    @MockK
    private lateinit var mockAppNotification: InAppNotification

    @MockK
    private lateinit var mockSuccessObserver: Observer<Boolean>

    @MockK
    private lateinit var mockLoadingObserver: Observer<Boolean>

    private var errorNotification: NotificationEvent? = null

    private lateinit var modifyViewModel: ListsModifyViewModel

    private val scope by lazy {
        CoroutineScope(Dispatchers.Main)
    }

    @Test
    fun updateList_successExpectTrue(): Unit = runBlocking(Dispatchers.Main) {
        verifySuccessAndLoadingBefore(mockLoadingObserver, mockSuccessObserver)
        suspendCoroutine<Boolean> {
            modifyViewModel.editList(
                listId = "123",
                title = "title",
                private = false
            ) { success, _ ->
                mockSuccessObserver.onChanged(success)
                it.resume(success)
            }
        }
        verifySuccessAndLoadingAfter(mockLoadingObserver, mockSuccessObserver, true)
    }

    @Test
    fun updateList_failedExpectFalseAndShowNotification(): Unit = runBlocking(Dispatchers.Main) {
        verifySuccessAndLoadingBefore(mockLoadingObserver, mockSuccessObserver)
        Assert.assertNull(errorNotification)
        suspendCoroutine<Boolean> {
            modifyViewModel.editList(
                listId = "error",
                title = "name",
                private = false
            ) { success, _ ->
                mockSuccessObserver.onChanged(success)
                it.resume(success)
            }
        }
        verifySuccessAndLoadingAfter(mockLoadingObserver, mockSuccessObserver, false)
        Assert.assertNotNull(errorNotification)
    }

    @Test
    fun deleteList_successExpectTrue(): Unit = runBlocking(Dispatchers.Main) {
        verifySuccessAndLoadingBefore(mockLoadingObserver, mockSuccessObserver)
        suspendCoroutine<Boolean> {
            modifyViewModel.deleteList(listId = "123", MicroBlogKey.Empty) { success, _ ->
                mockSuccessObserver.onChanged(success)
                it.resume(success)
            }
        }
        verifySuccessAndLoadingAfter(mockLoadingObserver, mockSuccessObserver, true)
    }

    @Test
    fun deleteList_failedExpectFalseAndShowNotification(): Unit = runBlocking(Dispatchers.Main) {
        verifySuccessAndLoadingBefore(mockLoadingObserver, mockSuccessObserver)
        Assert.assertNull(errorNotification)
        suspendCoroutine<Boolean> {
            modifyViewModel.deleteList(listId = "error", MicroBlogKey.Empty) { success, _ ->
                mockSuccessObserver.onChanged(success)
                it.resume(success)
            }
        }
        verifySuccessAndLoadingAfter(mockLoadingObserver, mockSuccessObserver, false)
        Assert.assertNotNull(errorNotification)
    }

    @Test
    fun subscribeList_successExpectTrue(): Unit = runBlocking(Dispatchers.Main) {
        verifySuccessAndLoadingBefore(mockLoadingObserver, mockSuccessObserver)
        suspendCoroutine<Boolean> {
            modifyViewModel.subscribeList(MicroBlogKey.twitter("123")) { success, _ ->
                mockSuccessObserver.onChanged(success)
                it.resume(success)
            }
        }
        verifySuccessAndLoadingAfter(mockLoadingObserver, mockSuccessObserver, true)
    }

    @Test
    fun subscribeList_failedExpectFalseAndShowNotification(): Unit = runBlocking(Dispatchers.Main) {
        verifySuccessAndLoadingBefore(mockLoadingObserver, mockSuccessObserver)
        Assert.assertNull(errorNotification)
        suspendCoroutine<Boolean> {
            modifyViewModel.subscribeList(MicroBlogKey.twitter("error")) { success, _ ->
                mockSuccessObserver.onChanged(success)
                it.resume(success)
            }
        }
        verifySuccessAndLoadingAfter(mockLoadingObserver, mockSuccessObserver, false)
        Assert.assertNotNull(errorNotification)
    }

    @Test
    fun unsubscribeList_successExpectTrue(): Unit = runBlocking(Dispatchers.Main) {
        verifySuccessAndLoadingBefore(mockLoadingObserver, mockSuccessObserver)
        suspendCoroutine<Boolean> {
            modifyViewModel.unsubscribeList(MicroBlogKey.twitter("123")) { success, _ ->
                mockSuccessObserver.onChanged(success)
                it.resume(success)
            }
        }
        verifySuccessAndLoadingAfter(mockLoadingObserver, mockSuccessObserver, true)
    }

    @Test
    fun unsubscribeList_failedExpectFalseAndShowNotification(): Unit =
        runBlocking(Dispatchers.Main) {
            verifySuccessAndLoadingBefore(mockLoadingObserver, mockSuccessObserver)
            Assert.assertNull(errorNotification)
            suspendCoroutine<Boolean> {
                modifyViewModel.unsubscribeList(MicroBlogKey.twitter("error")) { success, _ ->
                    mockSuccessObserver.onChanged(success)
                    it.resume(success)
                }
            }
            verifySuccessAndLoadingAfter(mockLoadingObserver, mockSuccessObserver, false)
            Assert.assertNotNull(errorNotification)
        }

    override fun setUp() {
        super.setUp()
        modifyViewModel = ListsModifyViewModel(
            mockRepository,
            mockAppNotification,
            mockAccountRepository,
            listKey = MicroBlogKey.Empty,
        )
        every { mockAppNotification.show(any()) }.answers {
            errorNotification = arg(0)
        }
        errorNotification = null
        mockSuccessObserver.onChanged(false)
        scope.launch {
            modifyViewModel.loading.collect {
                mockLoadingObserver.onChanged(it)
            }
        }
    }

    private fun verifySuccessAndLoadingBefore(
        loadingObserver: Observer<Boolean>,
        successObserver: Observer<Boolean>
    ) {
        verify(exactly = 1) { loadingObserver.onChanged(false) }
        verify { successObserver.onChanged(false) }
    }

    private fun verifySuccessAndLoadingAfter(
        loadingObserver: Observer<Boolean>,
        successObserver: Observer<Boolean>,
        success: Boolean
    ) {
        verify(exactly = 1) { loadingObserver.onChanged(true) }
        verify(exactly = 1) { loadingObserver.onChanged(false) }
        verify(exactly = if (success) 1 else 2) { successObserver.onChanged(success) }
    }
}
