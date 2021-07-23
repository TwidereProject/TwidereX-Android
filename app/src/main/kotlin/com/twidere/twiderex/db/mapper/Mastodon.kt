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

import com.twidere.services.mastodon.model.Account
import com.twidere.services.mastodon.model.Emoji
import com.twidere.services.mastodon.model.MastodonList
import com.twidere.services.mastodon.model.Mention
import com.twidere.services.mastodon.model.Notification
import com.twidere.services.mastodon.model.NotificationTypes
import com.twidere.services.mastodon.model.Status
import com.twidere.services.mastodon.model.Trend
import com.twidere.services.mastodon.model.Visibility
import com.twidere.twiderex.db.model.DbList
import com.twidere.twiderex.db.model.DbMastodonStatusExtra
import com.twidere.twiderex.db.model.DbMastodonUserExtra
import com.twidere.twiderex.db.model.DbMedia
import com.twidere.twiderex.db.model.DbPagingTimeline
import com.twidere.twiderex.db.model.DbPagingTimelineWithStatus
import com.twidere.twiderex.db.model.DbPreviewCard
import com.twidere.twiderex.db.model.DbStatusReaction
import com.twidere.twiderex.db.model.DbStatusV2
import com.twidere.twiderex.db.model.DbStatusWithMediaAndUser
import com.twidere.twiderex.db.model.DbStatusWithReference
import com.twidere.twiderex.db.model.DbTrend
import com.twidere.twiderex.db.model.DbTrendHistory
import com.twidere.twiderex.db.model.DbTrendWithHistory
import com.twidere.twiderex.db.model.DbUser
import com.twidere.twiderex.db.model.ReferenceType
import com.twidere.twiderex.db.model.toDbStatusReference
import com.twidere.twiderex.model.MastodonStatusType
import com.twidere.twiderex.model.MediaType
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.PlatformType
import com.twidere.twiderex.navigation.RootDeepLinksRoute
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import java.util.UUID
import java.util.regex.Pattern

fun Notification.toDbPagingTimeline(
    accountKey: MicroBlogKey,
    pagingKey: String,
): DbPagingTimelineWithStatus {
    val status = this.toDbStatusWithReference(accountKey = accountKey)
    return DbPagingTimelineWithStatus(
        timeline = DbPagingTimeline(
            _id = UUID.randomUUID().toString(),
            accountKey = accountKey,
            timestamp = createdAt?.time ?: 0,
            isGap = false,
            statusKey = status.status.data.statusKey,
            pagingKey = pagingKey,
            sortId = status.status.data.timestamp
        ),
        status = status,
    )
}

fun Notification.toDbStatusWithReference(
    accountKey: MicroBlogKey,
): DbStatusWithReference {
    val user = this.account?.toDbUser(accountKey = accountKey)
        ?: throw IllegalArgumentException("mastodon Notification.user should not be null")
    val relatedStatus = this.status?.toDbStatusWithMediaAndUser(accountKey = accountKey)
    val status = DbStatusV2(
        _id = UUID.randomUUID().toString(),
        statusId = id
            ?: throw IllegalArgumentException("mastodon Notification.id should not be null"),
        statusKey = accountKey.copy(
            id = id
                ?: throw IllegalArgumentException("mastodon Notification.id should not be null"),
        ),
        htmlText = "",
        rawText = "",
        timestamp = this.createdAt?.time ?: 0,
        retweetCount = 0,
        likeCount = 0,
        replyCount = 0,
        placeString = null,
        source = "",
        hasMedia = false,
        userKey = user.userKey,
        lang = null,
        is_possibly_sensitive = false,
        platformType = PlatformType.Mastodon,
        mastodonExtra = DbMastodonStatusExtra(
            type = this.type.toDbType(),
            emoji = emptyList(),
            visibility = Visibility.Public,
            sensitive = false,
            spoilerText = null,
            poll = null,
            card = null,
            mentions = null,
        ),
        inReplyToStatusId = null,
        inReplyToUserId = null,
    )
    return DbStatusWithReference(
        status = DbStatusWithMediaAndUser(
            data = status,
            media = emptyList(),
            user = user,
            reactions = emptyList(),
            url = emptyList(),
        ),
        references = listOfNotNull(
            relatedStatus.toDbStatusReference(
                status.statusKey,
                ReferenceType.MastodonNotification
            ),
        ),
    )
}

private fun NotificationTypes?.toDbType(): MastodonStatusType {
    return when (this) {
        NotificationTypes.follow -> MastodonStatusType.NotificationFollow
        NotificationTypes.favourite -> MastodonStatusType.NotificationFavourite
        NotificationTypes.reblog -> MastodonStatusType.NotificationReblog
        NotificationTypes.mention -> MastodonStatusType.NotificationMention
        NotificationTypes.poll -> MastodonStatusType.NotificationPoll
        NotificationTypes.follow_request -> MastodonStatusType.NotificationFollowRequest
        NotificationTypes.status -> MastodonStatusType.NotificationStatus
        null -> throw IllegalArgumentException("mastodon Notification.type should not be null")
    }
}

