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
package com.twidere.twiderex.model

import com.twidere.twiderex.db.model.DbUser

data class AmUser(
    val userId: String,
    val name: String,
    val userKey: MicroBlogKey,
    val screenName: String,
    val profileImage: String,
    val profileBackgroundImage: String?,
    val followersCount: Long,
    val friendsCount: Long,
    val listedCount: Long,
    val desc: String,
    val website: String?,
    val location: String?,
    val verified: Boolean,
    val isProtected: Boolean,
)

fun DbUser.toAmUser() = AmUser(
    userId = userId,
    name = name,
    userKey = userKey,
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
    isProtected = isProtected,
)
