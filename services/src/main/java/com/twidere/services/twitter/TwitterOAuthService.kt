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
package com.twidere.services.twitter

import com.twidere.services.http.authorization.OAuth1Authorization
import com.twidere.services.http.retrofit
import com.twidere.services.twitter.api.TwitterOAuthResources
import com.twidere.services.twitter.api.UsersResources
import com.twidere.services.twitter.model.AccessToken
import com.twidere.services.twitter.model.OAuthToken
import com.twidere.services.twitter.model.User
import com.twidere.services.utils.queryString

class TwitterOAuthService(
    private val consumerKey: String,
    private val consumerSecret: String,
) {
    suspend fun getOAuthToken(): OAuthToken {
        return retrofit<TwitterOAuthResources>(
            TWITTER_BASE_URL,
            OAuth1Authorization(
                consumerKey,
                consumerSecret,
            ),
        ).requestToken("oob").queryString()
    }

    suspend fun getAccessToken(pinCode: String, token: OAuthToken): AccessToken {
        return retrofit<TwitterOAuthResources>(
            TWITTER_BASE_URL,
            OAuth1Authorization(
                consumerKey,
                consumerSecret,
                token.oauth_token
            ),
        ).accessToken(pinCode).queryString()
    }

    suspend fun verifyCredentials(accessToken: AccessToken): User? {
        val usersResources = retrofit<UsersResources>(
            TWITTER_BASE_URL,
            OAuth1Authorization(consumerKey, consumerSecret, accessToken.oauth_token, accessToken.oauth_token_secret)
        )
        return usersResources.verifyCredentials()
    }

    fun getWebOAuthUrl(token: OAuthToken) =
        "https://api.twitter.com/oauth/authorize?oauth_token=${token.oauth_token}"
}
