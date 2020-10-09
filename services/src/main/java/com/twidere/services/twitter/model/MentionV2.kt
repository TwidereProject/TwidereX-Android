package com.twidere.services.twitter.model

import kotlinx.serialization.Serializable

@Serializable
data class MentionV2 (
    val start: Long? = null,
    val end: Long? = null,
    val username: String? = null
)