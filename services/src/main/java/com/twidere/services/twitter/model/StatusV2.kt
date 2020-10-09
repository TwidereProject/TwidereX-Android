package com.twidere.services.twitter.model

import com.twidere.services.microblog.model.IStatus
import com.twidere.services.serializer.DateSerializerV2
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class StatusV2 (
    @SerialName("referenced_tweets")
    val referencedTweets: List<ReferencedTweetV2>? = null,

    val text: String? = null,

    @SerialName("possibly_sensitive")
    val possiblySensitive: Boolean? = null,

    val id: String? = null,
    val entities: StatusV2Entities? = null,
    val source: String? = null,

    @SerialName("conversation_id")
    val conversationID: String? = null,

    val lang: String? = null,

    @SerialName("author_id")
    val authorID: String? = null,

    @SerialName("created_at")
    @Serializable(with = DateSerializerV2::class)
    val createdAt: Date? = null,

    @SerialName("public_metrics")
    val publicMetrics: StatusV2PublicMetrics? = null,

    val attachments: AttachmentsV2? = null
): IStatus