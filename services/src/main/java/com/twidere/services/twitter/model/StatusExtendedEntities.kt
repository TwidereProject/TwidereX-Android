package com.twidere.services.twitter.model

import kotlinx.serialization.Serializable

@Serializable
data class StatusExtendedEntities (
    val media: List<PurpleMedia>? = null
)