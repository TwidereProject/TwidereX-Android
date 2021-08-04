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
package com.twidere.twiderex.model.transform

import com.twidere.twiderex.db.model.DbMastodonUserExtra
import com.twidere.twiderex.db.model.DbTwitterUserExtra
import com.twidere.twiderex.db.model.DbUser
import com.twidere.twiderex.model.AmUser
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.ui.UiUrlEntity
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.model.ui.UserMetrics
import com.twidere.twiderex.model.ui.mastodon.Field
import com.twidere.twiderex.model.ui.mastodon.MastodonUserExtra
import com.twidere.twiderex.model.ui.twitter.TwitterUserExtra
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

fun DbUser.toAmUser() =
    AmUser(
        userId = userId,
        name = name,
        userKey = userKey,
        screenName = screenName,
        profileImage = profileImage,
        profileBackgroundImage = profileBackgroundImage,
        followersCount = followersCount,
        friendsCount = friendsCount,
        listedCount = listedCount,
        desc = rawDesc,
        website = website,
        location = location,
        verified = verified,
        isProtected = isProtected,
    )

fun DbUser.toUi() =
    UiUser(
        id = userId,
        name = name,
        screenName = screenName,
        profileImage = profileImage,
        profileBackgroundImage = profileBackgroundImage,
        metrics = UserMetrics(
            fans = followersCount,
            follow = friendsCount,
            listed = listedCount,
            status = statusesCount,
        ),
        rawDesc = rawDesc,
        htmlDesc = htmlDesc,
        website = website,
        location = location,
        verified = verified,
        protected = isProtected,
        userKey = userKey,
        platformType = platformType,
        extra = when (platformType) {
            PlatformType.Twitter -> Json.decodeFromString<DbTwitterUserExtra>(extra).toUi()
            PlatformType.StatusNet -> TODO()
            PlatformType.Fanfou -> TODO()
            PlatformType.Mastodon -> Json.decodeFromString<DbMastodonUserExtra>(extra).toUi()
        },
        acct = acct,
    )

fun DbTwitterUserExtra.toUi() = TwitterUserExtra(
    pinned_tweet_id = pinned_tweet_id,
    url = url.map { url ->
        UiUrlEntity(
            url = url.displayUrl,
            expandedUrl = url.expandedUrl,
            displayUrl = url.displayUrl,
            title = null,
            description = null,
            image = null
        )
    }
)

fun DbMastodonUserExtra.toUi() = MastodonUserExtra(
    emoji = emoji.toUi(),
    bot = bot,
    locked = locked,
    fields = fields.map { field ->
        Field(
            field.name,
            field.value
        )
    }
)
