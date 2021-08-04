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

import com.twidere.services.twitter.model.DirectMessageEvent
import com.twidere.services.twitter.model.ReferencedTweetType
import com.twidere.services.twitter.model.ReplySettings
import com.twidere.services.twitter.model.Status
import com.twidere.services.twitter.model.StatusV2
import com.twidere.services.twitter.model.Trend
import com.twidere.services.twitter.model.TwitterList
import com.twidere.services.twitter.model.User
import com.twidere.services.twitter.model.UserV2
import com.twidere.twiderex.db.model.DbDMEvent
import com.twidere.twiderex.db.model.DbDMEventWithAttachments
import com.twidere.twiderex.db.model.DbList
import com.twidere.twiderex.db.model.DbMedia
import com.twidere.twiderex.db.model.DbPagingTimeline
import com.twidere.twiderex.db.model.DbPagingTimelineWithStatus
import com.twidere.twiderex.db.model.DbPreviewCard
import com.twidere.twiderex.db.model.DbStatusReaction
import com.twidere.twiderex.db.model.DbStatusV2
import com.twidere.twiderex.db.model.DbStatusWithMediaAndUser
import com.twidere.twiderex.db.model.DbStatusWithReference
import com.twidere.twiderex.db.model.DbTrend
import com.twidere.twiderex.db.model.DbTrendWithHistory
import com.twidere.twiderex.db.model.DbTwitterStatusExtra
import com.twidere.twiderex.db.model.DbTwitterUserExtra
import com.twidere.twiderex.db.model.DbUrlEntity
import com.twidere.twiderex.db.model.DbUser
import com.twidere.twiderex.db.model.TwitterUrlEntity
import com.twidere.twiderex.db.model.toDbStatusReference
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.MediaType
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.enums.ReferenceType
import com.twidere.twiderex.model.enums.TwitterReplySettings
import com.twidere.twiderex.model.ui.ListsMode
import com.twidere.twiderex.navigation.RootDeepLinksRouteDefinition
import com.twitter.twittertext.Autolink
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

val autolink by lazy {
    Autolink().apply {
        setUsernameIncludeSymbol(true)
        hashtagUrlBase = "${generateDeepLinkBase(RootDeepLinksRouteDefinition.Search)}/%23"
        cashtagUrlBase = "${generateDeepLinkBase(RootDeepLinksRouteDefinition.Search)}/%24"
        usernameUrlBase = "${generateDeepLinkBase(RootDeepLinksRouteDefinition.Twitter.User)}/"
    }
}

private fun generateDeepLinkBase(deeplink: String): String {
    return deeplink.substring(
        0,
        deeplink.indexOf("/{")
    )
}

fun StatusV2.toDbPagingTimeline(
    accountKey: MicroBlogKey,
    pagingKey: String,
): DbPagingTimelineWithStatus {
    val status = toDbStatusWithReference(accountKey = accountKey)
    return DbPagingTimelineWithStatus(
        timeline = DbPagingTimeline(
            _id = UUID.randomUUID().toString(),
            accountKey = accountKey,
            timestamp = status.status.data.timestamp,
            isGap = false,
            statusKey = status.status.data.statusKey,
            pagingKey = pagingKey,
            sortId = status.status.data.timestamp
        ),
        status = status,
    )
}

fun StatusV2.toDbStatusWithReference(
    accountKey: MicroBlogKey,
): DbStatusWithReference {
    val status = this.toDbStatusWithMediaAndUser(accountKey)
    val retweet = this.referencedTweets
        ?.firstOrNull { it.type == ReferencedTweetType.retweeted }?.status?.toDbStatusWithMediaAndUser(
            accountKey
        )
    val replyTo = this.let {
        it.referencedTweets
            ?.firstOrNull { it.type == ReferencedTweetType.retweeted }?.status ?: it
    }.referencedTweets
        ?.firstOrNull { it.type == ReferencedTweetType.replied_to }?.status?.toDbStatusWithMediaAndUser(
            accountKey
        )
    val quote = this.let {
        it.referencedTweets
            ?.firstOrNull { it.type == ReferencedTweetType.retweeted }?.status ?: it
    }.referencedTweets
        ?.firstOrNull { it.type == ReferencedTweetType.quoted }?.status?.toDbStatusWithMediaAndUser(
            accountKey
        )

    return DbStatusWithReference(
        status = status,
        references = listOfNotNull(
            replyTo.toDbStatusReference(status.data.statusKey, ReferenceType.Reply),
            quote.toDbStatusReference(status.data.statusKey, ReferenceType.Quote),
            retweet.toDbStatusReference(status.data.statusKey, ReferenceType.Retweet),
        ),
    )
}

