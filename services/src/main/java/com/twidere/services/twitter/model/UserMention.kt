package com.twidere.services.twitter.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserMention (
    @SerialName("screen_name")
    val screenName: String? = null,

    val name: String? = null,
    val id: Long? = null,

    @SerialName("id_str")
    val idStr: String? = null,

    val indices: List<Long>? = null
)