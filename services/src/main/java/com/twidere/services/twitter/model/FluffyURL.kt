package com.twidere.services.twitter.model

import kotlinx.serialization.Serializable

@Serializable
data class FluffyURL (
    val urls: List<DescriptionURL>? = null
)