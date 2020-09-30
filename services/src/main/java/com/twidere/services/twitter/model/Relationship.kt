package com.twidere.services.twitter.model

import kotlinx.serialization.Serializable

@Serializable
data class Relationship (
    val source: Source? = null,
    val target: Target? = null
)