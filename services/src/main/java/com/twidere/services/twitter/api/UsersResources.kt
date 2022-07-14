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
package com.twidere.services.twitter.api

import com.twidere.services.twitter.model.BlockV2
import com.twidere.services.twitter.model.BlockV2Request
import com.twidere.services.twitter.model.ProfileBanner
import com.twidere.services.twitter.model.RelationshipResponse
import com.twidere.services.twitter.model.TwitterResponseV2
import com.twidere.services.twitter.model.User
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface UsersResources {
    @GET("/1.1/account/verify_credentials.json")
    suspend fun verifyCredentials(): User?

    @GET("/1.1/users/profile_banner.json")
    suspend fun profileBanners(@Query("screen_name") screenName: String): ProfileBanner

    @GET("/1.1/friendships/show.json")
    suspend fun showFriendships(@Query("target_id") target_id: String): RelationshipResponse

    @POST("/2/users/{sourceId}/blocking")
    suspend fun block(
        @Path(value = "sourceId") sourceId: String,
        @Body target: BlockV2Request
    ): TwitterResponseV2<BlockV2>

    @DELETE("/2/users/{sourceId}/blocking/{targetId}")
    suspend fun unblock(
        @Path(value = "sourceId") sourceId: String,
        @Path(value = "targetId") targetId: String,
    ): TwitterResponseV2<BlockV2>
}
