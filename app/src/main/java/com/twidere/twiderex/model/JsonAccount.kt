package com.twidere.twiderex.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class JsonAccount(
    val name: String,
    val type: String,
)