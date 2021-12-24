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
package com.twidere.twiderex.paging.source

import androidx.paging.PagingSource
import com.twidere.twiderex.mock.service.MockRelationshipService
import com.twidere.twiderex.model.MicroBlogKey
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class FollowingPagingSourceTest {
    @Test
    fun loadFollowingUsersFromService(): Unit = runBlocking {
        val service = MockRelationshipService()
        val pagingSource = FollowingPagingSource(
            MicroBlogKey.twitter("123"),
            service = service
        )
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 2,
                placeholdersEnabled = false
            )
        )
        (result as PagingSource.LoadResult.Page).data.map {
            assert(service.showRelationship(it.id).following)
        }
    }
}