fun Status.toDbPagingTimeline(
    accountKey: MicroBlogKey,
    pagingKey: String,
): DbPagingTimelineWithStatus {
    val status = this.toDbStatusWithReference(accountKey = accountKey)

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
    val retweet = this.reblog?.toDbStatusWithMediaAndUser(
        accountKey
    )

    return DbStatusWithReference(
        status = status,
        references = listOfNotNull(
            retweet.toDbStatusReference(status.data.statusKey, ReferenceType.Retweet),
        ),
    )
}

private fun Status.toDbStatusWithMediaAndUser(
    accountKey: MicroBlogKey
): DbStatusWithMediaAndUser {
    val user = account?.toDbUser(accountKey = accountKey)
        ?: throw IllegalArgumentException("mastodon Status.user should not be null")
    val status = DbStatusV2(
        _id = UUID.randomUUID().toString(),
        statusId = id ?: throw IllegalArgumentException("mastodon Status.idStr should not be null"),
        rawText = content ?: "",
        htmlText = content?.let {
            generateHtmlContentWithEmoji(
                content = it,
                emojis = emojis ?: emptyList()
            )
        }?.let {
            generateWithMentionLink(
                content = it,
                mentions = mentions ?: emptyList(),
                accountKey = accountKey,
            )
        }?.let {
            generateWithHashtag(content = it)
        } ?: "",
        timestamp = createdAt?.time ?: 0,
        retweetCount = reblogsCount ?: 0,
        likeCount = favouritesCount ?: 0,
        replyCount = repliesCount ?: 0,
        placeString = "",
        hasMedia = !mediaAttachments.isNullOrEmpty(),
        source = application?.name ?: "",
        userKey = user.userKey,
        lang = null,
        statusKey = MicroBlogKey(
            id ?: throw IllegalArgumentException("mastodon Status.idStr should not be null"),
            host = user.userKey.host,
        ),
        is_possibly_sensitive = sensitive ?: false,
        platformType = PlatformType.Mastodon,
        mastodonExtra = DbMastodonStatusExtra(
            type = MastodonStatusType.Status,
            emoji = emojis ?: emptyList(),
            visibility = visibility ?: Visibility.Public,
            sensitive = sensitive ?: false,
            spoilerText = spoilerText?.takeIf { it.isNotEmpty() },
            poll = poll,
            card = card,
            mentions = mentions,
        ),
        previewCard = card?.url?.let { url ->
            DbPreviewCard(
                link = url,
                displayLink = card?.url,
                title = card?.title,
                desc = card?.description?.takeIf { it.isNotEmpty() && it.isNotBlank() },
                image = card?.image,
            )
        },
        inReplyToUserId = inReplyToAccountID,
        inReplyToStatusId = inReplyToID
    )
    return DbStatusWithMediaAndUser(
        data = status,
        media = (mediaAttachments ?: emptyList()).mapIndexed { index, it ->
            DbMedia(
                _id = UUID.randomUUID().toString(),
                belongToKey = status.statusKey,
                previewUrl = it.previewURL,
                mediaUrl = it.url,
                width = it.meta?.original?.width ?: 0,
                height = it.meta?.original?.height ?: 0,
                pageUrl = it.textURL,
                altText = it.description ?: "",
                url = it.url,
                type = it.type?.let {
                    when (it) {
                        com.twidere.services.mastodon.model.MediaType.image -> MediaType.photo
                        com.twidere.services.mastodon.model.MediaType.unknown -> MediaType.other
                        com.twidere.services.mastodon.model.MediaType.gifv -> MediaType.video
                        com.twidere.services.mastodon.model.MediaType.video -> MediaType.video
                        com.twidere.services.mastodon.model.MediaType.audio -> MediaType.audio
                    }
                } ?: MediaType.photo,
                order = index,
            )
        },
        user = user,
        reactions = if (favourited == true || reblogged == true) {
            listOf(
                DbStatusReaction(
                    _id = UUID.randomUUID().toString(),
                    statusKey = status.statusKey,
                    accountKey = accountKey,
                    liked = favourited == true,
                    retweeted = reblogged == true,
                ),
            )
        } else {
            emptyList()
        },
        url = emptyList(),
    )
}

