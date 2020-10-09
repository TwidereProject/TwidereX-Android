package com.twidere.services.twitter.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StatusV2PublicMetrics (
    @SerialName("retweet_count")
    val retweetCount: Long? = null,

    @SerialName("reply_count")
    val replyCount: Long? = null,

    @SerialName("like_count")
    val likeCount: Long? = null,

    @SerialName("quote_count")
    val quoteCount: Long? = null
)