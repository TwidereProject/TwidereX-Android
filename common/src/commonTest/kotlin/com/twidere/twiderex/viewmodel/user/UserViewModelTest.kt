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
package com.twidere.twiderex.viewmodel.user

import app.cash.turbine.test
import com.twidere.services.microblog.MicroBlogService
import com.twidere.twiderex.mock.service.MockRelationshipService
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.repository.UserRepository
import com.twidere.twiderex.viewmodel.AccountViewModelTestBase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal class UserViewModelTest : AccountViewModelTestBase() {
    override val mockService: MicroBlogService
        get() = MockRelationshipService()

    @MockK(relaxed = true)
    private lateinit var repository: UserRepository

    @MockK
    private lateinit var inAppNotification: InAppNotification

    @Test
    fun user_information(): Unit = runBlocking {
        every { repository.getUserFlow(any()) }.returns(
            flowOf(
                mockk {
                    every { userKey }.returns(mockAccount.accountKey)
                }
            )
        )
        coEvery { repository.lookupUserById(any(), any(), any()) }.returns(
            mockk {
                every { userKey }.returns(mockAccount.accountKey)
            }
        )
        val viewModel = UserViewModel(
            repository,
            mockAccountRepository,
            inAppNotification,
            mockAccount.accountKey
        )
        viewModel.user.test {
            val it = awaitItem()
            assertNotNull(it)
            assertEquals(mockAccount.accountKey, it.userKey)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun is_me(): Unit = runBlocking {
        every { repository.getUserFlow(any()) }.returns(
            flowOf(
                mockk {
                    every { userKey }.returns(mockAccount.accountKey)
                }
            )
        )
        val viewModel = UserViewModel(
            repository,
            mockAccountRepository,
            inAppNotification,
            mockAccount.accountKey
        )
        viewModel.isMe.test {
            assert(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun is_not_me(): Unit = runBlocking {
        every { repository.getUserFlow(any()) }.returns(
            flowOf(
                mockk {
                    every { userKey }.returns(MicroBlogKey.twitter("321"))
                }
            )
        )
        val viewModel = UserViewModel(
            repository,
            mockAccountRepository,
            inAppNotification,
            MicroBlogKey.twitter("321")
        )
        viewModel.isMe.firstOrNull().let {
            assertNotNull(it)
            assert(!it)
        }
    }

    @Test
    fun follow_success(): Unit = runBlocking {
        val viewModel = UserViewModel(
            repository,
            mockAccountRepository,
            inAppNotification,
            MicroBlogKey.twitter("321")
        )
        viewModel.loadingRelationship.test {
            assert(!awaitItem())
            viewModel.follow()
            assert(awaitItem())
            assert(!awaitItem())
        }
        viewModel.relationship.test {
            val item = awaitItem()
            assertNotNull(item)
            assert(item.following)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
