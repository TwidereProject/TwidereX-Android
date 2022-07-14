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
import com.twidere.services.mastodon.model.CreateApplicationResponse
import com.twidere.services.mastodon.model.RequestTokenResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface MastodonOAuthResources {
    @POST("/api/v1/apps")
    @FormUrlEncoded
    suspend fun createApplication(
        @Field("client_name") client_name: String,
        @Field("redirect_uris") redirect_uris: String,
        @Field("scopes") scopes: String,
        @Field("website") website: String?,
    ): CreateApplicationResponse

    @GET("/api/v1/accounts/verify_credentials")
    suspend fun verifyCredentials(): Account

    @POST("/oauth/token")
    @FormUrlEncoded
    suspend fun requestToken(
        @Field("client_id") client_id: String,
        @Field("client_secret") client_secret: String,
        @Field("redirect_uri") redirect_uri: String,
        @Field("scope") scope: String,
        @Field("code") code: String,
        @Field("grant_type") grant_type: String,
    ): RequestTokenResponse
}
