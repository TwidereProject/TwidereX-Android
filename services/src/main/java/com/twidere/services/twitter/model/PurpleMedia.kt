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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PurpleMedia(
    val id: Long? = null,

    @SerialName("id_str")
    val idStr: String? = null,

    val indices: List<Long>? = null,

    @SerialName("media_url")
    val mediaURL: String? = null,

    @SerialName("media_url_https")
    val mediaURLHTTPS: String? = null,

    val url: String? = null,

    @SerialName("display_url")
    val displayURL: String? = null,

    @SerialName("expanded_url")
    val expandedURL: String? = null,

    val type: String? = null,
    val sizes: Sizes? = null,

    @SerialName("source_status_id")
    val sourceStatusID: Double? = null,

    @SerialName("source_status_id_str")
    val sourceStatusIDStr: String? = null,

    @SerialName("source_user_id")
    val sourceUserID: Long? = null,

    @SerialName("source_user_id_str")
    val sourceUserIDStr: String? = null,

    @SerialName("video_info")
    val videoInfo: VideoInfo? = null,
)
