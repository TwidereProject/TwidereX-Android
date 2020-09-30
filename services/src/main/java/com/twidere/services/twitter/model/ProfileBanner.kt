package com.twidere.services.twitter.model

import kotlinx.serialization.Serializable

@Serializable
data class ProfileBanner (
    val sizes: Map<String, ProfileBannerSize>? = null
)