package com.twidere.twiderex.model.cred

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OAuthCredentials(
    val consumer_key: String,
    val consumer_secret: String,
    val access_token: String,
    val access_token_secret: String,
): Credentials