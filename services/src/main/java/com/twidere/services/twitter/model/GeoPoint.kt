package com.twidere.services.twitter.model

import kotlinx.serialization.Serializable

@Serializable
data class GeoPoint(
    val coordinates: List<Double>? = null,
    val type: String? = null,
)