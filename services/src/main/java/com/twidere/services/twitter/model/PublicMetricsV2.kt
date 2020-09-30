package com.twidere.services.twitter.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PublicMetricsV2 (
    @SerialName("followers_count")
    val followersCount: Long? = null,

    @SerialName("following_count")
    val followingCount: Long? = null,

    @SerialName("tweet_count")
    val tweetCount: Long? = null,

    @SerialName("listed_count")
    val listedCount: Long? = null
)