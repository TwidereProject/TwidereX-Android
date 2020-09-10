package com.twidere.services.twitter.model

import kotlinx.serialization.Serializable

@Serializable
data class OAuthToken(
    val oauth_token: String,
    val oauth_token_secret: String,
)