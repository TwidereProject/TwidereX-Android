package com.twidere.services.twitter.model

import kotlinx.serialization.Serializable

@Serializable
data class AccessToken(
    val oauth_token: String,
    val oauth_token_secret: String,
    val user_id: String,
    val screen_name: String
)