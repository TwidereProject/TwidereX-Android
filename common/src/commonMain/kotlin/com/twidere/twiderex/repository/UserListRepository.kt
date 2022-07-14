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
package com.twidere.twiderex.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.twidere.services.microblog.RelationshipService
import com.twidere.twiderex.defaultLoadCount
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.paging.source.FollowersPagingSource
import com.twidere.twiderex.paging.source.FollowingPagingSource

class UserListRepository {
    fun following(
        userKey: MicroBlogKey,
        service: RelationshipService,
    ) = Pager(
        config = PagingConfig(
            pageSize = defaultLoadCount,
            enablePlaceholders = false,
        )
    ) {
        FollowingPagingSource(
            userKey = userKey,
            service = service,
        )
    }.flow

    fun followers(
        userKey: MicroBlogKey,
        service: RelationshipService
    ) = Pager(
        config = PagingConfig(
            pageSize = defaultLoadCount,
            enablePlaceholders = false,
        )
    ) {
        FollowersPagingSource(
            userKey = userKey,
            service = service,
        )
    }.flow
}