fun Status.toDbPagingTimeline(
    accountKey: MicroBlogKey,
    pagingKey: String,
): DbPagingTimelineWithStatus {
    val status = toDbStatusWithReference(accountKey = accountKey)

    return DbPagingTimelineWithStatus(
        timeline = DbPagingTimeline(
            _id = UUID.randomUUID().toString(),
            accountKey = accountKey,
            timestamp = status.status.data.timestamp,
            isGap = false,
            statusKey = status.status.data.statusKey,
            pagingKey = pagingKey,
            sortId = status.status.data.timestamp
        ),
        status = status,
    )
}

fun Status.toDbStatusWithReference(
    accountKey: MicroBlogKey,
): DbStatusWithReference {
    val status = this.toDbStatusWithMediaAndUser(accountKey)
    val retweet = retweetedStatus?.toDbStatusWithMediaAndUser(accountKey)
    val quote =
        (retweetedStatus?.quotedStatus ?: quotedStatus)?.toDbStatusWithMediaAndUser(accountKey)

    return DbStatusWithReference(
        status = status,
        references = listOfNotNull(
            quote.toDbStatusReference(status.data.statusKey, ReferenceType.Quote),
            retweet.toDbStatusReference(status.data.statusKey, ReferenceType.Retweet),
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
        userKey = user.userKey,
        lang = lang,
        statusKey = MicroBlogKey.twitter(
            id ?: throw IllegalArgumentException("Status.idStr should not be null")
        ),
        platformType = PlatformType.Twitter,
        extra = Json.encodeToString(
            DbTwitterStatusExtra(
                reply_settings = replySettings.toDbEnums(),
                quoteCount = publicMetrics?.quoteCount
            )
        ),
        previewCard = entities?.urls?.firstOrNull()
            ?.takeUnless { url ->
                referencedTweets?.firstOrNull { it.type == ReferencedTweetType.quoted }
                    ?.id?.let { id -> url.expandedURL?.endsWith(id) == true } == true
            }
            ?.takeUnless { url -> url.displayURL?.contains("pic.twitter.com") == true }
            ?.let {
                it.expandedURL?.let { url ->
                    DbPreviewCard(
                        link = url,
                        title = it.title,
                        desc = it.description,
                        image = it.images?.firstOrNull()?.url,
                        displayLink = it.displayURL,
                    )
                }
            },
        inReplyToStatusId = referencedTweets?.find { it.type == ReferencedTweetType.replied_to }?.id,
        inReplyToUserId = inReplyToUserId,
    )
    return DbStatusWithMediaAndUser(
        data = status,
        media = (attachments?.media ?: emptyList()).filter {
            it.type == MediaType.photo.name // TODO: video and gif
        }.mapIndexed { index, it ->
            val type = it.type?.let { MediaType.valueOf(it) } ?: MediaType.photo
            DbMedia(
                _id = UUID.randomUUID().toString(),
                belongToKey = status.statusKey,
                previewUrl = getImage(it.url ?: it.previewImageURL, "small"),
                mediaUrl = getImage(it.url ?: it.previewImageURL, "orig"),
                width = it.width ?: 0,
                height = it.height ?: 0,
                pageUrl = null, // TODO: how to play media under twitter v2 api
                altText = "",
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
        userKey = user.userKey,
        lang = lang,
        statusKey = MicroBlogKey.twitter(
            idStr ?: throw IllegalArgumentException("Status.idStr should not be null")
        ),
        platformType = PlatformType.Twitter,
        extra = Json.encodeToString(
            DbTwitterStatusExtra(
                reply_settings = TwitterReplySettings.Everyone,
            )
        ),
        previewCard = entities?.urls?.firstOrNull()
            ?.takeUnless { url -> quotedStatus?.idStr?.let { id -> url.expandedURL?.endsWith(id) == true } == true }
            ?.takeUnless { url -> url.expandedURL?.contains("pic.twitter.com") == true }
            ?.let {
                it.url?.let { url ->
                    DbPreviewCard(
                        link = it.expandedURL ?: url,
                        displayLink = it.displayURL,
                        image = null,
                        title = null,
                        desc = null,
                    )
                }
            },
        inReplyToUserId = inReplyToUserIDStr,
        inReplyToStatusId = inReplyToStatusIDStr
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
                belongToKey = status.statusKey,
                previewUrl = getImage(it.mediaURLHTTPS, "small"),
                mediaUrl = when (type) {
                    MediaType.photo -> getImage(it.mediaURLHTTPS, "orig")
                    MediaType.animated_gif, MediaType.video -> it.videoInfo?.variants?.maxByOrNull {
                        it.bitrate ?: 0L
                    }?.url
                    MediaType.audio -> it.mediaURLHTTPS
                    MediaType.other -> it.mediaURLHTTPS
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

fun User.toDbUser(): DbUser {
    return DbUser(
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
        acct = MicroBlogKey.twitter(screenName ?: ""),
        statusesCount = statusesCount ?: 0,
        extra = Json.encodeToString(
            DbTwitterUserExtra(
                pinned_tweet_id = null,
                url = entities?.description?.urls?.map {
                    TwitterUrlEntity(
                        url = it.url ?: "",
                        expandedUrl = it.expandedURL ?: "",
                        displayUrl = it.displayURL ?: "",
                    )
                } ?: emptyList()
            )
        )
    )
}

fun UserV2.toDbUser(): DbUser {
    return DbUser(
        _id = UUID.randomUUID().toString(),
        userId = id ?: throw IllegalArgumentException("user.idStr should not be null"),
        name = name ?: "",
        screenName = username ?: "",
        profileImage = profileImageURL?.let { updateProfileImagePath(it) } ?: "",
        profileBackgroundImage = profileBanner?.sizes?.let {
            it.getOrElse("mobile_retina", { null }) ?: it.values.firstOrNull()
        }?.url,
        followersCount = publicMetrics?.followersCount ?: 0,
        friendsCount = publicMetrics?.followingCount ?: 0,
        listedCount = publicMetrics?.listedCount ?: 0,
        rawDesc = description ?: "",
        htmlDesc = autolink.autoLink(description ?: ""),
        location = location,
        website = entities?.url?.urls?.firstOrNull { it.url == url }?.expandedURL,
        verified = verified ?: false,
        isProtected = protected ?: false,
        userKey = MicroBlogKey.twitter(
            id ?: throw IllegalArgumentException("user.idStr should not be null")
        ),
        acct = MicroBlogKey.twitter(username ?: ""),
        platformType = PlatformType.Twitter,
        statusesCount = publicMetrics?.tweetCount ?: 0,
        extra = Json.encodeToString(
            DbTwitterUserExtra(
                pinned_tweet_id = pinnedTweetID,
                url = entities?.description?.urls?.map {
                    TwitterUrlEntity(
                        url = it.url ?: "",
                        expandedUrl = it.expandedURL ?: "",
                        displayUrl = it.displayURL ?: "",
                    )
                } ?: emptyList()
            )
        )
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

fun TwitterList.toDbList(accountKey: MicroBlogKey) = DbList(
    _id = UUID.randomUUID().toString(),
    ownerId = user?.idStr ?: "",
    listId = idStr ?: throw IllegalArgumentException("list.idStr should not be null"),
    title = name ?: "",
    description = description ?: "",
    mode = mode ?: "",
    replyPolicy = "",
    accountKey = accountKey,
    listKey = MicroBlogKey.twitter(idStr ?: throw IllegalArgumentException("list.idStr should not be null"),),
    isFollowed = following ?: true,
    allowToSubscribe = mode != ListsMode.PRIVATE.value
)

fun Trend.toDbTrend(accountKey: MicroBlogKey) = DbTrendWithHistory(
    trend = DbTrend(
        _id = UUID.randomUUID().toString(),
        trendKey = MicroBlogKey.twitter("$name:$url"),
        accountKey = accountKey,
        displayName = name ?: "",
        query = name ?: "",
        url = url ?: "",
        volume = tweetVolume ?: 0,
    ),
    history = emptyList()
)

fun DirectMessageEvent.generateConversationId(accountKey: MicroBlogKey): String {
    return if (accountKey.id == messageCreate?.senderId) {
        "${messageCreate?.senderId}-${messageCreate?.target?.recipientId}"
    } else {
        "${messageCreate?.target?.recipientId}-${messageCreate?.senderId}"
    }
}

fun DirectMessageEvent.toDbDirectMessage(accountKey: MicroBlogKey, sender: DbUser): DbDMEventWithAttachments {
    val message = DbDMEvent(
        _id = UUID.randomUUID().toString(),
        accountKey = accountKey,
        sortId = createdTimestamp?.toLong() ?: 0L,
        conversationKey = MicroBlogKey.twitter(generateConversationId(accountKey)),
        messageId = id ?: throw IllegalArgumentException("message id should not be null"),
        messageKey = MicroBlogKey.twitter("dm-${id ?: throw IllegalArgumentException("message id should not be null")}"),
        htmlText = autolink.autoLink(messageCreate?.messageData?.text ?: ""),
        originText = messageCreate?.messageData?.text ?: "",
        createdTimestamp = createdTimestamp?.toLong() ?: 0L,
        messageType = type ?: throw IllegalArgumentException("message type should not be null"),
        senderAccountKey = MicroBlogKey.twitter(messageCreate?.senderId ?: throw IllegalArgumentException("message sender id should not be null")),
        recipientAccountKey = MicroBlogKey.twitter(messageCreate?.target?.recipientId ?: throw IllegalArgumentException("message recipientId id should not be null")),
        sendStatus = DbDMEvent.SendStatus.SUCCESS
    )
    return DbDMEventWithAttachments(
        message = message,
        media = messageCreate?.messageData?.attachment?.media?.let { media ->
            val type = media.type?.let { MediaType.valueOf(it) } ?: MediaType.photo
            listOf(
                DbMedia(
                    _id = UUID.randomUUID().toString(),
                    belongToKey = message.messageKey,
                    url = media.url ?: "",
                    previewUrl = media.mediaURLHTTPS,
                    type = type,
                    mediaUrl = when (type) {
                        MediaType.photo -> media.mediaURLHTTPS
                        MediaType.animated_gif, MediaType.video -> media.videoInfo?.variants?.maxByOrNull {
                            it.bitrate ?: 0L
                        }?.url
                        MediaType.audio -> media.mediaURLHTTPS
                        MediaType.other -> media.mediaURLHTTPS
                    },
                    width = media.sizes?.large?.w ?: 0,
                    height = media.sizes?.large?.h ?: 0,
                    pageUrl = media.expandedURL,
                    altText = media.displayURL ?: "",
                    order = 0,
                )
            )
        } ?: emptyList(),
        urlEntity = messageCreate?.messageData?.entities?.urls?.map {
            DbUrlEntity(
                _id = UUID.randomUUID().toString(),
                statusKey = message.messageKey,
                url = it.url ?: "",
                expandedUrl = it.expanded_url ?: "",
                displayUrl = it.display_url ?: "",
                title = null,
                description = null,
                image = null,
            )
        } ?: emptyList(),
        sender = sender
    )
}

private fun ReplySettings?.toDbEnums() = when (this) {
    ReplySettings.MentionedUsers -> TwitterReplySettings.MentionedUsers
    ReplySettings.FollowingUsers -> TwitterReplySettings.FollowingUsers
    ReplySettings.Everyone, null -> TwitterReplySettings.Everyone
}
