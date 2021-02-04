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
package com.twidere.twiderex.paging.source.twitter

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.twidere.services.twitter.TwitterService
import com.twidere.twiderex.db.mapper.toDbUser
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.model.ui.UiUser.Companion.toUi

class TwitterFollowingPagingSource(
    private val userKey: MicroBlogKey,
    private val service: TwitterService
) : PagingSource<String, UiUser>() {
    override suspend fun load(params: LoadParams<String>): LoadResult<String, UiUser> {
        return try {
            val page = params.key
            val result = service.following(userKey.id, cursor = page)
            val users = result.data?.map {
                it.toDbUser(userKey).toUi()
            } ?: emptyList()
            LoadResult.Page(data = users, prevKey = null, nextKey = result.meta?.nextToken)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<String, UiUser>): String? {
        return null
    }
}
