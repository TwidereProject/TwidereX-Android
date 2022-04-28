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
package com.twidere.services.twitter

import com.twidere.services.http.HttpClientFactory
import com.twidere.services.http.authorization.EmptyAuthorization
import com.twidere.services.twitter.api.TwitterOAuthV2Resources
import com.twidere.services.twitter.model.AccessTokenV2
import com.twidere.services.utils.queryString
import java.net.URLEncoder

class TwitterOAuthV2Service(
    private val clientId: String,
    private val httpClientFactory: HttpClientFactory,
) {

    suspend fun getAccessToken(
        codeVerifier: String,
        code: String,
    ): AccessTokenV2 {
        return httpClientFactory.createResources(
            TwitterOAuthV2Resources::class.java,
            TWITTER_BASE_URL,
            EmptyAuthorization(),
        ).token(
            clientId = clientId,
            code = code,
            codeVerifier = codeVerifier,
            grantType = "authorization_code",
            redirectUri = "",
        ).queryString()
    }

    fun getWebOAuthUrl(
        codeChallenge: String,
        redirectUri: String,
        state: String,
    ): String {
        return "https://twitter.com/i/oauth2/authorize" +
            "?response_type=code" +
            "&client_id=$clientId" +
            "&redirect_uri=${URLEncoder.encode(redirectUri, "UTF-8")}" +
            "&scope=$scopes" +
            "&state=$state" +
            "&code_challenge=$codeChallenge" +
            "&code_challenge_method=S256" +
            ""
    }

    // same scopes with ios
    private val scopes = listOf(
        "tweet.read",
        // "tweet.write",
        // "tweet.moderate.write",
        "users.read",
        "follows.read",
        "follows.write",
        "offline.access",
        // "space.read",
        // "mute.read",
        // "mute.write",
        // "like.read",
        // "like.write",
        // "list.read",
        // "list.write",
        // "block.read",
        // "block.write",
        "bookmark.read",
        // "bookmark.write",
    ).joinToString("%20")
}
