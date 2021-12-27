/*
 *  Twidere X
 *
 *  Copyright (C) TwidereProject and Contributors
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
package com.twidere.twiderex.dataprovider.mapper

import com.twidere.services.twitter.model.DirectMessageEvent
import com.twidere.services.twitter.model.ReferencedTweetType
import com.twidere.services.twitter.model.ReplySettings
import com.twidere.services.twitter.model.Status
import com.twidere.services.twitter.model.StatusV2
import com.twidere.services.twitter.model.Trend
import com.twidere.services.twitter.model.TwitterList
import com.twidere.services.twitter.model.User
import com.twidere.services.twitter.model.UserV2
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.MediaType
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.enums.ReferenceType
import com.twidere.twiderex.model.enums.TwitterReplySettings
import com.twidere.twiderex.model.paging.PagingTimeLine
import com.twidere.twiderex.model.paging.PagingTimeLineWithStatus
import com.twidere.twiderex.model.ui.ListsMode
import com.twidere.twiderex.model.ui.StatusMetrics
import com.twidere.twiderex.model.ui.UiCard
import com.twidere.twiderex.model.ui.UiDMEvent
import com.twidere.twiderex.model.ui.UiGeo
import com.twidere.twiderex.model.ui.UiList
import com.twidere.twiderex.model.ui.UiMedia
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.model.ui.UiTrend
import com.twidere.twiderex.model.ui.UiUrlEntity
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.model.ui.UserMetrics
import com.twidere.twiderex.model.ui.twitter.TwitterStatusExtra
import com.twidere.twiderex.model.ui.twitter.TwitterUserExtra
import com.twidere.twiderex.navigation.IRoute
import com.twidere.twiderex.navigation.RootDeepLinks
import com.twitter.twittertext.Autolink

val autolink by lazy {
    Autolink().apply {
        setUsernameIncludeSymbol(true)
        hashtagUrlBase = "${generateDeepLinkBase(RootDeepLinks.Search)}/%23"
        cashtagUrlBase = "${generateDeepLinkBase(RootDeepLinks.Search)}/%24"
        usernameUrlBase = "${generateDeepLinkBase(RootDeepLinks.Twitter.User)}/"
    }
}

private fun generateDeepLinkBase(route: IRoute) = generateDeepLinkBase(route.route)

private fun generateDeepLinkBase(deeplink: String): String {
    return deeplink.substring(
        0,
        deeplink.indexOf("/{")
    )
}

fun StatusV2.toPagingTimeline(
    accountKey: MicroBlogKey,
    pagingKey: String,
): PagingTimeLineWithStatus {
    val status = toUiStatus(accountKey, isGap = false)
    return PagingTimeLineWithStatus(
        timeline = PagingTimeLine(
            accountKey = accountKey,
            timestamp = status.timestamp,
            isGap = false,
            statusKey = status.statusKey,
            pagingKey = pagingKey,
            sortId = status.timestamp
        ),
        status = status,
    )
}

fun Status.toPagingTimeline(
    accountKey: MicroBlogKey,
    pagingKey: String,
): PagingTimeLineWithStatus {
    val status = toUiStatus(accountKey = accountKey)

    return PagingTimeLineWithStatus(
        timeline = PagingTimeLine(
            accountKey = accountKey,
            timestamp = status.timestamp,
            isGap = false,
            statusKey = status.statusKey,
            pagingKey = pagingKey,
            sortId = status.timestamp
        ),
        status = status,
    )
}

internal fun StatusV2.toUiStatus(
    @Suppress("UNUSED_PARAMETER")
    accountKey: MicroBlogKey,
    isGap: Boolean = false,
): UiStatus {
    val retweet = this.referencedTweets
        ?.firstOrNull { it.type == ReferencedTweetType.retweeted }?.status?.toUiStatus(
            accountKey
        )
    val replyTo = this.let {
        it.referencedTweets
            ?.firstOrNull { it.type == ReferencedTweetType.retweeted }?.status ?: it
    }.referencedTweets
        ?.firstOrNull { it.type == ReferencedTweetType.replied_to }?.status?.toUiStatus(
            accountKey
        )
    val quote = this.let {
        it.referencedTweets
            ?.firstOrNull { it.type == ReferencedTweetType.retweeted }?.status ?: it
    }.referencedTweets
        ?.firstOrNull { it.type == ReferencedTweetType.quoted }?.status?.toUiStatus(
            accountKey
        )

    val user = user?.toUiUser() ?: throw IllegalArgumentException("Status.user should not be null")
    val statusKey = MicroBlogKey.twitter(
        id ?: throw IllegalArgumentException("Status.idStr should not be null")
    )
    return UiStatus(
        statusId = id ?: throw IllegalArgumentException("Status.idStr should not be null"),
        sensitive = possiblySensitive ?: false,
        rawText = text ?: "",
        htmlText = autolink.autoLink(text ?: ""),
        timestamp = createdAt?.time ?: 0,
        metrics = StatusMetrics(
            retweet = publicMetrics?.retweetCount ?: 0,
            like = publicMetrics?.likeCount ?: 0,
            reply = publicMetrics?.replyCount ?: 0,
        ),
        geo = UiGeo(
            name = place?.fullName ?: ""
        ),
        hasMedia = !attachments?.media.isNullOrEmpty(),
        source = source ?: "",
        user = user,
        statusKey = statusKey,
        platformType = PlatformType.Twitter,
        extra = TwitterStatusExtra(
            reply_settings = replySettings.toDbEnums(),
            quoteCount = publicMetrics?.quoteCount
        ),
        card = entities?.urls?.firstOrNull()
            ?.takeUnless { url ->
                referencedTweets?.firstOrNull { it.type == ReferencedTweetType.quoted }
                    ?.id?.let { id -> url.expandedURL?.endsWith(id) == true } == true
            }
            ?.takeUnless { url -> url.displayURL?.contains("pic.twitter.com") == true }
            ?.let {
                it.expandedURL?.let { url ->
                    UiCard(
                        link = url,
                        title = it.title,
                        description = it.description,
                        image = it.images?.firstOrNull()?.url,
                        displayLink = it.displayURL,
                    )
                }
            },
        inReplyToStatusId = referencedTweets?.find { it.type == ReferencedTweetType.replied_to }?.id,
        inReplyToUserId = inReplyToUserId,
        retweeted = false,
        liked = false,
        media = (attachments?.media ?: emptyList()).filter {
            it.type == MediaType.photo.name // TODO: video and gif
        }.mapIndexed { index, it ->
            val type = it.type?.let { MediaType.valueOf(it) } ?: MediaType.photo
            UiMedia(
                belongToKey = statusKey,
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
        isGap = isGap,
        url = entities?.urls?.map {
            UiUrlEntity(
                url = it.url ?: "",
                expandedUrl = it.expandedURL ?: "",
                displayUrl = it.displayURL ?: "",
                title = it.title,
                description = it.description,
                image = it.images?.maxByOrNull { it.width ?: it.height ?: 0 }?.url
            )
        } ?: emptyList(),
        referenceStatus = mutableMapOf<ReferenceType, UiStatus>().apply {
            replyTo?.let { this[ReferenceType.Reply] = it }
            quote?.let { this[ReferenceType.Quote] = it }
            retweet?.let { this[ReferenceType.Retweet] = it }
        }
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

internal fun Status.toUiStatus(
    accountKey: MicroBlogKey,
    isGap: Boolean = false,
): UiStatus {
    val retweet = retweetedStatus?.toUiStatus(accountKey)
    val quote =
        (retweetedStatus?.quotedStatus ?: quotedStatus)?.toUiStatus(accountKey)

    val user = user?.toUiUser() ?: throw IllegalArgumentException("Status.user should not be null")
    val statusKey = MicroBlogKey.twitter(
        idStr ?: throw IllegalArgumentException("Status.idStr should not be null")
    )
    return UiStatus(
        statusId = idStr ?: throw IllegalArgumentException("Status.idStr should not be null"),
        sensitive = possiblySensitive ?: false,
        rawText = fullText ?: text ?: "",
        htmlText = autolink.autoLink(fullText ?: text ?: ""),
        timestamp = createdAt?.time ?: 0,
        metrics = StatusMetrics(
            retweet = retweetCount ?: 0,
            like = favoriteCount ?: 0,
            reply = 0,
        ),
        geo = UiGeo(
            name = place?.fullName ?: ""
        ),
        hasMedia = extendedEntities?.media != null || entities?.media != null,
        source = source ?: "",
        user = user,
        statusKey = statusKey,
        platformType = PlatformType.Twitter,
        extra = TwitterStatusExtra(
            reply_settings = TwitterReplySettings.Everyone,
        ),
        card = entities?.urls?.firstOrNull()
            ?.takeUnless { url -> quotedStatus?.idStr?.let { id -> url.expandedURL?.endsWith(id) == true } == true }
            ?.takeUnless { url -> url.expandedURL?.contains("pic.twitter.com") == true }
            ?.let {
                it.url?.let { url ->
                    UiCard(
                        link = it.expandedURL ?: url,
                        displayLink = it.displayURL,
                        image = null,
                        title = null,
                        description = null,
                    )
                }
            },
        inReplyToUserId = inReplyToUserIDStr,
        inReplyToStatusId = inReplyToStatusIDStr,
        media = (
            extendedEntities?.media ?: entities?.media
                ?: emptyList()
            ).mapIndexed { index, it ->
            val type = it.type?.let { MediaType.valueOf(it) } ?: MediaType.photo
            UiMedia(
                belongToKey = statusKey,
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
        liked = favorited == true,
        retweeted = retweeted == true,
        isGap = isGap,
        url = entities?.urls?.map {
            UiUrlEntity(
                url = it.url ?: "",
                expandedUrl = it.expandedURL ?: "",
                displayUrl = it.displayURL ?: "",
                title = null,
                description = null,
                image = null,
            )
        } ?: emptyList(),
        referenceStatus = mutableMapOf<ReferenceType, UiStatus>().apply {
            quote?.let { this[ReferenceType.Quote] = it }
            retweet?.let { this[ReferenceType.Retweet] = it }
        }
    )
}

internal fun User.toUiUser(): UiUser {
    return UiUser(
        id = this.idStr ?: throw IllegalArgumentException("user.idStr should not be null"),
        name = this.name ?: "",
        screenName = this.screenName ?: "",
        profileImage = (profileImageURLHTTPS ?: profileImageURL)?.let { updateProfileImagePath(it) }
            ?: "",
        profileBackgroundImage = profileBannerURL,
        metrics = UserMetrics(
            fans = this.followersCount ?: 0,
            follow = this.friendsCount ?: 0,
            listed = this.listedCount ?: 0,
            status = statusesCount ?: 0,
        ),
        rawDesc = this.description ?: "",
        htmlDesc = autolink.autoLink(this.description ?: ""),
        location = this.location,
        website = this.entities?.url?.urls?.firstOrNull { it.url == this.url }?.expandedURL,
        verified = this.verified ?: false,
        protected = this.protected ?: false,
        userKey = MicroBlogKey.twitter(
            idStr ?: throw IllegalArgumentException("user.idStr should not be null")
        ),
        platformType = PlatformType.Twitter,
        acct = MicroBlogKey.twitter(screenName ?: ""),
        extra = TwitterUserExtra(
            pinned_tweet_id = null,
            url = entities?.description?.urls?.map {
                UiUrlEntity(
                    url = it.url ?: "",
                    expandedUrl = it.expandedURL ?: "",
                    displayUrl = it.displayURL ?: "",
                    title = "",
                    description = "",
                    image = null
                )
            } ?: emptyList()
        )
    )
}

internal fun UserV2.toUiUser(): UiUser {
    return UiUser(
        id = id ?: throw IllegalArgumentException("user.idStr should not be null"),
        name = name ?: "",
        screenName = username ?: "",
        profileImage = profileImageURL?.let { updateProfileImagePath(it) } ?: "",
        profileBackgroundImage = profileBanner?.sizes?.let {
            it.getOrElse("mobile_retina", { null }) ?: it.values.firstOrNull()
        }?.url,
        metrics = UserMetrics(
            fans = publicMetrics?.followersCount ?: 0,
            follow = publicMetrics?.followingCount ?: 0,
            listed = publicMetrics?.listedCount ?: 0,
            status = publicMetrics?.tweetCount ?: 0,
        ),
        rawDesc = description ?: "",
        htmlDesc = autolink.autoLink(description ?: ""),
        location = location,
        website = entities?.url?.urls?.firstOrNull { it.url == url }?.expandedURL,
        verified = verified ?: false,
        protected = protected ?: false,
        userKey = MicroBlogKey.twitter(
            id ?: throw IllegalArgumentException("user.idStr should not be null")
        ),
        acct = MicroBlogKey.twitter(username ?: ""),
        platformType = PlatformType.Twitter,
        extra = TwitterUserExtra(
            pinned_tweet_id = pinnedTweetID,
            url = entities?.description?.urls?.map {
                UiUrlEntity(
                    url = it.url ?: "",
                    expandedUrl = it.expandedURL ?: "",
                    displayUrl = it.displayURL ?: "",
                    title = null,
                    description = null,
                    image = null
                )
            } ?: emptyList()
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

private enum class ProfileImageSize {
    original,
    reasonably_small,
    bigger,
    normal,
    mini,
}

internal fun TwitterList.toUiList(accountKey: MicroBlogKey) = UiList(
    ownerId = user?.idStr ?: "",
    id = idStr ?: throw IllegalArgumentException("list.idStr should not be null"),
    title = name ?: "",
    descriptions = description ?: "",
    mode = mode ?: "",
    replyPolicy = "",
    accountKey = accountKey,
    listKey = MicroBlogKey.twitter(idStr ?: throw IllegalArgumentException("list.idStr should not be null"),),
    isFollowed = following ?: true,
    allowToSubscribe = mode != ListsMode.PRIVATE.value
)

internal fun Trend.toUiTrend(accountKey: MicroBlogKey) = UiTrend(
    accountKey = accountKey,
    trendKey = MicroBlogKey.twitter("$name:$url"),
    displayName = name ?: "",
    query = name ?: "",
    url = url ?: "",
    volume = tweetVolume ?: 0,
    history = emptyList()
)

fun DirectMessageEvent.generateConversationId(accountKey: MicroBlogKey): String {
    return if (accountKey.id == messageCreate?.senderId) {
        "${messageCreate?.senderId}-${messageCreate?.target?.recipientId}"
    } else {
        "${messageCreate?.target?.recipientId}-${messageCreate?.senderId}"
    }
}

fun DirectMessageEvent.toUiDMEvent(accountKey: MicroBlogKey, sender: UiUser): UiDMEvent {
    val messageKey = MicroBlogKey.twitter("dm-${id ?: throw IllegalArgumentException("message id should not be null")}")
    return UiDMEvent(
        accountKey = accountKey,
        sortId = createdTimestamp?.toLong() ?: 0L,
        conversationKey = MicroBlogKey.twitter(generateConversationId(accountKey)),
        messageId = id ?: throw IllegalArgumentException("message id should not be null"),
        messageKey = messageKey,
        htmlText = autolink.autoLink(messageCreate?.messageData?.text ?: ""),
        originText = messageCreate?.messageData?.text ?: "",
        createdTimestamp = createdTimestamp?.toLong() ?: 0L,
        messageType = type ?: throw IllegalArgumentException("message type should not be null"),
        senderAccountKey = MicroBlogKey.twitter(messageCreate?.senderId ?: throw IllegalArgumentException("message sender id should not be null")),
        recipientAccountKey = MicroBlogKey.twitter(messageCreate?.target?.recipientId ?: throw IllegalArgumentException("message recipientId id should not be null")),
        sendStatus = UiDMEvent.SendStatus.SUCCESS,
        media = messageCreate?.messageData?.attachment?.media?.let { media ->
            val type = media.type?.let { MediaType.valueOf(it) } ?: MediaType.photo
            listOf(
                UiMedia(
                    belongToKey = messageKey,
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
            UiUrlEntity(
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
