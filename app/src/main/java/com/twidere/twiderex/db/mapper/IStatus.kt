package com.twidere.twiderex.db.mapper

import com.twidere.services.microblog.model.IStatus
import com.twidere.services.microblog.model.IUser
import com.twidere.twiderex.db.model.TimelineType
import com.twidere.twiderex.model.UserKey

private typealias TwitterUser = com.twidere.services.twitter.model.User
private typealias TwitterUserV2 = com.twidere.services.twitter.model.UserV2
private typealias TwitterStatus = com.twidere.services.twitter.model.Status

fun IStatus.toDbTimeline(
    userKey: UserKey,
    timelineType: TimelineType,
) = when (this) {
    is TwitterStatus -> this.toDbTimeline(
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