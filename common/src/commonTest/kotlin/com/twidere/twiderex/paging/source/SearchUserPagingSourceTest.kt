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
import com.twidere.twiderex.mock.model.mockIUser
import com.twidere.twiderex.mock.service.MockSearchService
import com.twidere.twiderex.model.MicroBlogKey
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class SearchUserPagingSourceTest {
    @Test
    fun loadUserFromSearchServiceAndApplyRightKeys() = runBlocking {
        val service = MockSearchService()
        val pagingSource = SearchUserPagingSource(
            MicroBlogKey.twitter("123"),
            service = service,
            query = "test"
        )
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )
        assert(result is PagingSource.LoadResult.Page)
        val nextKey = (result as PagingSource.LoadResult.Page).nextKey ?: 0
        assertEquals(1, nextKey)

        service.searchUser = listOf(mockIUser())
        val resultAppend = pagingSource.load(
            PagingSource.LoadParams.Append(
                key = nextKey,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )
        assert(resultAppend is PagingSource.LoadResult.Page)
        assertNull((resultAppend as PagingSource.LoadResult.Page).nextKey)
    }

    @Test
    fun loadReturnErrorWhenErrorOccurred() = runBlocking {
        val service = MockSearchService()
        service.errorMsg = "throw test errors"
        val pagingSource = SearchUserPagingSource(
            MicroBlogKey.twitter("123"),
            service = service,
            query = "test"
        )
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )
        assert(result is PagingSource.LoadResult.Error)
    }
}
