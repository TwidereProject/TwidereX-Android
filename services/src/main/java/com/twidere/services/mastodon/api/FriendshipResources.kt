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
package com.twidere.services.mastodon.api

import com.twidere.services.mastodon.model.Account
import com.twidere.services.mastodon.model.RelationshipResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FriendshipResources {
    @POST("/api/v1/accounts/{id}/follow")
    suspend fun follow(
        @Path(value = "id") id: String,
    ): Account

    @POST("/api/v1/accounts/{id}/unfollow")
    suspend fun unfollow(
        @Path(value = "id") id: String,
    ): Account

    @GET("/api/v1/accounts/relationships")
    suspend fun showFriendships(@Query("id[]") id: List<String>): List<RelationshipResponse>

    @POST("/api/v1/accounts/{id}/block")
    suspend fun block(
        @Path(value = "id") id: String,
    ): RelationshipResponse

    @POST("/api/v1/accounts/{id}/unblock")
    suspend fun unblock(
        @Path(value = "id") id: String,
    ): RelationshipResponse
}
