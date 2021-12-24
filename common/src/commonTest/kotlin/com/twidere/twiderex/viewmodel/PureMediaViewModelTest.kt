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

import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.repository.MediaRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class PureMediaViewModelTest : ViewModelTestBase() {

    @MockK
    lateinit var repository: MediaRepository

    lateinit var viewModel: PureMediaViewModel

    override fun setUp() {
        super.setUp()
        viewModel = PureMediaViewModel(repository, MicroBlogKey.twitter("123"))
        coEvery { repository.findMediaByBelongToKey(any()) }.returns(
            (0..3).map {
                mockk {
                    every { belongToKey }.returns(MicroBlogKey.twitter("123"))
                    every { mediaUrl }.returns(it.toString())
                }
            }
        )
    }

    @Test
    fun loadSource_success(): Unit = runBlocking {
        viewModel.source.firstOrNull().let {
            assertNotNull(it)
            assertEquals(4, it.size)
            assertTrue {
                it.all { it.belongToKey == MicroBlogKey.twitter("123") }
            }
        }
    }
}
