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
package com.twidere.twiderex.dataprovider.mapper

import com.twidere.services.gif.giphy.GifObject
import com.twidere.services.gif.model.IGif
import com.twidere.services.mastodon.model.Emoji
import com.twidere.services.mastodon.model.MastodonList
import com.twidere.services.microblog.model.IDirectMessage
import com.twidere.services.microblog.model.IListModel
import com.twidere.services.microblog.model.IStatus
import com.twidere.services.microblog.model.ITrend
import com.twidere.services.microblog.model.IUser
import com.twidere.services.twitter.model.DirectMessageEvent
import com.twidere.services.twitter.model.TwitterList
import com.twidere.twiderex.model.AmUser
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiEmoji
import com.twidere.twiderex.model.ui.UiEmojiCategory
import com.twidere.twiderex.model.ui.UiGif
import com.twidere.twiderex.model.ui.UiUser
import java.util.UUID

private typealias TwitterUser = com.twidere.services.twitter.model.User
private typealias TwitterUserV2 = com.twidere.services.twitter.model.UserV2
private typealias TwitterStatus = com.twidere.services.twitter.model.Status
private typealias TwitterStatusV2 = com.twidere.services.twitter.model.StatusV2
private typealias TwitterTrend = com.twidere.services.twitter.model.Trend
private typealias MastodonStatus = com.twidere.services.mastodon.model.Status
private typealias MastodonNotification = com.twidere.services.mastodon.model.Notification
private typealias MastodonUser = com.twidere.services.mastodon.model.Account
private typealias MastodonTrend = com.twidere.services.mastodon.model.Trend
typealias GiphyGif = GifObject

fun IUser.toUi(accountKey: MicroBlogKey) = when (this) {
    is TwitterUser -> this.toUiUser()
    is TwitterUserV2 -> this.toUiUser()
    is MastodonUser -> this.toUiUser(
        accountKey = accountKey
    )
    else -> throw NotImplementedError()
}

fun IStatus.toUi(accountKey: MicroBlogKey, isGap: Boolean = false) = when (this) {
    is TwitterStatus -> this.toUiStatus(
        accountKey = accountKey,
        isGap = isGap,
    )
    is TwitterStatusV2 -> this.toUiStatus(
        accountKey = accountKey,
        isGap = isGap,
    )
    is MastodonStatus -> this.toUiStatus(
        accountKey = accountKey,
        isGap = isGap
    )
    is MastodonNotification -> this.toUiStatus(
        accountKey = accountKey,
        isGap = isGap
    )
    else -> throw NotImplementedError()
}

fun IStatus.toPagingTimeline(accountKey: MicroBlogKey, pagingKey: String) = when (this) {
    is TwitterStatus -> this.toPagingTimeline(
        accountKey = accountKey,
        pagingKey = pagingKey,
    )
    is TwitterStatusV2 -> this.toPagingTimeline(
        accountKey = accountKey,
        pagingKey = pagingKey,
    )
    is MastodonStatus -> this.toPagingTimeline(
        accountKey = accountKey,
        pagingKey = pagingKey,
    )
    is MastodonNotification -> this.toPagingTimeline(
        accountKey = accountKey,
        pagingKey = pagingKey,
    )
    else -> throw NotImplementedError()
}

fun IListModel.toUi(accountKey: MicroBlogKey) = when (this) {
    is TwitterList -> this.toUiList(accountKey)
    is MastodonList -> this.toUiList(accountKey)
    else -> throw NotImplementedError()
}

fun ITrend.toUi(accountKey: MicroBlogKey) = when (this) {
    is TwitterTrend -> this.toUiTrend(accountKey)
    is MastodonTrend -> this.toUiTrend(accountKey)
    else -> throw NotImplementedError()
}

fun IDirectMessage.toUi(accountKey: MicroBlogKey, sender: UiUser) = when (this) {
    is DirectMessageEvent -> this.toUiDMEvent(accountKey, sender)
    else -> throw NotImplementedError()
}

fun List<Emoji>.toUi(): List<UiEmojiCategory> = groupBy({ it.category }, { it }).map {
    UiEmojiCategory(
        if (it.key.isNullOrEmpty()) null else it.key,
        it.value.map { emoji ->
            UiEmoji(
                shortcode = emoji.shortcode,
                url = emoji.url,
                staticURL = emoji.staticURL,
                visibleInPicker = emoji.visibleInPicker,
                category = emoji.category
            )
        }
    )
}

fun UiUser.toAmUser() =
    AmUser(
        userId = id,
        name = name,
        userKey = userKey,
        screenName = screenName,
        profileImage = profileImage.toString(),
        profileBackgroundImage = profileBackgroundImage,
        followersCount = metrics.fans,
        friendsCount = metrics.follow,
        listedCount = metrics.listed,
        desc = rawDesc,
        website = website,
        location = location,
        verified = verified,
        isProtected = protected,
    )

fun IGif.toUi(): UiGif {
    return when (this) {
        is GiphyGif -> UiGif(
            id = this.id ?: UUID.randomUUID().toString(),
            url = this.images?.original?.url ?: "",
            mp4 = this.images?.original?.mp4 ?: "",
            preview = this.images?.previewGif?.url ?: "",
            type = this.type ?: "gif"
        )
        else -> throw NotImplementedError()
    }
}
