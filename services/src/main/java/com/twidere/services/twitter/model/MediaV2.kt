package com.twidere.services.twitter.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MediaV2 (
    val url: String? = null,
    val height: Long? = null,

    @SerialName("media_key")
    val mediaKey: String? = null,

    val type: String? = null,
    val width: Long? = null
)