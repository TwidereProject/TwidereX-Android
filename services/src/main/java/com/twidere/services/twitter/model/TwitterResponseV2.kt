package com.twidere.services.twitter.model

import kotlinx.serialization.Serializable

@Serializable
data class TwitterResponseV2<T> (
    val data: T? = null,
    val errors: List<TwitterErrorV2>? = null
)