package com.twidere.services.twitter.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TwitterErrorV2 (
    @SerialName("resource_type")
    val resourceType: String? = null,
    val field: String? = null,
    val parameter: String? = null,
    val value: String? = null,
    val title: String? = null,
    val section: String? = null,
    val detail: String? = null,
    val type: String? = null
)