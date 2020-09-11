package com.twidere.twiderex.model.cred

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OAuth2Credentials(
    val access_token: String,
) : Credentials