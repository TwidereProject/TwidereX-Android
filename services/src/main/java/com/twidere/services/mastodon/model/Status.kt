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
package com.twidere.services.mastodon.model

import com.twidere.services.microblog.model.IStatus
import com.twidere.services.serializer.DateSerializerV2
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class Status(
    val id: String? = null,

    @SerialName("created_at")
    @Serializable(with = DateSerializerV2::class)
    val createdAt: Date? = null,

    @SerialName("in_reply_to_id")
    val inReplyToID: String? = null,

    @SerialName("in_reply_to_account_id")
    val inReplyToAccountID: String? = null,

    val sensitive: Boolean? = null,

    @SerialName("spoiler_text")
    val spoilerText: String? = null,

    val visibility: Visibility? = null,
    val language: String? = null,
    val uri: String? = null,
    val url: String? = null,

    @SerialName("replies_count")
    val repliesCount: Long? = null,

    @SerialName("reblogs_count")
    val reblogsCount: Long? = null,

    @SerialName("favourites_count")
    val favouritesCount: Long? = null,

    val favourited: Boolean? = null,
    val reblogged: Boolean? = null,
    val muted: Boolean? = null,
    val bookmarked: Boolean? = null,
    val content: String? = null,
    val reblog: Status? = null,
    val application: Application? = null,
    val account: Account? = null,

    @SerialName("media_attachments")
    val mediaAttachments: List<Attachment>? = null,

    val mentions: List<Mention>? = null,
    val tags: List<Tag>? = null,
    val emojis: List<Emoji>? = null,
    val card: Card? = null,
    val poll: Poll? = null,
    val pinned: Boolean? = null,
) : IStatus
