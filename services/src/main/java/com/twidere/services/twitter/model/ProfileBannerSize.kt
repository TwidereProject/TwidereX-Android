package com.twidere.services.twitter.model

import kotlinx.serialization.Serializable

@Serializable
data class ProfileBannerSize (
    val h: Long? = null,
    val w: Long? = null,
    val url: String? = null
)