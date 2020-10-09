package com.twidere.services.twitter.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PurpleURLV2 (
    val start: Long? = null,
    val end: Long? = null,
    val url: String? = null,

    @SerialName("expanded_url")
    val expandedURL: String? = null,

    @SerialName("display_url")
    val displayURL: String? = null,

    val images: List<ImageV2>? = null,
    val status: Long? = null,
    val title: String? = null,
    val description: String? = null,

    @SerialName("unwound_url")
    val unwoundURL: String? = null
)