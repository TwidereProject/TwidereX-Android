package com.twidere.services.twitter.model

import kotlinx.serialization.Serializable

@Serializable
data class Sizes (
    val medium: MediaSize? = null,
    val large: MediaSize? = null,
    val small: MediaSize? = null,
    val thumb: MediaSize? = null
)