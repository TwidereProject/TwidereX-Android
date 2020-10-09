package com.twidere.services.twitter.model

import kotlinx.serialization.Serializable

@Serializable
data class HashtagV2 (
    val start: Long? = null,
    val end: Long? = null,
    val tag: String? = null
)