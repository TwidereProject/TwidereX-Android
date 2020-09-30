package com.twidere.services.twitter

import com.twidere.services.http.authorization.OAuth1Authorization
import com.twidere.services.http.retrofit
import com.twidere.services.twitter.api.TwitterOAuthResource
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
        return retrofit<TwitterOAuthResource>(
            TWITTER_BASE_URL,
            OAuth1Authorization(
                consumerKey,
                consumerSecret,
            ),
        ).requestToken("oob").queryString()
    }

    suspend fun getAccessToken(pinCode: String, token: OAuthToken): AccessToken {
        return retrofit<TwitterOAuthResource>(
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