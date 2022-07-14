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
import com.twidere.services.mastodon.MastodonService
import com.twidere.services.mastodon.model.Hashtag
import com.twidere.twiderex.defaultLoadCount

class MastodonSearchHashtagPagingSource(
    private val query: String,
    private val service: MastodonService
) : PagingSource<Int, Hashtag>() {
    override fun getRefreshKey(state: PagingState<Int, Hashtag>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Hashtag> {
        return try {
            val offset = params.key ?: 0
            val result = service.searchHashTag(query, offset = offset, count = defaultLoadCount)
            LoadResult.Page(
                data = result,
                prevKey = null,
                nextKey = if (result.size == defaultLoadCount) {
                    result.size + offset
                } else {
                    null
                }
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
