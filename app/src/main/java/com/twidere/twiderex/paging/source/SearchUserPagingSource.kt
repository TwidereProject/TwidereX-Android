/*
 *  Twidere X
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
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
import com.twidere.services.microblog.SearchService
import com.twidere.twiderex.db.mapper.toDbUser
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.model.ui.UiUser.Companion.toUi

class SearchUserPagingSource(
    private val query: String,
    private val service: SearchService
) : PagingSource<Int, UiUser>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UiUser> {
        return try {
            val page = params.key ?: 0
            val result = service.searchUsers(query, page = page, count = params.loadSize).map {
                it.toDbUser().toUi()
            }
            LoadResult.Page(data = result, prevKey = null, nextKey = page + 1)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
