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
import com.twidere.services.microblog.model.IPaging
import com.twidere.services.microblog.model.IUser
import com.twidere.twiderex.dataprovider.mapper.toUi
import com.twidere.twiderex.mock.model.mockIUser
import com.twidere.twiderex.mock.model.toIPaging
import com.twidere.twiderex.model.MicroBlogKey
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

internal class UserPagingSourceTest {
    @Test
    fun loadReturnsPageWhenOnSuccessfulLoadOfPageKeyedData() = runBlocking {
        val pagingSource = MockUserPagingSource(MicroBlogKey.Empty)
        assertEquals(
            PagingSource.LoadResult.Page(
                data = pagingSource.mockData.map { it.toUi(MicroBlogKey.Empty) },
                prevKey = null,
                nextKey = (pagingSource.mockData as IPaging).nextPage
            ),
            pagingSource.load(
                PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = 2,
                    placeholdersEnabled = false
                )
            )
        )
    }

    @Test
    fun loadReturnsErrorWhenErrorOccurred() = runBlocking {
        val pagingSource = MockUserPagingSource(MicroBlogKey.Empty)
        pagingSource.errorMsg = "throw test errors"
        assert(
            pagingSource.load(
                PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = 2,
                    placeholdersEnabled = false
                )
            ) is PagingSource.LoadResult.Error
        )
    }
}

private class MockUserPagingSource(accountKey: MicroBlogKey) : UserPagingSource(
    accountKey
) {
    var errorMsg: String? = null
    val mockData = listOf(mockIUser()).toIPaging()

    override suspend fun loadUsers(params: LoadParams<String>): List<IUser> {
        if (!errorMsg.isNullOrEmpty()) throw Error(errorMsg)
        return mockData
    }
}
