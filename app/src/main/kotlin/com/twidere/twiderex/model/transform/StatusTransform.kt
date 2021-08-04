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
package com.twidere.twiderex.model.transform

import com.twidere.services.mastodon.model.Mention
import com.twidere.services.mastodon.model.Poll
import com.twidere.twiderex.db.model.DbMastodonStatusExtra
import com.twidere.twiderex.db.model.DbPagingTimelineWithStatus
import com.twidere.twiderex.db.model.DbPreviewCard
import com.twidere.twiderex.db.model.DbStatusReaction
import com.twidere.twiderex.db.model.DbStatusV2
import com.twidere.twiderex.db.model.DbStatusWithMediaAndUser
import com.twidere.twiderex.db.model.DbStatusWithReference
import com.twidere.twiderex.db.model.DbTwitterStatusExtra
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.enums.ReferenceType
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
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

fun DbStatusV2.toUi(
    user: UiUser,
    media: List<UiMedia>,
    url: List<UiUrlEntity>,
    reaction: DbStatusReaction?,
    isGap: Boolean,
    referenceStatus: Map<ReferenceType, UiStatus> = emptyMap(),
): UiStatus {
    var poll: UiPoll? = null
    var sensitive = false
    var spoilerText: String? = null
    val extra = when (platformType) {
        PlatformType.Twitter -> Json.decodeFromString<DbTwitterStatusExtra>(extra).toUi()
        PlatformType.StatusNet -> TODO()
        PlatformType.Fanfou -> TODO()
        PlatformType.Mastodon -> Json.decodeFromString<DbMastodonStatusExtra>(extra).apply {
            poll = this.poll?.toUi()
            sensitive = this.sensitive
            spoilerText = this.spoilerText
        }.toUi()
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
        poll = poll,
        inReplyToStatusId = inReplyToStatusId,
        inReplyToUserId = inReplyToStatusId,
        sensitive = sensitive,
        spoilerText = spoilerText
    )
}

fun DbStatusWithMediaAndUser.toUi(
    accountKey: MicroBlogKey,
): UiStatus {
    val reaction = reactions.firstOrNull { it.accountKey == accountKey }
    return data.toUi(
        user = user.toUi(),
        media = media.toUi(),
        url = url.toUi(),
        isGap = false,
        reaction = reaction
    )
}

fun DbStatusWithReference.toUi(
    accountKey: MicroBlogKey,
) = with(status) {
    val reaction = reactions.firstOrNull { it.accountKey == accountKey }
    data.toUi(
        user = user.toUi(),
        media = media.toUi(),
        isGap = false,
        url = url.toUi(),
        reaction = reaction,
        referenceStatus = references.map {
            it.reference.referenceType to it.status.toUi(
                accountKey = accountKey
            )
        }.toMap()
    )
}

fun DbPagingTimelineWithStatus.toUi(
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

fun DbTwitterStatusExtra.toUi() = TwitterStatusExtra(
    reply_settings = reply_settings,
    quoteCount = quoteCount
)

fun DbMastodonStatusExtra.toUi() = MastodonStatusExtra(
    type = type,
    emoji = emoji.toUi(),
    visibility = visibility,
    mentions = mentions?.toUi()
)

fun List<Mention>.toUi() = map {
    MastodonMention(
        id = it.id,
        username = it.username,
        url = it.url,
        acct = it.acct
    )
}

fun DbPreviewCard.toUi() = UiCard(
    link = link,
    displayLink = displayLink,
    title = title,
    description = desc,
    image = image
)

fun Poll.toUi() = id?.let {
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
