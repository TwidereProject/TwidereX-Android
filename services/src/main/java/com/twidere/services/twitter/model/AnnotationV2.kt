package com.twidere.services.twitter.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnnotationV2 (
    val start: Long? = null,
    val end: Long? = null,
    val probability: Double? = null,
    val type: String? = null,

    @SerialName("normalized_text")
    val normalizedText: String? = null
)