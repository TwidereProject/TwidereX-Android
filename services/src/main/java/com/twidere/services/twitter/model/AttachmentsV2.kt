package com.twidere.services.twitter.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AttachmentsV2 (
    @SerialName("media_keys")
    val mediaKeys: List<String>? = null
)