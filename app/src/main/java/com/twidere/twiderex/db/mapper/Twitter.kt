package com.twidere.twiderex.db.mapper

import androidx.compose.ui.util.fastMap
import com.twidere.services.twitter.model.Status
import com.twidere.services.twitter.model.User
import com.twidere.services.utils.decodeJson
import com.twidere.services.utils.encodeJson
import com.twidere.twiderex.db.model.DbStatus
import com.twidere.twiderex.db.model.DbTimeline
import com.twidere.twiderex.db.model.DbTimelineWithStatus
import com.twidere.twiderex.model.MediaData
import com.twidere.twiderex.model.MediaType
import com.twidere.twiderex.model.PlatformType
import com.twidere.twiderex.model.UserKey
import java.util.*

private typealias DbUser = com.twidere.twiderex.db.model.User

fun Status.toDbTimeline(
    userKey: UserKey
): DbTimelineWithStatus {
    val status = this.toDbStatus(userKey)
    val retweet = retweetedStatus?.toDbStatus(userKey)
    val quote = quotedStatus?.toDbStatus(userKey)

    return DbTimelineWithStatus(
        timeline = DbTimeline(
            _id = UUID.randomUUID().toString(),
            userKey = userKey,
            platformType = PlatformType.Twitter,
            timestamp = status.timestamp,
            isGap = false,
            statusDbId = status._id,
            retweetDbId = retweet?._id,
            quoteDbId = quote?._id,
            statusId = status.statusId,
        ),
        status = status,
        retweet = retweet,
        quote = quote,
    )
}

fun DbStatus.extraTwitterMedia(): List<MediaData>? {
    val status = this.extra.decodeJson<Status>()
    return status.let {
        it.entities?.media ?: it.extendedEntities?.media
    }?.fastMap { media ->
        MediaData(
            getImage(media.mediaURLHTTPS, "thumb"),
            getImage(media.mediaURLHTTPS, "large"),
            media.url,
            media.type?.let { MediaType.valueOf(it) } ?: MediaType.photo,
        )
    }
}

private fun getImage(uri: String?, type: String): String? {
    if (uri == null) {
        return null
    }
    if (uri.contains(".")) {
        val index = uri.lastIndexOf(".")
        val extension = uri.substring(index)
        return "${uri.removeSuffix(extension)}?format=${extension.removePrefix(".")}&name=${type}"
    }
    return uri
}

private fun Status.toDbStatus(
    userKey: UserKey
): DbStatus {
    return DbStatus(
        _id = UUID.randomUUID().toString(),
        statusId = idStr ?: throw IllegalArgumentException("Status.idStr should not be null"),
        userKey = userKey,
        platformType = PlatformType.Twitter,
        text = text ?: "",
        timestamp = createdAt?.time ?: 0,
        retweetCount = retweetCount ?: 0,
        likeCount = favoriteCount ?: 0,
        retweeted = retweeted ?: false,
        liked = favorited ?: false,
        replyCount = 0,
        placeString = place?.fullName,
        hasMedia = entities?.media != null || extendedEntities?.media != null,
        extra = encodeJson(),
        user = user?.toDbUser() ?: throw IllegalArgumentException("Status.user should not be null"),
    )
}

private fun User.toDbUser() = DbUser(
    id = this.idStr ?: throw IllegalArgumentException("user.idStr should not be null"),
    name = this.name ?: "",
    screenName = this.screenName ?: "",
    profileImage = profileImageURLHTTPS ?: profileImageURL ?: "",
)
