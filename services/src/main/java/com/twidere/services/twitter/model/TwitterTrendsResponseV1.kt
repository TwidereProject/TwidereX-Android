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

import com.twidere.services.microblog.model.ITrend
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TwitterTrendsResponseV1(
    @SerialName("as_of")
    val asOf: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("locations")
    val locations: List<Location>? = null,
    @SerialName("trends")
    val trends: List<Trend>? = null
)

@Serializable
data class Trend(
    @SerialName("name")
    val name: String? = null,
    @SerialName("promoted_content")
    val promotedContent: String? = null,
    @SerialName("query")
    val query: String? = null,
    @SerialName("tweet_volume")
    val tweetVolume: Long? = null,
    @SerialName("url")
    val url: String? = null
) : ITrend

@Serializable
data class Location(
    @SerialName("name")
    val name: String? = null,
    @SerialName("woeid")
    val woeid: Int? = null
)
