package com.twidere.services.twitter.model

import kotlinx.serialization.Serializable

@Serializable
data class ImageV2 (
    val url: String? = null,
    val width: Long? = null,
    val height: Long? = null
)