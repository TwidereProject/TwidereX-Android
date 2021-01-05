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
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.res.imageResource
import com.twidere.twiderex.R
import com.twidere.twiderex.db.model.DbUser
import com.twidere.twiderex.model.MicroBlogKey

data class UiUser(
    val id: String,
    val userKey: MicroBlogKey,
    val name: String,
    val screenName: String,
    val profileImage: Any,
    val profileBackgroundImage: String?,
    val followersCount: Long,
    val friendsCount: Long,
    val listedCount: Long,
    val desc: String,
    val website: String?,
    val location: String?,
    val verified: Boolean,
    val protected: Boolean,
) {
    companion object {
        @Composable
        fun sample() = UiUser(
            id = "",
            name = "Twidere",
            screenName = "TwidereProject",
            profileImage = imageResource(id = R.drawable.ic_profile_image_twidere).asAndroidBitmap(),
            profileBackgroundImage = null,
            followersCount = 0,
            friendsCount = 0,
            listedCount = 0,
            desc = "",
            website = null,
            location = null,
            verified = false,
            protected = false,
            userKey = MicroBlogKey.Empty,
        )

        fun DbUser.toUi() = UiUser(
            id = userId,
            name = name,
            screenName = screenName,
            profileImage = profileImage,
            profileBackgroundImage = profileBackgroundImage,
            followersCount = followersCount,
            friendsCount = friendsCount,
            listedCount = listedCount,
            desc = desc,
            website = website,
            location = location,
            verified = verified,
            protected = isProtected,
            userKey = userKey,
        )
    }
}
