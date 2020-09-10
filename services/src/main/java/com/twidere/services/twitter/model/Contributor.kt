package com.twidere.services.twitter.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Contributor(
    val id: Long? = null,
    @SerialName("screen_name")
    val screenName: String? = null,
)