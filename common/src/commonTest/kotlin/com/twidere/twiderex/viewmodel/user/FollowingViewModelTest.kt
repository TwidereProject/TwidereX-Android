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

import com.twidere.services.microblog.MicroBlogService
import com.twidere.twiderex.mock.service.MockRelationshipService
import com.twidere.twiderex.repository.UserListRepository
import com.twidere.twiderex.viewmodel.AccountViewModelTestBase
import io.mockk.impl.annotations.MockK

internal class FollowingViewModelTest : AccountViewModelTestBase() {
  override val mockService: MicroBlogService
    get() = MockRelationshipService()

  @MockK
  private lateinit var repository: UserListRepository

  // private lateinit var viewModel: FollowingViewModel

  // override fun setUp() {
  //   super.setUp()
  //   every { repository.following(any(), any()) }.returns(
  //     flowOf(
  //       PagingData.from(
  //         (0..3).map {
  //           mockk()
  //         }
  //       )
  //     )
  //   )
  //   viewModel = FollowingViewModel(
  //     repository,
  //     mockAccountRepository,
  //     MicroBlogKey.twitter("321")
  //   )
  // }
  //
  // @Test
  // fun source_any(): Unit = runBlocking {
  //   viewModel.source.firstOrNull().let {
  //     assertNotNull(it)
  //     assert(it.collectDataForTest().any())
  //   }
  // }
}
