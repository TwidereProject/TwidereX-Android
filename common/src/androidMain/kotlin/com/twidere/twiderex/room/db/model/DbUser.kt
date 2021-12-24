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
package com.twidere.twiderex.room.db.model

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.PlatformType
import kotlinx.serialization.Serializable

@Entity(
    tableName = "user",
    indices = [Index(value = ["userKey"], unique = true)],
)
internal data class DbUser(
    /**
     * Id that being used in the database
     */
    @PrimaryKey
    var _id: String,
    val userId: String,
    val name: String,
    val userKey: MicroBlogKey,
    val acct: MicroBlogKey,
    val screenName: String,
    val profileImage: String,
    val profileBackgroundImage: String?,
    val followersCount: Long,
    val friendsCount: Long,
    val listedCount: Long,
    val htmlDesc: String,
    val rawDesc: String,
    val website: String?,
    val location: String?,
    val verified: Boolean,
    val isProtected: Boolean,
    val platformType: PlatformType,
    val statusesCount: Long,
    val extra: Json
)

@Immutable
@Serializable
internal data class DbTwitterUserExtra(
    val pinned_tweet_id: String?,
    val url: List<TwitterUrlEntity>,
)

@Immutable
@Serializable
internal data class TwitterUrlEntity(
    val url: String,
    val expandedUrl: String,
    val displayUrl: String,
)

@Immutable
@Serializable
internal data class DbMastodonUserExtra(
    val fields: List<com.twidere.services.mastodon.model.Field>,
    val emoji: List<com.twidere.services.mastodon.model.Emoji>,
    val bot: Boolean,
    val locked: Boolean,
)
