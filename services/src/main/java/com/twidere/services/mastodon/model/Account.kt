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

import com.twidere.services.microblog.model.IUser
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Account(
    val id: String? = null,
    val username: String? = null,
    val acct: String? = null,

    @SerialName("display_name")
    val displayName: String? = null,

    val locked: Boolean? = null,
    val bot: Boolean? = null,
    val discoverable: Boolean? = null,
    val group: Boolean? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    val note: String? = null,
    val url: String? = null,
    val avatar: String? = null,

    @SerialName("avatar_static")
    val avatarStatic: String? = null,

    val header: String? = null,

    @SerialName("header_static")
    val headerStatic: String? = null,

    @SerialName("followers_count")
    val followersCount: Long? = null,

    @SerialName("following_count")
    val followingCount: Long? = null,

    @SerialName("statuses_count")
    val statusesCount: Long? = null,

    @SerialName("last_status_at")
    val lastStatusAt: String? = null,

    val emojis: List<Emoji>? = null,
    val fields: List<Field>? = null,
    val source: Source? = null,
) : IUser
