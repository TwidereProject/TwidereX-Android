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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RelationshipResponse(
    val id: String? = null,
    val following: Boolean? = null,

    @SerialName("showing_reblogs")
    val showingReblogs: Boolean? = null,

    val notifying: Boolean? = null,

    @SerialName("followed_by")
    val followedBy: Boolean? = null,

    val blocking: Boolean? = null,

    @SerialName("blocked_by")
    val blockedBy: Boolean? = null,

    val muting: Boolean? = null,

    @SerialName("muting_notifications")
    val mutingNotifications: Boolean? = null,

    val requested: Boolean? = null,

    @SerialName("domain_blocking")
    val domainBlocking: Boolean? = null,

    val endorsed: Boolean? = null,
    val note: String? = null
)
