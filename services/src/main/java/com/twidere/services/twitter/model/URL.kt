package com.twidere.services.twitter.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class URL (
    val url: String? = null,

    @SerialName("expanded_url")
    val expandedURL: String? = null,

    @SerialName("display_url")
    val displayURL: String? = null,

    val indices: List<Long>? = null
)