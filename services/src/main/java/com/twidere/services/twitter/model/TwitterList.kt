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

import com.twidere.services.microblog.model.IListModel
import com.twidere.services.serializer.DateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class TwitterList(
    @SerialName("created_at")
    @Serializable(with = DateSerializer::class)
    val createdAt: Date? = null,

    val id: Long? = null,

    val description: String? = null,

    val following: Boolean? = null,

    @SerialName("full_name")
    val fullName: String? = null,

    @SerialName("id_str")
    val idStr: String? = null,

    @SerialName("member_count")
    val memberCount: Int? = null,

    val mode: String? = null,

    val name: String? = null,

    val slug: String? = null,

    @SerialName("subscriber_count")
    val subscriberCount: Int? = null,

    val uri: String? = null,

    val user: User? = null
) : IListModel
