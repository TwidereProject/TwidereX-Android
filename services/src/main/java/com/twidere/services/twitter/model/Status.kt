package com.twidere.services.twitter.model

import com.twidere.services.microblog.model.IStatus
import com.twidere.services.serializer.DateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Status(
    @SerialName("created_at")
    @Serializable(with = DateSerializer::class)
    val createdAt: Date? = null,

    val id: Long? = null,

    @SerialName("id_str")
    val idStr: String? = null,

    val text: String? = null,
    val truncated: Boolean? = null,
    val entities: StatusEntities? = null,

    @SerialName("extended_entities")
    val extendedEntities: StatusExtendedEntities? = null,

    val source: String? = null,

    @SerialName("in_reply_to_status_id")
    val inReplyToStatusID: String? = null,

    @SerialName("in_reply_to_status_id_str")
    val inReplyToStatusIDStr: String? = null,

    @SerialName("in_reply_to_user_id")
    val inReplyToUserID: String? = null,

    @SerialName("in_reply_to_user_id_str")
    val inReplyToUserIDStr: String? = null,

    @SerialName("in_reply_to_screen_name")
    val inReplyToScreenName: String? = null,

    val geo: GeoPoint? = null,
//    val coordinates: Any? = null,
    val place: Place? = null,
    val contributors: List<Contributor>? = null,

    @SerialName("retweeted_status")
    val retweetedStatus: Status? = null,

    @SerialName("quoted_status")
    val quotedStatus: Status? = null,

    @SerialName("is_quote_status")
    val isQuoteStatus: Boolean? = null,

    @SerialName("retweet_count")
    val retweetCount: Long? = null,

    @SerialName("favorite_count")
    val favoriteCount: Long? = null,

    val favorited: Boolean? = null,
    val retweeted: Boolean? = null,

    @SerialName("possibly_sensitive")
    val possiblySensitive: Boolean? = null,

    val lang: String? = null,
    @SerialName("full_text")
    val fullText: String? = null,
    @SerialName("display_text_range")
    val displayTextRange: List<Long>? = null,
    @SerialName("user")
    val user: User? = null,
) : IStatus


