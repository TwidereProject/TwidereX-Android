package com.twidere.twiderex.db.mapper

import com.twidere.services.microblog.model.IStatus
import com.twidere.twiderex.model.UserKey

private typealias TwitterStatus = com.twidere.services.twitter.model.Status

fun IStatus.toDbTimeline(
    userKey: UserKey
) = when (this) {
    is TwitterStatus -> this.toDbTimeline(
        userKey = userKey
    )
    else -> throw NotImplementedError()
}