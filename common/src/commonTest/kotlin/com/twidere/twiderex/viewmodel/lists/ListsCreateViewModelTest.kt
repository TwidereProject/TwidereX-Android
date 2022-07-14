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
package com.twidere.twiderex.viewmodel.lists

import app.cash.turbine.test
import com.twidere.services.microblog.MicroBlogService
import com.twidere.twiderex.mock.db.MockCacheDatabase
import com.twidere.twiderex.mock.service.MockListsService
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.notification.NotificationEvent
import com.twidere.twiderex.repository.ListsRepository
import com.twidere.twiderex.viewmodel.AccountViewModelTestBase
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal class ListsCreateViewModelTest : AccountViewModelTestBase() {
    override val mockService: MicroBlogService
        get() = MockListsService()

    private val mockRepository: ListsRepository = ListsRepository(MockCacheDatabase())

    @MockK
    private lateinit var mockAppNotification: InAppNotification

    private var errorNotification: NotificationEvent? = null

    private lateinit var createViewModel: ListsCreateViewModel

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
    }

    @Test
    fun createList_successExpectTrue(): Unit = runBlocking {
        assertNull(errorNotification)
        createViewModel.loading.test {
            assert(!awaitItem())
            launch {
                val result = createViewModel.createList(title = "title", private = false)
                assertNotNull(result)
            }
            assert(awaitItem())
            assert(!awaitItem())
        }
        assertNull(errorNotification)
    }

    @Test
    fun createList_failedExpectFalseAndShowNotification(): Unit = runBlocking {
        assertNull(errorNotification)
        createViewModel.loading.test {
            assert(!awaitItem())
            launch {
                val result = createViewModel.createList(title = "error", private = false)
                assertNull(result)
            }
            assert(awaitItem())
            assert(!awaitItem())
        }
        assertNotNull(errorNotification)
    }
}
