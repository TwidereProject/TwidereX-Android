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
import androidx.paging.PagingState
import com.twidere.services.microblog.SearchService
import com.twidere.twiderex.dataprovider.mapper.toUi
import com.twidere.twiderex.defaultLoadCount
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiUser

class SearchUserPagingSource(
    private val accountKey: MicroBlogKey,
    private val query: String,
    private val service: SearchService,
    private val following: Boolean = false
) : PagingSource<Int, UiUser>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UiUser> {
        return try {
            val page = params.key ?: 0
            val result = service.searchUsers(query, page = page, count = defaultLoadCount, following = following).map {
                it.toUi(accountKey)
            }
            LoadResult.Page(
                data = result,
                prevKey = null,
                nextKey = if (result.size == defaultLoadCount) page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, UiUser>): Int? {
        return null
    }
}
