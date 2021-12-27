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
data class SearchMetadataV1(
    @SerialName("completed_in")
    val completedIn: Double? = null,

    @SerialName("max_id")
    val maxID: Double? = null,

    @SerialName("max_id_str")
    val maxIDStr: String? = null,

    @SerialName("next_results")
    val nextResults: String? = null,

    val query: String? = null,

    @SerialName("refresh_url")
    val refreshURL: String? = null,

    val count: Long? = null,

    @SerialName("since_id")
    val sinceID: Long? = null,

    @SerialName("since_id_str")
    val sinceIDStr: String? = null
)
