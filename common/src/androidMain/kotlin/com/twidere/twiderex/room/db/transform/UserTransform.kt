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
package com.twidere.twiderex.room.db.transform

import com.twidere.services.mastodon.model.Emoji
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.ui.UiUrlEntity
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.model.ui.UserMetrics
import com.twidere.twiderex.model.ui.mastodon.Field
import com.twidere.twiderex.model.ui.mastodon.MastodonUserExtra
import com.twidere.twiderex.model.ui.twitter.TwitterUserExtra
import com.twidere.twiderex.room.db.model.DbMastodonUserExtra
import com.twidere.twiderex.room.db.model.DbTwitterUserExtra
import com.twidere.twiderex.room.db.model.DbUser
import com.twidere.twiderex.room.db.model.TwitterUrlEntity
import com.twidere.twiderex.utils.fromJson
import com.twidere.twiderex.utils.json
import java.util.UUID

internal fun DbUser.toUi() =
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
        extra = try {
            when (platformType) {
                PlatformType.Twitter -> extra.fromJson<DbTwitterUserExtra>().toUi()
                PlatformType.StatusNet -> TODO()
                PlatformType.Fanfou -> TODO()
                PlatformType.Mastodon -> extra.fromJson<DbMastodonUserExtra>().toUi()
            }
        } catch (e: Throwable) {
            null
        },
        acct = acct,
    )

internal fun UiUser.toDbUser(dbId: String = UUID.randomUUID().toString()) =
    DbUser(
        _id = dbId,
        name = name,
        screenName = screenName,
        profileImage = profileImage.toString(),
        profileBackgroundImage = profileBackgroundImage,

        rawDesc = rawDesc,
        htmlDesc = htmlDesc,
        website = website,
        location = location,
        verified = verified,
        userKey = userKey,
        platformType = platformType,
        extra = when (extra) {
            is TwitterUserExtra -> DbTwitterUserExtra(
                pinned_tweet_id = extra.pinned_tweet_id,
                url = extra.url.map {
                    TwitterUrlEntity(
                        url = it.url,
                        expandedUrl = it.expandedUrl,
                        displayUrl = it.displayUrl
                    )
                }
            ).json()
            is MastodonUserExtra -> DbMastodonUserExtra(
                fields = extra.fields.map {
                    com.twidere.services.mastodon.model.Field(
                        name = it.name,
                        value = it.value,
                    )
                },
                emoji = extra.emoji.map { it.emoji }.flatten().map {
                    Emoji(
                        shortcode = it.shortcode,
                        url = it.url,
                        staticURL = it.staticURL,
                        visibleInPicker = it.visibleInPicker,
                        category = it.category
                    )
                },
                bot = extra.bot,
                locked = extra.locked
            ).json()
            else -> extra.json()
        },
        acct = acct,
        userId = id,
        followersCount = metrics.fans,
        friendsCount = metrics.follow,
        listedCount = metrics.listed,
        isProtected = protected,
        statusesCount = metrics.status
    )

internal fun DbTwitterUserExtra.toUi() = TwitterUserExtra(
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

internal fun DbMastodonUserExtra.toUi() = MastodonUserExtra(
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
