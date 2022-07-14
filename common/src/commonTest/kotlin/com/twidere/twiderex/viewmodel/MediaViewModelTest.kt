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

import com.twidere.services.microblog.MicroBlogService
import com.twidere.twiderex.action.MediaAction
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.repository.StatusRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class MediaViewModelTest : AccountViewModelTestBase() {
    override val mockService: MicroBlogService = mockk()
    private lateinit var viewModel: MediaViewModel

    @MockK
    private lateinit var repository: StatusRepository

    @MockK
    private lateinit var mediaAction: MediaAction

    override fun setUp() {
        super.setUp()
        viewModel = MediaViewModel(
            repository,
            mockAccountRepository,
            mediaAction,
            MicroBlogKey.twitter("123")
        )
        coEvery { repository.loadStatus(any(), any()) }.returns(
            flowOf(
                mockk {
                    every { statusKey }.returns(MicroBlogKey.twitter("123"))
                }
            )
        )
    }

    @Test
    fun load_status(): Unit = runBlocking {
        viewModel.status.firstOrNull().let {
            assertNotNull(it)
            assertEquals(MicroBlogKey.twitter("123"), it.statusKey)
        }
    }

    @Test
    fun saveFile_success(): Unit = runBlocking {
        viewModel.saveFile(
            mockk {
                every { mediaUrl }.returns("123")
                every { fileName }.returns("target")
            }
        ) {
            it
        }
        verify(exactly = 1) {
            mediaAction.download(
                "123",
                "target",
                MicroBlogKey.twitter("123")
            )
        }
    }

    @Test
    fun shareMedia_success(): Unit = runBlocking {
        viewModel.shareMedia(
            mockk {
                every { mediaUrl }.returns("123")
                every { fileName }.returns("target")
            }
        )
        verify(exactly = 1) {
            mediaAction.share(
                "123",
                "target",
                MicroBlogKey.twitter("123")
            )
        }
    }
}
