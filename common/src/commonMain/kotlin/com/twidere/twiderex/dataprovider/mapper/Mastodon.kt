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

import com.twidere.services.mastodon.model.Account
import com.twidere.services.mastodon.model.Emoji
import com.twidere.services.mastodon.model.MastodonList
import com.twidere.services.mastodon.model.Mention
import com.twidere.services.mastodon.model.Notification
import com.twidere.services.mastodon.model.NotificationTypes
import com.twidere.services.mastodon.model.Poll
import com.twidere.services.mastodon.model.Status
import com.twidere.services.mastodon.model.Trend
import com.twidere.services.mastodon.model.Visibility
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.MastodonStatusType
import com.twidere.twiderex.model.enums.MastodonVisibility
import com.twidere.twiderex.model.enums.MediaType
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.enums.ReferenceType
import com.twidere.twiderex.model.paging.PagingTimeLine
import com.twidere.twiderex.model.paging.PagingTimeLineWithStatus
import com.twidere.twiderex.model.ui.Option
import com.twidere.twiderex.model.ui.StatusMetrics
import com.twidere.twiderex.model.ui.UiCard
import com.twidere.twiderex.model.ui.UiGeo
import com.twidere.twiderex.model.ui.UiList
import com.twidere.twiderex.model.ui.UiMedia
import com.twidere.twiderex.model.ui.UiPoll
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.model.ui.UiTrend
import com.twidere.twiderex.model.ui.UiTrendHistory
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.model.ui.UserMetrics
import com.twidere.twiderex.model.ui.mastodon.Field
import com.twidere.twiderex.model.ui.mastodon.MastodonMention
import com.twidere.twiderex.model.ui.mastodon.MastodonStatusExtra
import com.twidere.twiderex.model.ui.mastodon.MastodonUserExtra
import com.twidere.twiderex.navigation.RootDeepLinks
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import java.util.regex.Pattern

fun Notification.toPagingTimeline(
    accountKey: MicroBlogKey,
    pagingKey: String,
): PagingTimeLineWithStatus {
    val status = this.toUiStatus(accountKey = accountKey)
    return PagingTimeLineWithStatus(
        timeline = PagingTimeLine(
            accountKey = accountKey,
            timestamp = createdAt?.time ?: 0,
            isGap = false,
            statusKey = status.statusKey,
            pagingKey = pagingKey,
            sortId = status.timestamp
        ),
        status = status,
    )
}

