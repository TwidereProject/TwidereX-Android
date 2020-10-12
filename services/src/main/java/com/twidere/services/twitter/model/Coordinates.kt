package com.twidere.services.twitter.model

import kotlinx.serialization.Serializable

@Serializable
data class Coordinates (
    val type: String? = null,
    val coordinates: List<Double>? = null
)