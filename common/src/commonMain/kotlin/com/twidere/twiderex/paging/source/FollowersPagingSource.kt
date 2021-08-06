/*
 *  Twidere X
 *
 *  Copyright (C) 2020-2021 Tlaster <tlaster@outlook.com>
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
import com.twidere.services.microblog.RelationshipService
import com.twidere.services.microblog.model.IPaging
import com.twidere.twiderex.dataprovider.toUi
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiUser

class FollowersPagingSource(
    private val userKey: MicroBlogKey,
    private val service: RelationshipService
) : PagingSource<String, UiUser>() {
    override suspend fun load(params: LoadParams<String>): LoadResult<String, UiUser> {
        return try {
            val page = params.key
            val result = service.followers(userKey.id, nextPage = page)
            val users = result.map {
                it.toUi(userKey)
            }
            val nextPage = if (result is IPaging) {
                result.nextPage
            } else {
                null
            }
            LoadResult.Page(data = users, prevKey = null, nextKey = nextPage)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<String, UiUser>): String? {
        return null
    }
}
