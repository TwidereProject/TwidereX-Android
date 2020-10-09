package com.twidere.services.twitter.model

import kotlinx.serialization.Serializable

@Serializable
data class StatusV2Entities (
    val mentions: List<MentionV2>? = null,
    val hashtags: List<HashtagV2>? = null,
    val annotations: List<AnnotationV2>? = null,
    val urls: List<PurpleURLV2>? = null
)