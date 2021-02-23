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
package com.twidere.services.mastodon

import com.twidere.services.http.authorization.BearerAuthorization
import com.twidere.services.http.retrofit
import com.twidere.services.mastodon.api.MastodonResources
import com.twidere.services.mastodon.model.MastodonPaging
import com.twidere.services.mastodon.model.exceptions.MastodonException
import com.twidere.services.microblog.LookupService
import com.twidere.services.microblog.MicroBlogService
import com.twidere.services.microblog.RelationshipService
import com.twidere.services.microblog.TimelineService
import com.twidere.services.microblog.model.IRelationship
import com.twidere.services.microblog.model.IStatus
import com.twidere.services.microblog.model.IUser
import com.twidere.services.microblog.model.Relationship

class MastodonService(
    private val host: String,
    private val accessToken: String,
) : MicroBlogService, TimelineService, LookupService, RelationshipService {
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
        user_id: String,
        count: Int,
        since_id: String?,
        max_id: String?,
        exclude_replies: Boolean
    ): List<IStatus> = resources.userTimeline(
        user_id = user_id,
        max_id = max_id,
        since_id = since_id,
        limit = count,
        exclude_replies = exclude_replies,
    )

    override suspend fun favorites(
        user_id: String,
        count: Int,
        since_id: String?,
        max_id: String?
    ): List<IStatus> {
        val response = resources.favoritesList(
            max_id = max_id,
            since_id = since_id,
            limit = count,
        )
        return MastodonPaging.from(response)
    }

    override suspend fun lookupUserByName(name: String): IUser {
        TODO("Not yet implemented")
    }

    override suspend fun lookupUsersByName(name: List<String>): List<IUser> {
        TODO("Not yet implemented")
    }

    override suspend fun lookupUser(id: String): IUser {
        return resources.lookupUser(id)
    }

    override suspend fun lookupStatus(id: String): IStatus {
        TODO("Not yet implemented")
    }

    override suspend fun userPinnedStatus(userId: String): IStatus? {
        TODO("Not yet implemented")
    }

    override suspend fun showRelationship(target_id: String): IRelationship {
        val response = resources.showFriendships(target_id).firstOrNull()
            ?: throw MastodonException("can not fetch relationship")
        return Relationship(
            followedBy = response.following ?: false,
            following = response.followedBy ?: false,
        )
    }

    override suspend fun followers(user_id: String, nextPage: String?) = resources.followers(
        user_id,
        max_id = nextPage,
    ).let {
        MastodonPaging.from(it)
    }

    override suspend fun following(user_id: String, nextPage: String?) = resources.following(
        user_id,
        max_id = nextPage,
    ).let {
        MastodonPaging.from(it)
    }

    override suspend fun follow(user_id: String) {
        resources.follow(user_id)
    }

    override suspend fun unfollow(user_id: String) {
        resources.unfollow(user_id)
    }
}
