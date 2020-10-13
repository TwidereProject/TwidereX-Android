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

import com.twidere.services.twitter.model.ReferencedTweetType
import com.twidere.services.twitter.model.Status
import com.twidere.services.twitter.model.StatusV2
import com.twidere.services.twitter.model.User
import com.twidere.services.twitter.model.UserV2
import com.twidere.services.utils.encodeJson
import com.twidere.twiderex.db.model.DbMedia
import com.twidere.twiderex.db.model.DbStatus
import com.twidere.twiderex.db.model.DbStatusWithMediaAndUser
import com.twidere.twiderex.db.model.DbTimeline
import com.twidere.twiderex.db.model.DbTimelineWithStatus
import com.twidere.twiderex.db.model.DbUser
import com.twidere.twiderex.db.model.TimelineType
import com.twidere.twiderex.model.MediaType
import com.twidere.twiderex.model.PlatformType
import com.twidere.twiderex.model.UserKey
import java.util.UUID

fun StatusV2.toDbTimeline(
    userKey: UserKey,
    timelineType: TimelineType,
): DbTimelineWithStatus {
    val status = this.toDbStatusWithMediaAndUser(userKey)
    val retweet = this.referencedTweets
        ?.firstOrNull { it.type == ReferencedTweetType.retweeted }?.status?.toDbStatusWithMediaAndUser(
            userKey
        )
    val quote = this.referencedTweets
        ?.firstOrNull { it.type == ReferencedTweetType.quoted }?.status?.toDbStatusWithMediaAndUser(
            userKey
        )

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

fun Status.toDbTimeline(
    userKey: UserKey,
    timelineType: TimelineType,
): DbTimelineWithStatus {
    val status = this.toDbStatusWithMediaAndUser(userKey)
    val retweet = retweetedStatus?.toDbStatusWithMediaAndUser(userKey)
    val quote = quotedStatus?.toDbStatusWithMediaAndUser(userKey)

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
        return "${uri.removeSuffix(extension)}?format=${extension.removePrefix(".")}&name=$type"
    }
    return uri
}

private fun StatusV2.toDbStatusWithMediaAndUser(
    userKey: UserKey
): DbStatusWithMediaAndUser {
    val user = user?.toDbUser() ?: throw IllegalArgumentException("Status.user should not be null")
    val status = DbStatus(
        _id = UUID.randomUUID().toString(),
        statusId = id ?: throw IllegalArgumentException("Status.idStr should not be null"),
        userKey = userKey,
        platformType = PlatformType.Twitter,
        text = text ?: "",
        timestamp = createdAt?.time ?: 0,
        retweetCount = publicMetrics?.retweetCount ?: 0,
        likeCount = publicMetrics?.likeCount ?: 0,
        retweeted = false, // TODO: twitter v2 api does not return this
        liked = false, // TODO: twitter v2 api does not return this
        replyCount = 0,
        placeString = place?.fullName,
        hasMedia = !attachments?.medias.isNullOrEmpty(),
        userId = user.userId
    )
    return DbStatusWithMediaAndUser(
        status = status,
        media = (attachments?.medias ?: emptyList()).mapIndexed { index, it ->
            DbMedia(
                _id = UUID.randomUUID().toString(),
                statusId = status.statusId,
                previewUrl = getImage(it.url ?: it.previewImageURL, "small"),
                mediaUrl = getImage(it.url ?: it.previewImageURL, "large"),
                width = it.width ?: 0,
                height = it.height ?: 0,
                pageUrl = null, // TODO: how to play media under twitter v2 api
                altText = it.publicMetrics?.viewCount?.toString() ?: "",
                url = it.url ?: it.previewImageURL,
                type = it.type?.let { MediaType.valueOf(it) } ?: MediaType.photo,
                order = index,
            )
        },
        user = user
    )
}

private fun Status.toDbStatusWithMediaAndUser(
    userKey: UserKey
): DbStatusWithMediaAndUser {
    val user = user?.toDbUser() ?: throw IllegalArgumentException("Status.user should not be null")
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
        userId = user.userId
    )
    return DbStatusWithMediaAndUser(
        status = status,
        media = (
            extendedEntities?.media ?: entities?.media
                ?: emptyList()
            ).mapIndexed { index, it ->
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
        },
        user = user
    )
}

fun User.toDbUser() = DbUser(
    _id = UUID.randomUUID().toString(),
    userId = this.idStr ?: throw IllegalArgumentException("user.idStr should not be null"),
    name = this.name ?: "",
    screenName = this.screenName ?: "",
    profileImage = (profileImageURLHTTPS ?: profileImageURL)?.let { updateProfileImagePath(it) }
        ?: "",
    profileBackgroundImage = profileBackgroundImageURLHTTPS,
    followersCount = this.followersCount ?: 0,
    friendsCount = this.friendsCount ?: 0,
    listedCount = this.listedCount ?: 0,
    desc = this.description ?: "",
    location = this.location,
    website = this.entities?.url?.urls?.firstOrNull { it.url == this.url }?.expandedURL,
    verified = this.verified ?: false,
    isProtected = this.protected ?: false,
)

fun UserV2.toDbUser() = DbUser(
    _id = UUID.randomUUID().toString(),
    userId = this.id ?: throw IllegalArgumentException("user.idStr should not be null"),
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

private fun updateProfileImagePath(
    value: String,
    size: ProfileImageSize = ProfileImageSize.reasonably_small
): String {
    val last = value.split("/").lastOrNull()
    var id = last?.split(".")?.firstOrNull()
    ProfileImageSize.values().forEach {
        id = id?.removeSuffix("_${it.name}")
    }
    return if (id != null && last != null) {
        value.replace(last, "${id}_${size.name}.${value.split(".").lastOrNull()}")
    } else {
        value
    }
}

enum class ProfileImageSize {
    original,
    reasonably_small,
    bigger,
    normal,
    mini,
}
