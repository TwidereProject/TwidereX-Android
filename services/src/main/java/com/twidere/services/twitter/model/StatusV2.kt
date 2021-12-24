/*
 *  Twidere X
 *
 *  Copyright (C) TwidereProject and Contributors
 * 
 *  This file is part of Twidere X.
 * 
 *  Twidere X is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Twidere X is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Twidere X. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.services.twitter.model

import com.twidere.services.microblog.model.IStatus
import com.twidere.services.serializer.DateSerializerV2
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.Date

@Serializable
data class StatusV2(
    @SerialName("referenced_tweets")
    val referencedTweets: List<ReferencedTweetV2>? = null,

    val text: String? = null,

    @SerialName("possibly_sensitive")
    val possiblySensitive: Boolean? = null,

    val id: String? = null,
    val entities: StatusV2Entities? = null,
    val source: String? = null,
    val geo: StatusV2Geo? = null,

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

    @SerialName("reply_settings")
    val replySettings: ReplySettings? = null,

    val attachments: AttachmentsV2? = null,

    @SerialName("in_reply_to_user_id")
    val inReplyToUserId: String? = null
) : IStatus {
    internal fun setExtra(includesV2: IncludesV2) {
        if (authorID != null) {
            user = includesV2.users?.firstOrNull { it.id == authorID }
        }
        if (!referencedTweets.isNullOrEmpty()) {
            referencedTweets.forEach {
                it.setExtra(includesV2)
            }
        }
        attachments?.setExtra(includesV2)

        place = geo?.let { geo ->
            includesV2.places?.firstOrNull { it.id == geo.placeID }
        }
    }

    @Transient
    var user: UserV2? = null

    @Transient
    var place: PlaceV2? = null
}

@Serializable
enum class ReplySettings {
    @SerialName("everyone")
    Everyone,
    @SerialName("mentionedUsers")
    MentionedUsers,
    @SerialName("following")
    FollowingUsers,
}
