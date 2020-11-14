/*
 *  Twidere X
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
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

import com.twidere.services.twitter.model.ReferencedTweetType
import com.twidere.services.twitter.model.Status
import com.twidere.services.twitter.model.StatusV2
import com.twidere.services.twitter.model.User
import com.twidere.services.twitter.model.UserV2
import com.twidere.twiderex.db.model.DbMedia
import com.twidere.twiderex.db.model.DbStatusReaction
import com.twidere.twiderex.db.model.DbStatusV2
import com.twidere.twiderex.db.model.DbStatusWithMediaAndUser
import com.twidere.twiderex.db.model.DbStatusWithReference
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
    val replyTo = this.referencedTweets
        ?.firstOrNull { it.type == ReferencedTweetType.replied_to }?.status?.toDbStatusWithMediaAndUser(
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
            timestamp = status.data.timestamp,
            isGap = false,
            statusId = status.data.statusId,
            type = timelineType,
        ),
        status = DbStatusWithReference(
            replyTo = replyTo,
            quote = quote,
            retweet = retweet,
            status = status
        ),
    )
}

fun Status.toDbTimeline(
    userKey: UserKey,
    timelineType: TimelineType,
): DbTimelineWithStatus {
    val status = this.toDbStatusWithMediaAndUser(userKey)
    val retweet = retweetedStatus?.toDbStatusWithMediaAndUser(userKey)
    val quote = (retweetedStatus?.quotedStatus ?: quotedStatus)?.toDbStatusWithMediaAndUser(userKey)

    return DbTimelineWithStatus(
        timeline = DbTimeline(
            _id = UUID.randomUUID().toString(),
            userKey = userKey,
            timestamp = status.data.timestamp,
            isGap = false,
            statusId = status.data.statusId,
            type = timelineType,
        ),
        status = DbStatusWithReference(
            status = status,
            quote = quote,
            retweet = retweet,
            replyTo = null,
        ),
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
    @Suppress("UNUSED_PARAMETER")
    userKey: UserKey
): DbStatusWithMediaAndUser {
    val user = user?.toDbUser() ?: throw IllegalArgumentException("Status.user should not be null")
    val retweet = this.referencedTweets
        ?.firstOrNull { it.type == ReferencedTweetType.retweeted }?.id
    val replyTo = this.referencedTweets
        ?.firstOrNull { it.type == ReferencedTweetType.replied_to }?.id
    val quote = this.referencedTweets
        ?.firstOrNull { it.type == ReferencedTweetType.quoted }?.id
    val status = DbStatusV2(
        _id = UUID.randomUUID().toString(),
        statusId = id ?: throw IllegalArgumentException("Status.idStr should not be null"),
        text = text ?: "",
        timestamp = createdAt?.time ?: 0,
        retweetCount = publicMetrics?.retweetCount ?: 0,
        likeCount = publicMetrics?.likeCount ?: 0,
        replyCount = 0,
        placeString = place?.fullName,
        hasMedia = !attachments?.media.isNullOrEmpty(),
        source = source ?: "",
        userId = user.userId,
        lang = lang,
        replyStatusId = replyTo,
        retweetStatusId = retweet,
        quoteStatusId = quote,
    )
    return DbStatusWithMediaAndUser(
        data = status,
        media = (attachments?.media ?: emptyList()).mapIndexed { index, it ->
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
        user = user,
        reactions = emptyList() // TODO: twitter v2 api does not return this
    )
}

private fun Status.toDbStatusWithMediaAndUser(
    userKey: UserKey
): DbStatusWithMediaAndUser {
    val user = user?.toDbUser() ?: throw IllegalArgumentException("Status.user should not be null")
    val status = DbStatusV2(
        _id = UUID.randomUUID().toString(),
        statusId = idStr ?: throw IllegalArgumentException("Status.idStr should not be null"),
        text = text ?: "",
        timestamp = createdAt?.time ?: 0,
        retweetCount = retweetCount ?: 0,
        likeCount = favoriteCount ?: 0,
        replyCount = 0,
        placeString = place?.fullName,
        hasMedia = extendedEntities?.media != null || entities?.media != null,
        source = source ?: "",
        userId = user.userId,
        lang = lang,
        replyStatusId = null,
        retweetStatusId = retweetedStatus?.idStr,
        quoteStatusId = (retweetedStatus?.quotedStatus ?: quotedStatus)?.idStr,
    )
    return DbStatusWithMediaAndUser(
        data = status,
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
        user = user,
        reactions = if (favorited == true || retweeted == true) {
            listOf(
                DbStatusReaction(
                    _id = UUID.randomUUID().toString(),
                    statusId = status.statusId,
                    userKey = userKey,
                    liked = favorited == true,
                    retweeted = retweeted == true,
                ),
            )
        } else {
            emptyList()
        }
    )
}

fun User.toDbUser() = DbUser(
    _id = UUID.randomUUID().toString(),
    userId = this.idStr ?: throw IllegalArgumentException("user.idStr should not be null"),
    name = this.name ?: "",
    screenName = this.screenName ?: "",
    profileImage = (profileImageURLHTTPS ?: profileImageURL)?.let { updateProfileImagePath(it) }
        ?: "",
    profileBackgroundImage = profileBannerURL,
    followersCount = this.followersCount ?: 0,
    friendsCount = this.friendsCount ?: 0,
    listedCount = this.listedCount ?: 0,
    desc = this.description ?: "",
    location = this.location,
    website = this.entities?.url?.urls?.firstOrNull { it.url == this.url }?.expandedURL,
    verified = this.verified ?: false,
    isProtected = this.protected ?: false,
    platformType = PlatformType.Twitter,
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
    isProtected = this.protected ?: false,
    platformType = PlatformType.Twitter,
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
