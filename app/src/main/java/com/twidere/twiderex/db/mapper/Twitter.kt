package com.twidere.twiderex.db.mapper

import com.twidere.services.twitter.model.Status
import com.twidere.services.twitter.model.User
import com.twidere.services.twitter.model.UserV2
import com.twidere.services.utils.encodeJson
import com.twidere.twiderex.db.model.*
import com.twidere.twiderex.model.MediaType
import com.twidere.twiderex.model.PlatformType
import com.twidere.twiderex.model.UserKey
import java.util.*

fun Status.toDbTimeline(
    userKey: UserKey,
    timelineType: TimelineType,
): DbTimelineWithStatus {
    val status = this.toDbStatusWithMedia(userKey)
    val retweet = retweetedStatus?.toDbStatusWithMedia(userKey)
    val quote = quotedStatus?.toDbStatusWithMedia(userKey)

    return DbTimelineWithStatus(
        timeline = DbTimeline(
            _id = UUID.randomUUID().toString(),
            userKey = userKey,
            platformType = PlatformType.Twitter,
            timestamp = status.status.timestamp,
            isGap = false,
            retweetId = retweet?.status?.statusId,
            quoteId = quote?.status?.statusId,
            statusId = status.status.statusId,
            type = timelineType,
        ),
        status = status,
        retweet = retweet,
        quote = quote,
    )
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

private fun Status.toDbStatusWithMedia(
    userKey: UserKey
): DbStatusWithMedia {
    val status = DbStatus(
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
        hasMedia = extendedEntities?.media != null || entities?.media != null,
        extra = encodeJson(),
        user = user?.toDbUser()
            ?: throw IllegalArgumentException("Status.user should not be null"),
    )
    return DbStatusWithMedia(
        status = status,
        media = (extendedEntities?.media ?: entities?.media
        ?: emptyList()).mapIndexed { index, it ->
            DbMedia(
                _id = UUID.randomUUID().toString(),
                statusId = status.statusId,
                previewUrl = getImage(it.mediaURLHTTPS, "small"),
                mediaUrl = getImage(it.mediaURLHTTPS, "large"),
                width = it.sizes?.large?.w ?: 0,
                height = it.sizes?.large?.h ?: 0,
                pageUrl = it.url,
                altText = it.displayURL ?: "",
                url = it.expandedURL,
                type = it.type?.let { MediaType.valueOf(it) } ?: MediaType.photo,
                order = index,
            )
        }
    )
}

fun User.toDbUser() = DbUser(
    id = this.idStr ?: throw IllegalArgumentException("user.idStr should not be null"),
    name = this.name ?: "",
    screenName = this.screenName ?: "",
    profileImage = (profileImageURLHTTPS ?: profileImageURL)?.let { updateProfileImagePath(it) } ?: "",
    profileBackgroundImage = profileBackgroundImageURLHTTPS,
    followersCount = this.followersCount ?: 0,
    friendsCount = this.friendsCount ?: 0,
    listedCount = this.listedCount ?: 0,
    desc = this.description ?: "",
    location = this.location,
    website = this.entities?.url?.urls?.firstOrNull { it.url == this.url }?.expandedURL,
    verified = this.verified ?: false,
    isProtected = this.protected ?: false
)

fun UserV2.toDbUser() = DbUser(
    id = this.id ?: throw IllegalArgumentException("user.idStr should not be null"),
    name = this.name ?: "",
    screenName = this.username ?: "",
    profileImage = profileImageURL?.let { updateProfileImagePath(it) } ?: "",
    profileBackgroundImage = this.profileBanner?.sizes?.let {
        it.getOrElse("mobile_retina", { null }) ?: it.values.firstOrNull()
    }?.url,
    followersCount = this.publicMetrics?.followersCount ?: 0,
    friendsCount = this.publicMetrics?.followingCount ?: 0,
    listedCount = this.publicMetrics?.listedCount ?: 0,
    desc = this.description ?: "",
    location = this.location,
    website = this.entities?.url?.urls?.firstOrNull { it.url == this.url }?.expandedURL,
    verified = this.verified ?: false,
    isProtected = this.protected ?: false
)

private fun updateProfileImagePath(value: String): String {
    val last = value.split("/").lastOrNull()
    val id = last?.split("_")?.firstOrNull()
    return if (id != null) {
        value.replace(last, "${id}_reasonably_small.${value.split(".").lastOrNull()}")
    } else {
        value
    }
}