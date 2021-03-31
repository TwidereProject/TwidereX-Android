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
import com.twidere.twiderex.db.model.DbStatusWithReference
import com.twidere.twiderex.model.MicroBlogKey

private typealias TwitterUser = com.twidere.services.twitter.model.User
private typealias TwitterUserV2 = com.twidere.services.twitter.model.UserV2
private typealias TwitterStatus = com.twidere.services.twitter.model.Status
private typealias TwitterStatusV2 = com.twidere.services.twitter.model.StatusV2
private typealias MastodonStatus = com.twidere.services.mastodon.model.Status
private typealias MastodonNotification = com.twidere.services.mastodon.model.Notification
private typealias MastodonUser = com.twidere.services.mastodon.model.Account

fun IStatus.toDbPagingTimeline(
    accountKey: MicroBlogKey,
    pagingKey: String,
) = when (this) {
    is TwitterStatus -> this.toDbPagingTimeline(
        accountKey = accountKey,
        pagingKey = pagingKey,
    )
    is TwitterStatusV2 -> this.toDbPagingTimeline(
        accountKey = accountKey,
        pagingKey = pagingKey,
    )
    is MastodonStatus -> this.toDbPagingTimeline(
        accountKey = accountKey,
        pagingKey = pagingKey,
    )
    is MastodonNotification -> this.toDbPagingTimeline(
        accountKey = accountKey,
        pagingKey = pagingKey,
    )
    else -> throw NotImplementedError()
}

fun IStatus.toDbStatusWithReference(
    accountKey: MicroBlogKey,
): DbStatusWithReference = when (this) {
    is TwitterStatus -> this.toDbStatusWithReference(
        accountKey = accountKey,
    )
    is TwitterStatusV2 -> this.toDbStatusWithReference(
        accountKey = accountKey,
    )
    is MastodonStatus -> this.toDbStatusWithReference(
        accountKey = accountKey,
    )
    is MastodonNotification -> this.toDbStatusWithReference(
        accountKey = accountKey,
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
