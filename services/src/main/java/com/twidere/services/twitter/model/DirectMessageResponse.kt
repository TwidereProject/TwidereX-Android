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

import com.twidere.services.microblog.model.IDirectMessage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DirectMessageResponse(
    @SerialName("events")
    val events: List<DirectMessageEvent>? = null,
    @SerialName("next_cursor")
    val nextCursor: String? = null
)
@Serializable
data class DirectMessageEventObject(
    val event: DirectMessageEvent? = null
)

@Serializable
data class DirectMessageEvent(
    @SerialName("created_timestamp")
    val createdTimestamp: String? = null,
    @SerialName("id")
    val id: String? = null,
    @SerialName("message_create")
    val messageCreate: MessageCreate? = null,
    @SerialName("type")
    val type: String? = null
) : IDirectMessage

@Serializable
data class MessageCreate(
    @SerialName("message_data")
    val messageData: MessageData? = null,
    @SerialName("sender_id")
    val senderId: String? = null,
    @SerialName("source_app_id")
    val sourceAppId: String? = null,
    @SerialName("target")
    val target: MessageTarget? = null
)

@Serializable
data class MessageTarget(
    @SerialName("recipient_id")
    val recipientId: String? = null
)

@Serializable
data class MessageData(
    @SerialName("entities")
    val entities: Entities? = null,
    @SerialName("text")
    val text: String? = null,
    @SerialName("quick_reply_response")
    val quickReplyResponse: String? = null,
    @SerialName("attachment")
    val attachment: Attachment? = null
)

@Serializable
data class Attachment(
    @SerialName("type")
    val type: String? = null,
    @SerialName("media")
    val media: PurpleMedia? = null,
)
