package com.twidere.services.twitter.model

import kotlinx.serialization.Serializable

@Serializable
data class UserEntities (
    val url: Description? = null,
    val description: Description? = null
)