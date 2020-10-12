/*
 *  TwidereX
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of TwidereX.
 * 
 *  TwidereX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  TwidereX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with TwidereX. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.db.mapper

import com.twidere.services.microblog.model.IStatus
import com.twidere.services.microblog.model.IUser
import com.twidere.twiderex.db.model.TimelineType
import com.twidere.twiderex.model.UserKey

private typealias TwitterUser = com.twidere.services.twitter.model.User
private typealias TwitterUserV2 = com.twidere.services.twitter.model.UserV2
private typealias TwitterStatus = com.twidere.services.twitter.model.Status
private typealias TwitterStatusV2 = com.twidere.services.twitter.model.StatusV2

fun IStatus.toDbTimeline(
    userKey: UserKey,
    timelineType: TimelineType,
) = when (this) {
    is TwitterStatus -> this.toDbTimeline(
        userKey = userKey,
        timelineType = timelineType,
    )
    is TwitterStatusV2 -> this.toDbTimeline(
        userKey = userKey,
        timelineType = timelineType,
    )
    else -> throw NotImplementedError()
}

fun IUser.toDbUser() = when (this) {
    is TwitterUser -> this.toDbUser()
    is TwitterUserV2 -> this.toDbUser()
    else -> throw NotImplementedError()
}
