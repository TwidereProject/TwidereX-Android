package com.twidere.services.twitter

import com.twidere.services.http.authorization.OAuthAuthorization
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
    private val twitterOAuth by lazy {
        retrofit<TwitterOAuthResource>(
            TWITTER_BASE_URL,
            OAuthAuthorization(
                consumerKey,
                consumerSecret,
            ),
        )
    }

    suspend fun getOAuthToken(): OAuthToken {
        return twitterOAuth.requestToken("oob").queryString()
    }

    suspend fun getAccessToken(pinCode: String): AccessToken {
        return twitterOAuth.accessToken(pinCode).queryString()
    }

    suspend fun verifyCredentials(accessToken: AccessToken): User? {
        val usersResources = retrofit<UsersResources>(
            TWITTER_BASE_URL,
            OAuthAuthorization(consumerKey, consumerSecret, accessToken.oauth_token, accessToken.oauth_token_secret)
        )
        return usersResources.verifyCredentials()
    }

    fun getWebOAuthUrl(token: OAuthToken) =
        "https://api.twitter.com/oauth/authorize?oauth_token=${token.oauth_token}"
}