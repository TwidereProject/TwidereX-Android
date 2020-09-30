package com.twidere.services.twitter.model

import kotlinx.serialization.Serializable

@Serializable
data class RelationshipResponse (
    val relationship: Relationship? = null
)