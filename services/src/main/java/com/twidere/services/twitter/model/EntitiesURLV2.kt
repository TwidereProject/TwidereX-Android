package com.twidere.services.twitter.model

import kotlinx.serialization.Serializable

@Serializable
data class EntitiesURLV2 (
    val urls: List<URLElementV2>? = null
)