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
package com.twidere.twiderex.viewmodel

import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.repository.AccountRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test
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
    fun set_activeAccount() {
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