fun Account.toDbUser(
    accountKey: MicroBlogKey
): DbUser {
    return DbUser(
        _id = UUID.randomUUID().toString(),
        userId = this.id ?: throw IllegalArgumentException("mastodon user.id should not be null"),
        name = displayName
            ?: throw IllegalArgumentException("mastodon user.displayName should not be null"),
        screenName = username
            ?: throw IllegalArgumentException("mastodon user.username should not be null"),
        userKey = MicroBlogKey(
            id ?: throw IllegalArgumentException("mastodon user.id should not be null"),
            accountKey.host,
        ),
        profileImage = avatar ?: avatarStatic ?: "",
        profileBackgroundImage = header ?: headerStatic ?: "",
        followersCount = followersCount ?: 0,
        friendsCount = followingCount ?: 0,
        listedCount = 0,
        rawDesc = note ?: "",
        htmlDesc = note?.let {
            generateHtmlContentWithEmoji(
                content = it,
                emojis = emojis ?: emptyList()
            )
        }?.let {
            generateWithHashtag(content = it)
        } ?: "",
        website = null,
        location = null,
        verified = false,
        isProtected = false,
        acct = acct?.let { MicroBlogKey.valueOf(it) }?.let {
            if (it.host.isEmpty()) {
                it.copy(host = accountKey.host)
            } else {
                it
            }
        } ?: throw IllegalArgumentException("mastodon user.acct should not be null"),
        platformType = PlatformType.Mastodon,
        statusesCount = statusesCount ?: 0L,
        mastodonExtra = DbMastodonUserExtra(
            fields = fields ?: emptyList(),
            bot = bot ?: false,
            locked = locked ?: false,
            emoji = emojis ?: emptyList(),
        )
    )
}

fun MastodonList.toDbList(accountKey: MicroBlogKey): DbList {
    return DbList(
        _id = UUID.randomUUID().toString(),
        ownerId = accountKey.id,
        listId = id ?: throw IllegalArgumentException("list.idStr should not be null"),
        title = title ?: "",
        description = "",
        mode = "",
        replyPolicy = repliesPolicy ?: "",
        accountKey = accountKey,
        listKey = MicroBlogKey(
            id ?: throw IllegalArgumentException("list.idStr should not be null"),
            accountKey.host
        ),
        isFollowed = true,
        allowToSubscribe = false,
    )
}

fun Trend.toDbTrend(accountKey: MicroBlogKey): DbTrendWithHistory {
    return DbTrendWithHistory(
        trend = DbTrend(
            _id = UUID.randomUUID().toString(),
            trendKey = MicroBlogKey("$name:$url", accountKey.host),
            accountKey = accountKey,
            displayName = name ?: "",
            query = name ?: "",
            url = url ?: "",
            volume = 0
        ),
        history = history?.map {
            DbTrendHistory(
                _id = UUID.randomUUID().toString(),
                trendKey = MicroBlogKey("$name:$url", accountKey.host),
                day = it.day?.toLong() ?: 0L,
                uses = it.uses?.toLong() ?: 0L,
                accounts = it.accounts?.toLong() ?: 0L,
                accountKey = accountKey
            )
        } ?: emptyList()
    )
}

private fun generateHtmlContentWithEmoji(
    content: String,
    emojis: List<Emoji>,
): String {
    var result = content
    emojis.forEach { (shortcode, url) ->
        val regex = Pattern.compile(":$shortcode:", Pattern.LITERAL).toRegex()
        result = result.replace(regex = regex) {
            "<emoji target=\"$url\">:$shortcode:</emoji>"
        }
    }
    return result
}

private fun generateWithMentionLink(
    content: String,
    mentions: List<Mention>,
    accountKey: MicroBlogKey,
): String {
    if (mentions.isEmpty()) {
        return content
    }
    val body = Jsoup.parse(content).body()
    body.childNodes().forEach { replaceMention(mentions, it, accountKey) }
    return body.html()
}

private fun replaceMention(mentions: List<Mention>, node: Node, accountKey: MicroBlogKey) {
    if (mentions.any { it.url == node.attr("href") }) {
        val id = mentions.firstOrNull { it.url == node.attr("href") }?.id
        if (id != null) {
            node.attr(
                "href",
                RootDeepLinksRoute.User(MicroBlogKey(id, accountKey.host))
            )
        }
    } else {
        node.childNodes().forEach { replaceMention(mentions, it, accountKey) }
    }
}

private fun generateWithHashtag(content: String): String {
    val body = Jsoup.parse(content).body()
    body.childNodes().forEach { replaceHashTag(it) }
    return body.html()
}

private fun replaceHashTag(node: Node) {
    if (node is Element && node.normalName() == "a" && node.hasText() && node.text()
        .startsWith('#')
    ) {
        node.attr(
            "href",
            RootDeepLinksRoute.Mastodon.Hashtag(node.text().trimStart('#'))
        )
    } else {
        node.childNodes().forEach { replaceHashTag(it) }
    }
}
