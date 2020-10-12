package com.twidere.services.twitter.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MediaPublicMetrics (
    @SerialName("view_count")
    val viewCount: Long? = null
)