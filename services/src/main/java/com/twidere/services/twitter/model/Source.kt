package com.twidere.services.twitter.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Source (
    val id: Double? = null,

    @SerialName("id_str")
    val idStr: String? = null,

    @SerialName("screen_name")
    val screenName: String? = null,

    val following: Boolean? = null,

    @SerialName("followed_by")
    val followedBy: Boolean? = null,

    @SerialName("live_following")
    val liveFollowing: Boolean? = null,

    @SerialName("following_received")
    val followingReceived: Boolean? = null,

    @SerialName("following_requested")
    val followingRequested: Boolean? = null,

    @SerialName("notifications_enabled")
    val notificationsEnabled: Boolean? = null,

    @SerialName("can_dm")
    val canDm: Boolean? = null,

    val blocking: Boolean? = null,

    @SerialName("blocked_by")
    val blockedBy: Boolean? = null,

    val muting: Boolean? = null,

    @SerialName("want_retweets")
    val wantRetweets: Boolean? = null,

    @SerialName("all_replies")
    val allReplies: Boolean? = null,

    @SerialName("marked_spam")
    val markedSpam: Boolean? = null
)