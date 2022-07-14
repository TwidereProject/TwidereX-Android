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
package com.twidere.twiderex.db.sqldelight.model

import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.ReferenceType
import kotlinx.serialization.Serializable

@Serializable
data class DbStatusReferenceList(
    val list: List<DbStatusReference>
)

@Serializable
data class DbStatusReference(
    val referenceType: ReferenceType,
    val statusKey: MicroBlogKey,
)

@Serializable
data class DbStatusMetrics(
    val like: Long,
    val reply: Long,
    val retweet: Long
)

@Serializable
data class DbGeo(
    val name: String,
    val lat: Long? = null,
    val long: Long? = null,
)

@Serializable
data class DbCard(
    val link: String,
    val displayLink: String?,
    val title: String?,
    val description: String?,
    val image: String?,
)

@Serializable
data class DbPoll(
    val id: String,
    val options: List<DbOption>,
    val expiresAt: Long?, // some instance of mastodon won't expire
    val expired: Boolean,
    val multiple: Boolean,
    val voted: Boolean,
    val votesCount: Long? = null,
    val votersCount: Long? = null,
    val ownVotes: List<Int>? = null,
)

@Serializable
data class DbOption(
    val text: String,
    val count: Long,
)
