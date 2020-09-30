package com.twidere.services.twitter.model

import com.twidere.services.microblog.model.IUser
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserV2 (
    val id: String? = null,
    val protected: Boolean? = null,

    @SerialName("profile_image_url")
    val profileImageURL: String? = null,

    val verified: Boolean? = null,
    val name: String? = null,
    val entities: EntitiesV2? = null,

    @SerialName("public_metrics")
    val publicMetrics: PublicMetricsV2? = null,

    val description: String? = null,
    val location: String? = null,
    val url: String? = null,

    @SerialName("pinned_tweet_id")
    val pinnedTweetID: String? = null,

    val username: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    var profileBanner: ProfileBanner? = null
) : IUser