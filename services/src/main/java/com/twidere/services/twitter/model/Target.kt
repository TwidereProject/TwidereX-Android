package com.twidere.services.twitter.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Target (
    val id: Long? = null,

    @SerialName("id_str")
    val idStr: String? = null,

    @SerialName("screen_name")
    val screenName: String? = null,

    val following: Boolean? = null,

    @SerialName("followed_by")
    val followedBy: Boolean? = null,

    @SerialName("following_received")
    val followingReceived: Boolean? = null,

    @SerialName("following_requested")
    val followingRequested: Boolean? = null
)