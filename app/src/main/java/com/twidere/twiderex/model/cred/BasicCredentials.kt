package com.twidere.twiderex.model.cred

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BasicCredentials(
    val username: String,
    val password: String,
): Credentials