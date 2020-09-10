package com.twidere.services.twitter.api

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface TwitterOAuthResource {
    @FormUrlEncoded
    @POST("/oauth/request_token")
    suspend fun requestToken(@Field("oauth_callback") oauthCallback: String): String

    @FormUrlEncoded
    @POST("/oauth/access_token")
    suspend fun accessToken(@Field("oauth_verifier") oauthVerifier: String): String
}