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
package com.twidere.twiderex.db.mapper

import com.twidere.services.microblog.model.IStatus
import com.twidere.services.microblog.model.IUser
import com.twidere.twiderex.db.model.TimelineType
import com.twidere.twiderex.model.MicroBlogKey

private typealias TwitterUser = com.twidere.services.twitter.model.User
private typealias TwitterUserV2 = com.twidere.services.twitter.model.UserV2
private typealias TwitterStatus = com.twidere.services.twitter.model.Status
private typealias TwitterStatusV2 = com.twidere.services.twitter.model.StatusV2
private typealias MastodonStatus = com.twidere.services.mastodon.model.Status
private typealias MastodonNotification = com.twidere.services.mastodon.model.Notification
private typealias MastodonUser = com.twidere.services.mastodon.model.Account

fun IStatus.toDbTimeline(
    accountKey: MicroBlogKey,
    timelineType: TimelineType,
) = when (this) {
    is TwitterStatus -> this.toDbTimeline(
        accountKey = accountKey,
        timelineType = timelineType,
    )
    is TwitterStatusV2 -> this.toDbTimeline(
        accountKey = accountKey,
        timelineType = timelineType,
    )
    is MastodonStatus -> this.toDbTimeline(
        accountKey = accountKey,
        timelineType = timelineType
    )
    is MastodonNotification -> this.toDbTimeline(
        accountKey = accountKey,
        timelineType = timelineType,
    )
    else -> throw NotImplementedError()
}

fun IUser.toDbUser(
    accountKey: MicroBlogKey
) = when (this) {
    is TwitterUser -> this.toDbUser()
    is TwitterUserV2 -> this.toDbUser()
    is MastodonUser -> this.toDbUser(
        accountKey = accountKey
    )
    else -> throw NotImplementedError()
}
