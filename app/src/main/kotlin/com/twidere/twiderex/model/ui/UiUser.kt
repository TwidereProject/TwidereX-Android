/*
 *  Twidere X
 *
 *  Copyright (C) 2020-2021 Tlaster <tlaster@outlook.com>
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
package com.twidere.twiderex.model.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.res.painterResource
import com.twidere.twiderex.R
import com.twidere.twiderex.db.model.DbMastodonUserExtra
import com.twidere.twiderex.db.model.DbTwitterUserExtra
import com.twidere.twiderex.db.model.DbUser
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.PlatformType

@Immutable
data class UiUser(
    val id: String,
    val userKey: MicroBlogKey,
    val acct: MicroBlogKey,
    val name: String,
    val screenName: String,
    val profileImage: Any,
    val profileBackgroundImage: String?,
    val followersCount: Long,
    val friendsCount: Long,
    val statusesCount: Long,
    val listedCount: Long,
    val rawDesc: String,
    val htmlDesc: String,
    val website: String?,
    val location: String?,
    val verified: Boolean,
    val protected: Boolean,
    val platformType: PlatformType,
    val twitterExtra: DbTwitterUserExtra? = null,
    val mastodonExtra: DbMastodonUserExtra? = null,
) {
    val displayName
        get() = name.takeUnless { it.isEmpty() } ?: screenName

    fun getDisplayScreenName(host: String?): String {
        return if (host != null && host != acct.host) {
            "@$screenName@${acct.host}"
        } else {
            "@$screenName"
        }
    }

    companion object {
        @Composable
        fun sample() = UiUser(
            id = "",
            name = "Twidere",
            screenName = "TwidereProject",
            profileImage = painterResource(id = R.drawable.ic_profile_image_twidere),
            profileBackgroundImage = null,
            followersCount = 0,
            friendsCount = 0,
            listedCount = 0,
            statusesCount = 0,
            rawDesc = "",
            htmlDesc = "",
            website = null,
            location = null,
            verified = false,
            protected = false,
            userKey = MicroBlogKey.Empty,
            platformType = PlatformType.Twitter,
            acct = MicroBlogKey.twitter("TwidereProject")
        )

        fun DbUser.toUi() =
            UiUser(
                id = userId,
                name = name,
                screenName = screenName,
                profileImage = profileImage,
                profileBackgroundImage = profileBackgroundImage,
                followersCount = followersCount,
                friendsCount = friendsCount,
                listedCount = listedCount,
                statusesCount = statusesCount,
                rawDesc = rawDesc,
                htmlDesc = htmlDesc,
                website = website,
                location = location,
                verified = verified,
                protected = isProtected,
                userKey = userKey,
                platformType = platformType,
                twitterExtra = twitterExtra,
                mastodonExtra = mastodonExtra,
                acct = acct,
            )
    }
}
