package com.twidere.twiderex.viewmodel

import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.repository.AccountRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class ActiveAccountViewModelTest : ViewModelTestBase() {
    @MockK(relaxed = true)
    private lateinit var repository: AccountRepository
    private lateinit var viewModel: ActiveAccountViewModel

    override fun setUp() {
        super.setUp()
        viewModel = ActiveAccountViewModel(repository)
        every { repository.getFirstByType(PlatformType.Twitter) }.returns(mockk())
        every { repository.getFirstByType(PlatformType.Fanfou) }.returns(null)
    }

    @Test
    fun set_activeAccount()  {
        viewModel.setActiveAccount(mockk())
        verify(exactly = 1) { repository.setCurrentAccount(any()) }
    }

    @Test
    fun delete_account() {
        viewModel.deleteAccount(mockk())
        verify(exactly = 1) { repository.delete(any()) }
    }

    @Test
    fun get_target_platform_success() {
        viewModel.getTargetPlatformDefault(PlatformType.Twitter).let {
            assertNotNull(it)
            verify(exactly = 1) { repository.getFirstByType(PlatformType.Twitter) }
        }
    }

    @Test
    fun get_target_platform_failed() {
        viewModel.getTargetPlatformDefault(PlatformType.Fanfou).let {
            assertNull(it)
            verify(exactly = 1) { repository.getFirstByType(PlatformType.Fanfou) }
        }
    }
}