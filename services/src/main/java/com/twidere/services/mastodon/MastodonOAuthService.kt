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
import com.twidere.services.http.authorization.EmptyAuthorization
import com.twidere.services.http.retrofit
import com.twidere.services.mastodon.api.MastodonOAuthResources
import com.twidere.services.mastodon.model.CreateApplicationResponse
import com.twidere.services.mastodon.model.MastodonAuthScope

class MastodonOAuthService(
    private val host: String,
    private val client_name: String,
    private val website: String? = null,
    private val redirect_uri: String = "urn:ietf:wg:oauth:2.0:oob",
    private val scopes: List<MastodonAuthScope> = listOf(
        MastodonAuthScope.read,
        MastodonAuthScope.write,
        MastodonAuthScope.follow,
        MastodonAuthScope.push,
    ),
) {
    private val resources by lazy {
        retrofit<MastodonOAuthResources>(
            host,
            EmptyAuthorization()
        )
    }

    suspend fun createApplication() = resources.createApplication(
        client_name = client_name,
        redirect_uris = redirect_uri,
        scopes = scopes.joinToString(" ") { it.name },
        website = website,
    )

    fun getWebOAuthUrl(response: CreateApplicationResponse) =
        "$host/oauth/authorize?client_id=${response.clientID}&response_type=code&redirect_uri=${response.redirectURI}&scope=${
        scopes.joinToString(
            " "
        ) { it.name }
        }"

    suspend fun getAccessToken(code: String, response: CreateApplicationResponse) =
        resources.requestToken(
            client_id = response.clientID,
            client_secret = response.clientSecret,
            redirect_uri = response.redirectURI,
            scope = scopes.joinToString(" ") { it.name },
            code = code,
            grant_type = "authorization_code",
        )

    suspend fun verifyCredentials(accessToken: String) =
        retrofit<MastodonOAuthResources>(
            host,
            BearerAuthorization(accessToken)
        ).verifyCredentials()
}
