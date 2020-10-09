package com.twidere.services.twitter.model

import kotlinx.serialization.Serializable

@Serializable
data class Description (
    val urls: List<URL>? = null,
    val hashtags: List<HashtagV2>? = null,
    val mentions: List<MentionV2>? = null,
)