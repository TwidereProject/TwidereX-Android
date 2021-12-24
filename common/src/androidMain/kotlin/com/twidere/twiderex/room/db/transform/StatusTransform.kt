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
package com.twidere.twiderex.room.db.transform

import com.twidere.services.mastodon.model.Emoji
import com.twidere.services.mastodon.model.Mention
import com.twidere.services.mastodon.model.Poll
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.enums.ReferenceType
import com.twidere.twiderex.model.paging.PagingTimeLine
import com.twidere.twiderex.model.paging.PagingTimeLineWithStatus
import com.twidere.twiderex.model.ui.Option
import com.twidere.twiderex.model.ui.StatusMetrics
import com.twidere.twiderex.model.ui.UiCard
import com.twidere.twiderex.model.ui.UiGeo
import com.twidere.twiderex.model.ui.UiMedia
import com.twidere.twiderex.model.ui.UiPoll
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.model.ui.UiUrlEntity
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.model.ui.mastodon.MastodonMention
import com.twidere.twiderex.model.ui.mastodon.MastodonStatusExtra
import com.twidere.twiderex.model.ui.twitter.TwitterStatusExtra
import com.twidere.twiderex.room.db.model.DbMastodonStatusExtra
import com.twidere.twiderex.room.db.model.DbPagingTimeline
import com.twidere.twiderex.room.db.model.DbPagingTimelineWithStatus
import com.twidere.twiderex.room.db.model.DbPoll
import com.twidere.twiderex.room.db.model.DbPollOption
import com.twidere.twiderex.room.db.model.DbPreviewCard
import com.twidere.twiderex.room.db.model.DbStatusReaction
import com.twidere.twiderex.room.db.model.DbStatusReference
import com.twidere.twiderex.room.db.model.DbStatusReferenceWithStatus
import com.twidere.twiderex.room.db.model.DbStatusV2
import com.twidere.twiderex.room.db.model.DbStatusWithMediaAndUser
import com.twidere.twiderex.room.db.model.DbStatusWithReference
import com.twidere.twiderex.room.db.model.DbTwitterStatusExtra
import com.twidere.twiderex.utils.fromJson
import com.twidere.twiderex.utils.json
import java.util.UUID

internal fun DbStatusV2.toUi(
    user: UiUser,
    media: List<UiMedia>,
    url: List<UiUrlEntity>,
    reaction: DbStatusReaction?,
    isGap: Boolean,
    referenceStatus: Map<ReferenceType, UiStatus> = emptyMap(),
): UiStatus {
    val extra = try {
        when (platformType) {
            PlatformType.Twitter -> extra.fromJson<DbTwitterStatusExtra>().toUi()
            PlatformType.StatusNet -> TODO()
            PlatformType.Fanfou -> TODO()
            PlatformType.Mastodon -> extra.fromJson<DbMastodonStatusExtra>().toUi()
        }
    } catch (e: Throwable) {
        null
    }
    return UiStatus(
        statusId = statusId,
        htmlText = htmlText,
        timestamp = timestamp,
        metrics = StatusMetrics(
            retweet = retweetCount,
            like = likeCount,
            reply = replyCount,
        ),
        retweeted = reaction?.retweeted ?: false,
        liked = reaction?.liked ?: false,
        geo = UiGeo(
            name = placeString ?: "",
            lat = null,
            long = null
        ),
        hasMedia = hasMedia,
        user = user,
        media = media,
        isGap = isGap,
        source = source,
        url = url,
        statusKey = statusKey,
        rawText = rawText,
        platformType = platformType,
        extra = extra,
        referenceStatus = referenceStatus,
        card = previewCard?.toUi(),
        poll = poll?.toUi(),
        inReplyToStatusId = inReplyToStatusId,
        inReplyToUserId = inReplyToStatusId,
        sensitive = is_possibly_sensitive,
        spoilerText = spoilerText
    )
}

internal fun DbStatusWithMediaAndUser.toUi(
    accountKey: MicroBlogKey,
    isGap: Boolean = false
): UiStatus {
    val reaction = reactions.firstOrNull { it.accountKey == accountKey }
    return data.toUi(
        user = user.toUi(),
        media = media.toUi(),
        url = url.toUi(),
        isGap = isGap,
        reaction = reaction
    )
}

internal fun DbStatusWithReference.toUi(
    accountKey: MicroBlogKey,
    isGap: Boolean = false
) = with(status) {
    val reaction = reactions.firstOrNull { it.accountKey == accountKey }
    data.toUi(
        user = user.toUi(),
        media = media.toUi(),
        isGap = isGap,
        url = url.toUi(),
        reaction = reaction,
        referenceStatus = references.map {
            it.reference.referenceType to it.status.toUi(
                accountKey = accountKey,
                isGap = isGap,
            )
        }.toMap()
    )
}

internal fun UiStatus.toDbStatusWithReference(accountKey: MicroBlogKey) = DbStatusWithReference(
    status = toDbStatusWithMediaAndUser(accountKey),
    references = referenceStatus.map { entry ->
        DbStatusReferenceWithStatus(
            status = entry.value.toDbStatusWithMediaAndUser(accountKey),
            reference = DbStatusReference(
                _id = UUID.randomUUID().toString(),
                referenceType = entry.key,
                statusKey = statusKey,
                referenceStatusKey = entry.value.statusKey
            )
        )
    }
)

