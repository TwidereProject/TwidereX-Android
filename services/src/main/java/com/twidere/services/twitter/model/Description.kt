package com.twidere.services.twitter.model

import kotlinx.serialization.Serializable

@Serializable
data class Description (
    val urls: List<URL>? = null
)