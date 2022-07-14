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

import androidx.paging.PagingData
import com.twidere.services.microblog.MicroBlogService
import com.twidere.twiderex.mock.paging.collectDataForTest
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.repository.StatusRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class StatusViewModelTest : AccountViewModelTestBase() {
    override val mockService: MicroBlogService = mockk()

    @MockK
    private lateinit var repository: StatusRepository
    private lateinit var viewModel: StatusViewModel
    override fun setUp() {
        super.setUp()
        every { repository.conversation(any(), any(), any(), any()) }.returns(
            flowOf(
                PagingData.from(
                    (0..4).map {
                        mockk {
                            every { statusKey }.returns(MicroBlogKey.twitter(it.toString()))
                            every { statusId }.returns(it.toString())
                        }
                    }
                )
            )
        )
        viewModel = StatusViewModel(repository, mockAccountRepository, MicroBlogKey.twitter("2"))
    }

    @Test
    fun source_loadConversation(): Unit = runBlocking {
        viewModel.source.first().let {
            val data = it.collectDataForTest()
            assertEquals(5, data.size)
            assertTrue {
                data.any {
                    it.statusId == "2"
                }
            }
            assertTrue {
                data.indexOfFirst {
                    it.statusId == "2"
                } == 2
            }
        }
    }
}