internal fun UiStatus.toDbStatusWithMediaAndUser(accountKey: MicroBlogKey) = DbStatusWithMediaAndUser(
    data = toDbStatusV2(),
    media = media.toDbMedia(),
    user = user.toDbUser(),
    reactions = listOf(
        DbStatusReaction(
            _id = UUID.randomUUID().toString(),
            statusKey = statusKey,
            accountKey = accountKey,
            liked = liked,
            retweeted = retweeted
        )
    ),
    url = url.toDbUrl(statusKey)
)
internal fun UiStatus.toDbStatusV2() = DbStatusV2(
    _id = UUID.randomUUID().toString(),
    statusId = statusId,
    htmlText = htmlText,
    timestamp = timestamp,
    hasMedia = hasMedia,
    statusKey = statusKey,
    rawText = rawText,
    retweetCount = metrics.retweet,
    likeCount = metrics.like,
    replyCount = metrics.reply,
    placeString = geo.name,
    source = source,
    userKey = user.userKey,
    lang = "",
    is_possibly_sensitive = sensitive,
    platformType = platformType,
    previewCard = card?.toDbCard(),
    poll = poll?.toDbPoll(),
    spoilerText = spoilerText,
    inReplyToUserId = inReplyToUserId,
    inReplyToStatusId = inReplyToStatusId,
    extra = when (extra) {
        is TwitterStatusExtra -> DbTwitterStatusExtra(
            reply_settings = extra.reply_settings,
            quoteCount = extra.quoteCount
        ).json()
        is MastodonStatusExtra -> DbMastodonStatusExtra(
            emoji = extra.emoji.map { it.emoji }.flatten().map {
                Emoji(
                    shortcode = it.shortcode,
                    url = it.url,
                    staticURL = it.staticURL,
                    visibleInPicker = it.visibleInPicker,
                    category = it.category
                )
            },
            type = extra.type,
            visibility = extra.visibility,
            mentions = extra.mentions?.map {
                Mention(
                    id = it.id,
                    username = it.username,
                    url = it.url,
                    acct = it.acct
                )
            }
        ).json()
        else -> extra.json()
    }
)

private fun UiCard.toDbCard() = DbPreviewCard(
    link = link,
    displayLink = displayLink,
    title = title,
    desc = description,
    image = image
)

private fun UiPoll.toDbPoll() = DbPoll(
    id = id,
    options = options.map { DbPollOption(text = it.text, count = it.count) },
    expiresAt = expiresAt,
    expired = expired,
    multiple = multiple,
    voted = voted,
    votesCount = votesCount,
    votersCount = votersCount,
    ownVotes = ownVotes
)

private fun DbPoll.toUi() = UiPoll(
    id = id,
    options = options.map { Option(text = it.text, count = it.count) },
    expiresAt = expiresAt,
    expired = expired,
    multiple = multiple,
    voted = voted,
    votesCount = votesCount,
    votersCount = votersCount,
    ownVotes = ownVotes
)

internal fun DbPagingTimelineWithStatus.toPagingTimeline(
    accountKey: MicroBlogKey
) = PagingTimeLineWithStatus(
    timeline = timeline.toUi(),
    status = status.toUi(accountKey = accountKey, isGap = timeline.isGap)
)

internal fun DbPagingTimeline.toUi() = PagingTimeLine(
    accountKey = accountKey,
    pagingKey = pagingKey,
    statusKey = statusKey,
    timestamp = timestamp,
    sortId = sortId,
    isGap = isGap
)

internal fun PagingTimeLine.toDbPagingTimeline() = DbPagingTimeline(
    accountKey = accountKey,
    pagingKey = pagingKey,
    statusKey = statusKey,
    timestamp = timestamp,
    sortId = sortId,
    isGap = isGap,
    _id = UUID.randomUUID().toString()
)

internal fun DbPagingTimelineWithStatus.toUi(
    accountKey: MicroBlogKey,
) = with(status.status) {
    val reaction = reactions.firstOrNull { it.accountKey == accountKey }
    data.toUi(
        user = user.toUi(),
        media = media.toUi(),
        isGap = timeline.isGap,
        url = url.toUi(),
        reaction = reaction,
        referenceStatus = status.references.map {
            it.reference.referenceType to it.status.toUi(
                accountKey = accountKey
            )
        }.toMap()
    )
}

internal fun DbTwitterStatusExtra.toUi() = TwitterStatusExtra(
    reply_settings = reply_settings,
    quoteCount = quoteCount
)

internal fun DbMastodonStatusExtra.toUi() = MastodonStatusExtra(
    type = type,
    emoji = emoji.toUi(),
    visibility = visibility,
    mentions = mentions?.toUi()
)

internal fun List<Mention>.toUi() = map {
    MastodonMention(
        id = it.id,
        username = it.username,
        url = it.url,
        acct = it.acct
    )
}

internal fun DbPreviewCard.toUi() = UiCard(
    link = link,
    displayLink = displayLink,
    title = title,
    description = desc,
    image = image
)

internal fun Poll.toUi() = id?.let {
    UiPoll(
        id = it,
        options = options?.map { option ->
            Option(
                text = option.title ?: "",
                count = option.votesCount ?: 0
            )
        } ?: emptyList(),
        expired = expired ?: false,
        expiresAt = expiresAt?.time,
        multiple = multiple ?: false,
        voted = voted ?: false,
        votersCount = votersCount,
        ownVotes = ownVotes,
        votesCount = votesCount
    )
}
