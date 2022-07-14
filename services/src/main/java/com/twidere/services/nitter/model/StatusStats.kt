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
package com.twidere.services.nitter.model

import com.twidere.services.nitter.model.serializer.StatsSerializer
import moe.tlaster.hson.annotations.HtmlSerializable

data class StatusStats(
    @HtmlSerializable(".tweet-stat .icon-comment", serializer = StatsSerializer::class)
    val comment: Int,
    @HtmlSerializable(".tweet-stat .icon-retweet", serializer = StatsSerializer::class)
    val retweet: Int,
    @HtmlSerializable(".tweet-stat .icon-quote", serializer = StatsSerializer::class)
    val quote: Int,
    @HtmlSerializable(".tweet-stat .icon-heart", serializer = StatsSerializer::class)
    val like: Int,
)
