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
import com.twidere.services.mastodon.model.MastodonList
import com.twidere.services.mastodon.model.PostAccounts
import com.twidere.services.mastodon.model.PostList
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ListsResources {
    @GET("/api/v1/lists")
    suspend fun lists(): List<MastodonList>

    @POST("/api/v1/lists")
    suspend fun createList(@Body postList: PostList): MastodonList

    @PUT("/api/v1/lists/{id}")
    suspend fun updateList(
        @Path("id") id: String,
        @Body postList: PostList
    ): MastodonList

    @DELETE("/api/v1/lists/{id}")
    suspend fun deleteList(@Path("id") id: String): Response<String>

    @GET("/api/v1/lists/{id}/accounts")
    suspend fun listMembers(
        @Path("id") listId: String,
        @Query("max_id") max_id: String? = null,
        @Query("since_id") since_id: String? = null,
        @Query("limit") limit: Int = 20,
    ): Response<List<Account>>

    @POST("/api/v1/lists/{id}/accounts")
    suspend fun addMember(
        @Path("id") listId: String,
        @Body accounts: PostAccounts
    ): Response<String>

    @HTTP(method = "DELETE", path = "/api/v1/lists/{id}/accounts", hasBody = true)
    suspend fun removeMember(
        @Path("id") listId: String,
        @Body accounts: PostAccounts
    ): Response<String>
}
