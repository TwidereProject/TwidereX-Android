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
import com.twidere.services.http.authorization.OAuth1Authorization
import com.twidere.services.twitter.api.TwitterOAuthResources
import com.twidere.services.twitter.model.AccessToken
import com.twidere.services.twitter.model.OAuthToken
import com.twidere.services.utils.queryString

class TwitterOAuthService(
    private val consumerKey: String,
    private val consumerSecret: String,
    private val httpClientFactory: HttpClientFactory,
) {
    suspend fun getOAuthToken(
        callback: String = "oob"
    ): OAuthToken {
        return httpClientFactory.createResources<TwitterOAuthResources>(
            TwitterOAuthResources::class.java,
            TWITTER_BASE_URL,
            OAuth1Authorization(
                consumerKey,
                consumerSecret,
            ),
        ).requestToken(callback).queryString()
    }

    suspend fun getAccessToken(pinCode: String, token: OAuthToken): AccessToken {
        return httpClientFactory.createResources<TwitterOAuthResources>(
            TwitterOAuthResources::class.java,
            TWITTER_BASE_URL,
            OAuth1Authorization(
                consumerKey,
                consumerSecret,
                token.oauth_token
            ),
        ).accessToken(pinCode).queryString()
    }

    fun getWebOAuthUrl(token: OAuthToken) =
        "https://api.twitter.com/oauth/authorize?oauth_token=${token.oauth_token}"
}
