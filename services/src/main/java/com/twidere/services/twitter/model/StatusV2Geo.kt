package com.twidere.services.twitter.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StatusV2Geo (
    val coordinates: Coordinates? = null,

    @SerialName("place_id")
    val placeID: String? = null
)