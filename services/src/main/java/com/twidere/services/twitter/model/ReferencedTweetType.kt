package com.twidere.services.twitter.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ReferencedTweetType {
    @SerialName("retweeted")
    retweeted,
    @SerialName("quoted")
    quoted,
    @SerialName("replied_to")
    replied_to,
}