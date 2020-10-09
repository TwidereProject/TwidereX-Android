package com.twidere.services.twitter.model

import kotlinx.serialization.Serializable

@Serializable
data class IncludesV2 (
    val users: List<User>? = null,
    val tweets: List<StatusV2>? = null,
    val media: List<MediaV2>? = null
)