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
package com.twidere.services.mastodon

import com.twidere.services.http.authorization.BearerAuthorization
import com.twidere.services.http.retrofit
import com.twidere.services.mastodon.api.MastodonResources
import com.twidere.services.microblog.MicroBlogService
import com.twidere.services.microblog.TimelineService
import com.twidere.services.microblog.model.IStatus

class MastodonService(
    private val host: String,
    private val accessToken: String,
) : MicroBlogService, TimelineService {
    private val resources by lazy {
        retrofit<MastodonResources>(
            "https://$host",
            BearerAuthorization(accessToken)
        )
    }

    override suspend fun homeTimeline(
        count: Int,
        since_id: String?,
        max_id: String?
    ) = resources.homeTimeline(max_id, since_id, limit = count)

    override suspend fun mentionsTimeline(
        count: Int,
        since_id: String?,
        max_id: String?
    ): List<IStatus> {
        TODO("Not yet implemented")
    }

    override suspend fun userTimeline(
        screen_name: String,
        count: Int,
        since_id: String?,
        max_id: String?,
        exclude_replies: Boolean
    ): List<IStatus> {
        TODO("Not yet implemented")
    }

    override suspend fun favorites(
        screen_name: String,
        count: Int,
        since_id: String?,
        max_id: String?
    ): List<IStatus> {
        TODO("Not yet implemented")
    }
}
