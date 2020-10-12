package com.twidere.services.twitter.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlaceV2 (
    @SerialName("country_code")
    val countryCode: String? = null,

    val geo: PlaceGeo? = null,

    @SerialName("place_type")
    val placeType: String? = null,

    val id: String? = null,

    @SerialName("full_name")
    val fullName: String? = null,

    val country: String? = null,
    val name: String? = null
)