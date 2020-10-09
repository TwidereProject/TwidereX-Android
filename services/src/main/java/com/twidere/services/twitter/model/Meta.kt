package com.twidere.services.twitter.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Meta (
    @SerialName("newest_id")
    val newestID: String? = null,

    @SerialName("oldest_id")
    val oldestID: String? = null,

    @SerialName("result_count")
    val resultCount: Long? = null,

    @SerialName("next_token")
    val nextToken: String? = null
)