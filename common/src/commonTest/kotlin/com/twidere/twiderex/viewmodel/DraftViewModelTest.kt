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

import com.twidere.twiderex.action.DraftAction
import com.twidere.twiderex.repository.DraftRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class DraftViewModelTest : ViewModelTestBase() {
    @MockK
    private lateinit var action: DraftAction

    @MockK
    private lateinit var repository: DraftRepository
    private lateinit var viewModel: DraftViewModel

    override fun setUp() {
        super.setUp()
        viewModel = DraftViewModel(repository, action)
        every { repository.source }.returns(
            flowOf(
                (0..3).map {
                    mockk {
                        every { draftId }.returns(it.toString())
                    }
                }
            )
        )
    }

    @Test
    fun draft_list() = runBlocking {
        viewModel.source.firstOrNull().let {
            assertNotNull(it)
            assertEquals(4, it.size)
            assertContentEquals(
                (0..3).map { it.toString() }.toTypedArray(),
                it.map { it.draftId }.toTypedArray()
            )
        }
    }

    @Test
    fun delete_draft() = runBlocking {
        viewModel.delete(
            mockk {
                every { draftId }.returns("123")
            }
        )
        verify(exactly = 1) { action.delete("123") }
    }
}