fun Notification.toUiStatus(
    accountKey: MicroBlogKey,
    isGap: Boolean = false
): UiStatus {
    val user = this.account?.toUiUser(accountKey = accountKey)
        ?: throw IllegalArgumentException("mastodon Notification.user should not be null")
    val relatedStatus = this.status?.toUiStatus(accountKey = accountKey)
    val statusKey = accountKey.copy(
        id = id
            ?: throw IllegalArgumentException("mastodon Notification.id should not be null"),
    )
    return UiStatus(
        statusId = id
            ?: throw IllegalArgumentException("mastodon Notification.id should not be null"),
        statusKey = statusKey,
        htmlText = "",
        rawText = "",
        timestamp = this.createdAt?.time ?: 0,
        metrics = StatusMetrics(
            retweet = 0,
            like = 0,
            reply = 0
        ),
        geo = UiGeo(
            name = ""
        ),
        source = "",
        hasMedia = false,
        user = user,
        sensitive = false,
        platformType = PlatformType.Mastodon,
        extra = MastodonStatusExtra(
            type = this.type.toDbType(),
            emoji = emptyList(),
            visibility = MastodonVisibility.Public,
            mentions = null,
        ),
        spoilerText = null,
        poll = null,
        card = null,
        inReplyToStatusId = null,
        inReplyToUserId = null,
        media = emptyList(),
        url = emptyList(),
        liked = false,
        retweeted = false,
        referenceStatus = mutableMapOf<ReferenceType, UiStatus>().apply {
            relatedStatus?.let { this[ReferenceType.MastodonNotification] = it }
        },
        isGap = isGap
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

fun Status.toPagingTimeline(
    accountKey: MicroBlogKey,
    pagingKey: String,
): PagingTimeLineWithStatus {
    val status = this.toUiStatus(accountKey = accountKey, false)

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

internal fun Status.toUiStatus(
    accountKey: MicroBlogKey,
    isGap: Boolean = false
): UiStatus {
    val retweet = this.reblog?.toUiStatus(
        accountKey
    )
    val user = account?.toUiUser(accountKey = accountKey)
        ?: throw IllegalArgumentException("mastodon Status.user should not be null")
    val statusKey = MicroBlogKey(
        id ?: throw IllegalArgumentException("mastodon Status.idStr should not be null"),
        host = user.userKey.host,
    )
    return UiStatus(
        statusId = id ?: throw IllegalArgumentException("mastodon Status.idStr should not be null"),
        rawText = content?.let { Jsoup.parse(it).wholeText() } ?: "",
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
        metrics = StatusMetrics(
            retweet = reblogsCount ?: 0,
            like = favouritesCount ?: 0,
            reply = repliesCount ?: 0,
        ),
        geo = UiGeo(
            name = ""
        ),
        hasMedia = !mediaAttachments.isNullOrEmpty(),
        source = application?.name ?: "",
        user = user,
        statusKey = statusKey,
        sensitive = sensitive ?: false,
        platformType = PlatformType.Mastodon,
        spoilerText = spoilerText?.takeIf { it.isNotEmpty() },
        poll = poll?.toUi(),
        extra = MastodonStatusExtra(
            type = MastodonStatusType.Status,
            emoji = emojis?.toUi() ?: emptyList(),
            visibility = visibility.toMastodonVisibility(),
            mentions = mentions?.map {
                MastodonMention(
                    id = it.id,
                    username = it.username,
                    url = it.url,
                    acct = it.acct
                )
            },
        ),
        card = card?.url?.let { url ->
            UiCard(
                link = url,
                displayLink = card?.url,
                title = card?.title,
                description = card?.description?.takeIf { it.isNotEmpty() && it.isNotBlank() },
                image = card?.image,
            )
        },
        inReplyToUserId = inReplyToAccountID,
        inReplyToStatusId = inReplyToID,
        media = (mediaAttachments ?: emptyList()).mapIndexed { index, it ->
            UiMedia(
                belongToKey = statusKey,
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
        liked = favourited == true,
        retweeted = reblogged == true,
        referenceStatus = mutableMapOf<ReferenceType, UiStatus>().apply {
            retweet?.let { this[ReferenceType.Retweet] = it }
        },
        isGap = isGap,
        url = emptyList()
    )
}

fun Poll.toUi() = UiPoll(
    id = id ?: "",
    options = options?.map { option ->
        Option(
            text = option.title ?: "",
            count = option.votesCount ?: 0
        )
    } ?: emptyList(),
    expiresAt = expiresAt?.time,
    expired = expired ?: false,
    multiple = multiple ?: false,
    voted = voted ?: false,
    votesCount = votesCount ?: 0,
    votersCount = votersCount ?: 0,
    ownVotes = ownVotes
)

internal fun Account.toUiUser(
    accountKey: MicroBlogKey
): UiUser {
    return UiUser(
        id = this.id ?: throw IllegalArgumentException("mastodon user.id should not be null"),
        name = displayName?.let {
            generateHtmlContentWithEmoji(it, emojis ?: emptyList())
        } ?: throw IllegalArgumentException("mastodon user.displayName should not be null"),
        screenName = username
            ?: throw IllegalArgumentException("mastodon user.username should not be null"),
        userKey = MicroBlogKey(
            id ?: throw IllegalArgumentException("mastodon user.id should not be null"),
            accountKey.host,
        ),
        profileImage = avatar ?: avatarStatic ?: "",
        profileBackgroundImage = header ?: headerStatic ?: "",
        metrics = UserMetrics(
            fans = followersCount ?: 0,
            follow = followingCount ?: 0,
            listed = 0,
            status = statusesCount ?: 0L,
        ),

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
        protected = false,
        acct = acct?.let { MicroBlogKey.valueOf(it) }?.let {
            if (it.host.isEmpty()) {
                it.copy(host = accountKey.host)
            } else {
                it
            }
        } ?: throw IllegalArgumentException("mastodon user.acct should not be null"),
        platformType = PlatformType.Mastodon,
        extra = MastodonUserExtra(
            fields = fields?.map { field ->
                Field(
                    field.name,
                    field.value
                )
            } ?: emptyList(),
            bot = bot ?: false,
            locked = locked ?: false,
            emoji = emojis?.toUi() ?: emptyList(),
        )
    )
}

fun MastodonList.toUiList(accountKey: MicroBlogKey): UiList {
    return UiList(
        ownerId = accountKey.id,
        id = id ?: throw IllegalArgumentException("list.idStr should not be null"),
        title = title ?: "",
        descriptions = "",
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

fun Trend.toUiTrend(accountKey: MicroBlogKey): UiTrend {
    return UiTrend(
        accountKey = accountKey,
        trendKey = MicroBlogKey("$name:$url", accountKey.host),
        displayName = name ?: "",
        query = name ?: "",
        url = url ?: "",
        volume = 0,
        history = history?.map {
            UiTrendHistory(
                trendKey = MicroBlogKey("$name:$url", accountKey.host),
                day = it.day?.toLong() ?: 0L,
                uses = it.uses?.toLong() ?: 0L,
                accounts = it.accounts?.toLong() ?: 0L,
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
                RootDeepLinks.User(MicroBlogKey(id, accountKey.host))
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
            RootDeepLinks.Mastodon.Hashtag(node.text().trimStart('#'))
        )
    } else {
        node.childNodes().forEach { replaceHashTag(it) }
    }
}

private fun Visibility?.toMastodonVisibility() = when (this) {
    Visibility.Unlisted -> MastodonVisibility.Unlisted
    Visibility.Private -> MastodonVisibility.Private
    Visibility.Direct -> MastodonVisibility.Direct
    Visibility.Public, null -> MastodonVisibility.Public
}
