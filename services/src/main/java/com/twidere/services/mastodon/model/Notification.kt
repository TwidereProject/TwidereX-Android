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

import com.twidere.services.microblog.model.INotification
import com.twidere.services.serializer.DateSerializerV2
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class Notification(
    val id: String? = null,
    val type: NotificationTypes? = null,

    @SerialName("created_at")
    @Serializable(with = DateSerializerV2::class)
    val createdAt: Date? = null,

    val account: Account? = null,
    val status: Status? = null
) : INotification

@Serializable
enum class NotificationTypes {
    @SerialName("follow")
    follow,
    @SerialName("favourite")
    favourite,
    @SerialName("reblog")
    reblog,
    @SerialName("mention")
    mention,
    @SerialName("poll")
    poll,
    @SerialName("follow_request")
    follow_request,
    @SerialName("status")
    status,
}
