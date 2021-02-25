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
import com.twidere.twiderex.db.model.DbTwitterUserExtra
import com.twidere.twiderex.db.model.DbUrlEntity
import com.twidere.twiderex.db.model.DbUser
import com.twidere.twiderex.db.model.DbUserWithEntity
import com.twidere.twiderex.db.model.TimelineType
import com.twidere.twiderex.model.MediaType
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.PlatformType
import com.twidere.twiderex.navigation.DeepLinks
import com.twitter.twittertext.Autolink
import java.util.UUID

private val autolink by lazy {
    Autolink().apply {
        setUsernameIncludeSymbol(true)
        hashtagUrlBase = "${DeepLinks.Search}%23"
        cashtagUrlBase = "${DeepLinks.Search}%24"
        usernameUrlBase = DeepLinks.User
    }
}

fun StatusV2.toDbTimeline(
    accountKey: MicroBlogKey,
    timelineType: TimelineType,
): DbTimelineWithStatus {
    val status = this.toDbStatusWithMediaAndUser(accountKey)
    val retweet = this.referencedTweets
        ?.firstOrNull { it.type == ReferencedTweetType.retweeted }?.status?.toDbStatusWithMediaAndUser(
            accountKey
        )
    val replyTo = this.referencedTweets
        ?.firstOrNull { it.type == ReferencedTweetType.replied_to }?.status?.toDbStatusWithMediaAndUser(
            accountKey
        )
    val quote = this.referencedTweets
        ?.firstOrNull { it.type == ReferencedTweetType.quoted }?.status?.toDbStatusWithMediaAndUser(
            accountKey
        )

    return DbTimelineWithStatus(
        timeline = DbTimeline(
            _id = UUID.randomUUID().toString(),
            accountKey = accountKey,
            timestamp = status.data.timestamp,
            isGap = false,
            statusKey = status.data.statusKey,
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
    accountKey: MicroBlogKey,
    timelineType: TimelineType,
): DbTimelineWithStatus {
    val status = this.toDbStatusWithMediaAndUser(accountKey)
    val retweet = retweetedStatus?.toDbStatusWithMediaAndUser(accountKey)
    val quote =
        (retweetedStatus?.quotedStatus ?: quotedStatus)?.toDbStatusWithMediaAndUser(accountKey)

    return DbTimelineWithStatus(
        timeline = DbTimeline(
            _id = UUID.randomUUID().toString(),
            accountKey = accountKey,
            timestamp = status.data.timestamp,
            isGap = false,
            statusKey = status.data.statusKey,
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
    accountKey: MicroBlogKey
): DbStatusWithMediaAndUser {
    val user = user?.toDbUser() ?: throw IllegalArgumentException("Status.user should not be null")
    val retweet = this.referencedTweets
        ?.firstOrNull { it.type == ReferencedTweetType.retweeted }?.status
    val replyTo = this.referencedTweets
        ?.firstOrNull { it.type == ReferencedTweetType.replied_to }?.status
    val quote = this.referencedTweets
        ?.firstOrNull { it.type == ReferencedTweetType.quoted }?.status
    val status = DbStatusV2(
        _id = UUID.randomUUID().toString(),
        statusId = id ?: throw IllegalArgumentException("Status.idStr should not be null"),
        is_possibly_sensitive = possiblySensitive ?: false,
        rawText = text ?: "",
        htmlText = autolink.autoLink(text ?: ""),
//        htmlText = autolink.autoLinkEntities(text ?: "", entities?.let {
//            (it.mentions?.map {
//                Extractor.Entity(
//                    it.start?.toInt() ?: 0,
//                    it.end?.toInt() ?: 0,
//                    it.username ?: "",
//                    Extractor.Entity.Type.MENTION
//                )
//            } ?: emptyList()) + (it.hashtags?.map {
//                Extractor.Entity(
//                    it.start?.toInt() ?: 0,
//                    it.end?.toInt() ?: 0,
//                    it.tag ?: "",
//                    Extractor.Entity.Type.HASHTAG
//                )
//            } ?: emptyList()) + (it.urls?.map {
//                Extractor.Entity(
//                    it.start?.toInt() ?: 0,
//                    it.end?.toInt() ?: 0,
//                    it.url ?: "",
//                    Extractor.Entity.Type.URL
//                ).apply {
//                    displayURL = it.displayURL
//                    expandedURL = it.expandedURL
//                }
//            } ?: emptyList())
//        }?.distinctBy { it.start }?.sortedBy { it.start } ?: emptyList<Extractor.Entity>()),
        timestamp = createdAt?.time ?: 0,
        retweetCount = publicMetrics?.retweetCount ?: 0,
        likeCount = publicMetrics?.likeCount ?: 0,
        replyCount = publicMetrics?.replyCount ?: 0,
        placeString = place?.fullName,
        hasMedia = !attachments?.media.isNullOrEmpty(),
        source = source ?: "",
        userKey = user.user.userKey,
        lang = lang,
        replyStatusKey = replyTo?.toDbStatusWithMediaAndUser(accountKey = accountKey)?.data?.statusKey,
        retweetStatusKey = retweet?.toDbStatusWithMediaAndUser(accountKey = accountKey)?.data?.statusKey,
        quoteStatusKey = quote?.toDbStatusWithMediaAndUser(accountKey = accountKey)?.data?.statusKey,
        statusKey = MicroBlogKey.twitter(
            id ?: throw IllegalArgumentException("Status.idStr should not be null")
        ),
        platformType = PlatformType.Twitter,
    )
    return DbStatusWithMediaAndUser(
        data = status,
        media = (attachments?.media ?: emptyList()).filter {
            it.type == MediaType.photo.name // TODO: video and gif
        }.mapIndexed { index, it ->
            val type = it.type?.let { MediaType.valueOf(it) } ?: MediaType.photo
            DbMedia(
                _id = UUID.randomUUID().toString(),
                statusKey = status.statusKey,
                previewUrl = getImage(it.url ?: it.previewImageURL, "small"),
                mediaUrl = getImage(it.url ?: it.previewImageURL, "orig"),
                width = it.width ?: 0,
                height = it.height ?: 0,
                pageUrl = null, // TODO: how to play media under twitter v2 api
                altText = it.publicMetrics?.viewCount?.toString() ?: "",
                url = it.url,
                type = type,
                order = index,
            )
        },
        user = user,
        reactions = emptyList(), // TODO: twitter v2 api does not return this
        url = entities?.urls?.map {
            DbUrlEntity(
                _id = UUID.randomUUID().toString(),
                statusKey = status.statusKey,
                userKey = null,
                url = it.url ?: "",
                expandedUrl = it.expandedURL ?: "",
                displayUrl = it.displayURL ?: "",
                title = it.title,
                description = it.description,
                image = it.images?.maxByOrNull { it.width ?: it.height ?: 0 }?.url
            )
        } ?: emptyList()
    )
}

private fun Status.toDbStatusWithMediaAndUser(
    accountKey: MicroBlogKey
): DbStatusWithMediaAndUser {
    val user = user?.toDbUser() ?: throw IllegalArgumentException("Status.user should not be null")
    val status = DbStatusV2(
        _id = UUID.randomUUID().toString(),
        statusId = idStr ?: throw IllegalArgumentException("Status.idStr should not be null"),
        is_possibly_sensitive = possiblySensitive ?: false,
        rawText = fullText ?: text ?: "",
        htmlText = autolink.autoLink(fullText ?: text ?: ""),
//        htmlText = autolink.autoLinkEntities(fullText ?: text ?: "", entities?.let {
//            (it.userMentions?.map {
//                Extractor.Entity(
//                    it.indices?.elementAtOrNull(0)?.toInt() ?: 0,
//                    it.indices?.elementAtOrNull(1)?.toInt() ?: 0,
//                    it.screenName ?: "",
//                    Extractor.Entity.Type.MENTION
//                )
//            } ?: emptyList()) + (it.hashtags?.map {
//                Extractor.Entity(
//                    it.indices?.elementAtOrNull(0)?.toInt() ?: 0,
//                    it.indices?.elementAtOrNull(1)?.toInt() ?: 0,
//                    it.text ?: "",
//                    Extractor.Entity.Type.HASHTAG
//                )
//            } ?: emptyList()) + (it.urls?.map {
//                Extractor.Entity(
//                    it.indices?.elementAtOrNull(0)?.toInt() ?: 0,
//                    it.indices?.elementAtOrNull(1)?.toInt() ?: 0,
//                    it.url ?: "",
//                    Extractor.Entity.Type.URL
//                ).apply {
//                    displayURL = it.displayURL
//                    expandedURL = it.expandedURL
//                }
//            } ?: emptyList())
//        }?.sortedBy { it.start } ?: emptyList<Extractor.Entity>()),
        timestamp = createdAt?.time ?: 0,
        retweetCount = retweetCount ?: 0,
        likeCount = favoriteCount ?: 0,
        replyCount = 0,
        placeString = place?.fullName,
        hasMedia = extendedEntities?.media != null || entities?.media != null,
        source = source ?: "",
        userKey = user.user.userKey,
        lang = lang,
        replyStatusKey = inReplyToStatusIDStr?.let { MicroBlogKey.twitter(it) },
        retweetStatusKey = retweetedStatus?.toDbStatusWithMediaAndUser(accountKey = accountKey)?.data?.statusKey,
        quoteStatusKey = (retweetedStatus?.quotedStatus ?: quotedStatus)?.toDbStatusWithMediaAndUser(accountKey = accountKey)?.data?.statusKey,
        statusKey = MicroBlogKey.twitter(
            idStr ?: throw IllegalArgumentException("Status.idStr should not be null")
        ),
        platformType = PlatformType.Twitter,
    )
    return DbStatusWithMediaAndUser(
        data = status,
        media = (
            extendedEntities?.media ?: entities?.media
                ?: emptyList()
            ).mapIndexed { index, it ->
            val type = it.type?.let { MediaType.valueOf(it) } ?: MediaType.photo
            DbMedia(
                _id = UUID.randomUUID().toString(),
                statusKey = status.statusKey,
                previewUrl = getImage(it.mediaURLHTTPS, "small"),
                mediaUrl = when (type) {
                    MediaType.photo -> getImage(it.mediaURLHTTPS, "orig")
                    MediaType.animated_gif, MediaType.video -> it.videoInfo?.variants?.maxByOrNull {
                        it.bitrate ?: 0L
                    }?.url
                },
                width = it.sizes?.large?.w ?: 0,
                height = it.sizes?.large?.h ?: 0,
                pageUrl = it.expandedURL,
                altText = it.displayURL ?: "",
                url = it.url,
                type = type,
                order = index,
            )
        },
        user = user,
        reactions = if (favorited == true || retweeted == true) {
            listOf(
                DbStatusReaction(
                    _id = UUID.randomUUID().toString(),
                    statusKey = status.statusKey,
                    accountKey = accountKey,
                    liked = favorited == true,
                    retweeted = retweeted == true,
                ),
            )
        } else {
            emptyList()
        },
        url = entities?.urls?.map {
            DbUrlEntity(
                _id = UUID.randomUUID().toString(),
                statusKey = status.statusKey,
                userKey = null,
                url = it.url ?: "",
                expandedUrl = it.expandedURL ?: "",
                displayUrl = it.displayURL ?: "",
                title = null,
                description = null,
                image = null,
            )
        } ?: emptyList()
    )
}

fun User.toDbUser(): DbUserWithEntity {
    val user = DbUser(
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
        rawDesc = this.description ?: "",
        htmlDesc = autolink.autoLink(this.description ?: ""),
        location = this.location,
        website = this.entities?.url?.urls?.firstOrNull { it.url == this.url }?.expandedURL,
        verified = this.verified ?: false,
        isProtected = this.protected ?: false,
        userKey = MicroBlogKey.twitter(
            idStr ?: throw IllegalArgumentException("user.idStr should not be null")
        ),
        platformType = PlatformType.Twitter,
    )
    return DbUserWithEntity(
        user = user,
        url = entities?.url?.urls?.map {
            DbUrlEntity(
                _id = UUID.randomUUID().toString(),
                statusKey = null,
                userKey = user.userKey,
                url = it.url ?: "",
                expandedUrl = it.expandedURL ?: "",
                displayUrl = it.displayURL ?: "",
                title = null,
                description = null,
                image = null,
            )
        } ?: emptyList()
    )
}

fun UserV2.toDbUser(): DbUserWithEntity {
    val user = DbUser(
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
        rawDesc = this.description ?: "",
        htmlDesc = autolink.autoLink(this.description ?: ""),
        location = this.location,
        website = this.entities?.url?.urls?.firstOrNull { it.url == this.url }?.expandedURL,
        verified = this.verified ?: false,
        isProtected = this.protected ?: false,
        userKey = MicroBlogKey.twitter(
            id ?: throw IllegalArgumentException("user.idStr should not be null")
        ),
        platformType = PlatformType.Twitter,
        twitterExtra = DbTwitterUserExtra(
            pinned_tweet_id = pinnedTweetID
        )
    )
    return DbUserWithEntity(
        user = user,
        url = entities?.description?.urls?.map {
            DbUrlEntity(
                _id = UUID.randomUUID().toString(),
                statusKey = null,
                userKey = user.userKey,
                url = it.url ?: "",
                expandedUrl = it.expandedURL ?: "",
                displayUrl = it.displayURL ?: "",
                title = null,
                description = null,
                image = null,
            )
        } ?: emptyList()
    )
}

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
