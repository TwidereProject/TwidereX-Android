package com.twidere.twiderex.viewmodel.user

import com.twidere.services.microblog.MicroBlogService
import com.twidere.twiderex.mock.service.MockRelationshipService
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.repository.UserRepository
import com.twidere.twiderex.viewmodel.AccountViewModelTestBase
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertNotNull

internal class UserViewModelRelationshipTest : AccountViewModelTestBase() {
    override val mockService: MicroBlogService
        get() = MockRelationshipService()

    @MockK(relaxed = true)
    private lateinit var repository: UserRepository

    @MockK
    private lateinit var inAppNotification: InAppNotification

    override fun setUp() {
        super.setUp()
    }

    @Test
    fun is_me() = runBlocking {
        every { repository.getUserFlow(any()) }.returns(flowOf(mockk {
            every { userKey }.returns(mockAccount.accountKey)
        }))
        val viewModel = UserViewModel(
            repository,
            mockAccountRepository,
            inAppNotification,
            mockAccount.accountKey
        )
        viewModel.isMe.firstOrNull().let {
            assertNotNull(it)
            assert(it)
        }
    }

    @Test
    fun is_not_me() = runBlocking {
        every { repository.getUserFlow(any()) }.returns(flowOf(mockk {
            every { userKey }.returns(MicroBlogKey.twitter("321"))
        }))
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
    fun follow_success() = runBlocking {
        val viewModel = UserViewModel(
            repository,
            mockAccountRepository,
            inAppNotification,
            MicroBlogKey.twitter("321")
        )
        viewModel.follow()
    }
}