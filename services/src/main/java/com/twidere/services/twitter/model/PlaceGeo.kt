package com.twidere.services.twitter.model

import kotlinx.serialization.Serializable

@Serializable
data class PlaceGeo (
    val type: String? = null,
    val bbox: List<Double>? = null,
//    val properties: Any? = null
)