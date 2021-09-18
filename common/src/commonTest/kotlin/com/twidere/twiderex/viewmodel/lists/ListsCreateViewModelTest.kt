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
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class ListsCreateViewModelTest : AccountViewModelTestBase() {
    override val mockService: MicroBlogService
        get() = MockListsService()

    private val mockRepository: ListsRepository = ListsRepository(MockCacheDatabase())

    @MockK
    private lateinit var mockAppNotification: InAppNotification

    @MockK
    private lateinit var mockLoadingObserver: Observer<Boolean>

    private var errorNotification: NotificationEvent? = null

    private lateinit var createViewModel: ListsCreateViewModel

    private val scope = CoroutineScope(Dispatchers.Main)

    override fun setUp() {
        super.setUp()
        createViewModel = ListsCreateViewModel(
            mockAppNotification,
            mockRepository,
            mockAccountRepository
        )
        every { mockAppNotification.show(any()) }.answers {
            errorNotification = arg(0)
        }
        errorNotification = null
        scope.launch {
            createViewModel.loading.collect {
                mockLoadingObserver.onChanged(it)
            }
        }
    }

    @Test
    fun createList_successExpectTrue(): Unit = runBlocking(Dispatchers.Main) {
        verifySuccessAndLoadingBefore(mockLoadingObserver)
        val result = createViewModel.createList(title = "title", private = false)
        assertNotNull(result)
        verifySuccessAndLoadingAfter(mockLoadingObserver)
    }

    @Test
    fun createList_failedExpectFalseAndShowNotification(): Unit = runBlocking(Dispatchers.Main) {
        verifySuccessAndLoadingBefore(mockLoadingObserver)
        assertNull(errorNotification)
        val result = createViewModel.createList(title = "error", private = false)
        assertNull(result)
        verifySuccessAndLoadingAfter(mockLoadingObserver)
        assertNotNull(errorNotification)
    }

    private fun verifySuccessAndLoadingBefore(
        loadingObserver: Observer<Boolean>,
    ) {
        verify(exactly = 1) { loadingObserver.onChanged(false) }
    }

    private fun verifySuccessAndLoadingAfter(
        loadingObserver: Observer<Boolean>,
    ) {
        verify(exactly = 1) { loadingObserver.onChanged(true) }
        verify(exactly = 1) { loadingObserver.onChanged(false) }
    }
}
