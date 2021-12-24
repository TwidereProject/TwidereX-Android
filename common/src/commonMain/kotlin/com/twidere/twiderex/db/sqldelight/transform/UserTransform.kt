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
package com.twidere.twiderex.db.sqldelight.transform

import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.model.ui.UserExtra
import com.twidere.twiderex.model.ui.UserMetrics
import com.twidere.twiderex.model.ui.mastodon.MastodonUserExtra
import com.twidere.twiderex.model.ui.twitter.TwitterUserExtra
import com.twidere.twiderex.sqldelight.table.DbUser
import com.twidere.twiderex.utils.fromJson
import com.twidere.twiderex.utils.json

fun UiUser.toDbUser() = DbUser(
    id = id,
    userKey = userKey,
    acct = acct,
    name = name,
    screenName = screenName,
    profileImage = profileImage.toString(),
    profileBackgroundImage = profileBackgroundImage,
    fans = metrics.fans,
    follow = metrics.follow,
    status = metrics.status,
    listed = metrics.listed,
    rawDesc = rawDesc,
    htmlDesc = htmlDesc,
    website = website,
    location = location,
    verified = verified,
    protected_ = protected,
    platformType = platformType,
    extra = extra?.toDbUserExtra()
)

fun UserExtra.toDbUserExtra(): String {
    return when (this) {
        is TwitterUserExtra -> { json() }
        is MastodonUserExtra -> { json() }
        else -> toString()
    }
}

fun DbUser.toUi() = UiUser(
    id = id,
    userKey = userKey,
    acct = acct,
    name = name,
    screenName = screenName,
    profileImage = profileImage,
    profileBackgroundImage = profileBackgroundImage,
    metrics = UserMetrics(
        fans = fans,
        follow = follow,
        status = status,
        listed = listed,
    ),
    rawDesc = rawDesc,
    htmlDesc = htmlDesc,
    website = website,
    location = location,
    verified = verified,
    protected = protected_,
    platformType = platformType,
    extra = when (platformType) {
        PlatformType.Twitter -> extra?.fromJson<TwitterUserExtra>()
        PlatformType.StatusNet -> TODO()
        PlatformType.Fanfou -> TODO()
        PlatformType.Mastodon -> extra?.fromJson<MastodonUserExtra>()
    }
)
