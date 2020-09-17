package com.twidere.twiderex.db.mapper

import com.twidere.services.microblog.model.IStatus
import com.twidere.twiderex.db.model.DbStatus
import com.twidere.twiderex.model.MediaData
import com.twidere.twiderex.model.PlatformType
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


fun DbStatus.extraMedia(): List<MediaData>? {
    if (mediaCache == null) {
        mediaCache = when(platformType) {
            PlatformType.Twitter -> extraTwitterMedia()
            PlatformType.StatusNet -> TODO()
            PlatformType.Fanfou -> TODO()
            PlatformType.Mastodon -> TODO()
        }
    }
    return mediaCache
}