package com.twidere.services.twitter.model

import kotlinx.serialization.Serializable

@Serializable
data class ReferencedTweetV2 (
    val type: String? = null,
    val id: String? = null
)