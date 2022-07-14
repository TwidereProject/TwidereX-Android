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
package com.twidere.twiderex.db.sqldelight.transform

import com.twidere.twiderex.db.sqldelight.model.DbCard
import com.twidere.twiderex.db.sqldelight.model.DbGeo
import com.twidere.twiderex.db.sqldelight.model.DbOption
import com.twidere.twiderex.db.sqldelight.model.DbPoll
import com.twidere.twiderex.db.sqldelight.model.DbStatusMetrics
import com.twidere.twiderex.db.sqldelight.model.DbStatusReference
import com.twidere.twiderex.db.sqldelight.model.DbStatusReferenceList
import com.twidere.twiderex.db.sqldelight.model.DbStatusWithAttachments
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.ui.Option
import com.twidere.twiderex.model.ui.StatusMetrics
import com.twidere.twiderex.model.ui.UiCard
import com.twidere.twiderex.model.ui.UiGeo
import com.twidere.twiderex.model.ui.UiPoll
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.model.ui.mastodon.MastodonStatusExtra
import com.twidere.twiderex.model.ui.twitter.TwitterStatusExtra
import com.twidere.twiderex.sqldelight.table.DbStatus
import com.twidere.twiderex.sqldelight.table.DbStatusReactions
import com.twidere.twiderex.utils.fromJson
import com.twidere.twiderex.utils.json

internal fun UiStatus.toDbStatusWithAttachments(accountKey: MicroBlogKey): DbStatusWithAttachments = DbStatusWithAttachments(
    status = DbStatus(
        statusId = statusId,
        statusKey = statusKey,
        userKey = user.userKey,
        htmlText = htmlText,
        rawText = rawText,
        timestamp = timestamp,
        metrics = metrics.toDbStatusMetrics(),
        sensitive = sensitive,
        geo = geo.toDbGeo(),
        hasMedia = hasMedia,
        source = source,
        isGap = isGap,
        platformType = platformType,
        spoilerText = spoilerText,
        card = card?.toDbCard(),
        poll = poll?.toDbPoll(),
        inReplyToStatusId = inReplyToStatusId,
        inReplyToUserId = inReplyToUserId,
        refrenceStatus = DbStatusReferenceList(referenceStatus.map { DbStatusReference(it.key, it.value.statusKey) }),
        extra = when (extra) {
            is TwitterStatusExtra -> extra.json()
            is MastodonStatusExtra -> extra.json()
            else -> extra.toString()
        },
    ),
    reactions = DbStatusReactions(
        liked = liked,
        retweeted = retweeted,
        statusKey = statusKey,
        accountKey = accountKey
    ),
    user = user.toDbUser(),
    medias = media.map { it.toDbMedia() },
    urls = url.map { it.toDbUrlEntity(statusKey) },
    references = referenceStatus.mapValues { it.value.toDbStatusWithAttachments(accountKey) }
)

internal fun DbStatusWithAttachments.toUi(): UiStatus = UiStatus(
    statusId = status.statusId,
    statusKey = status.statusKey,
    htmlText = status.htmlText,
    rawText = status.rawText,
    timestamp = status.timestamp,
    metrics = status.metrics.toUi(),
    sensitive = status.sensitive,
    liked = reactions.liked,
    retweeted = reactions.retweeted,
    geo = status.geo.toUi(),
    hasMedia = status.hasMedia,
    source = status.source,
    isGap = status.isGap,
    platformType = status.platformType,
    spoilerText = status.spoilerText,
    card = status.card?.toUi(),
    poll = status.poll?.toUi(),
    inReplyToStatusId = status.inReplyToStatusId,
    inReplyToUserId = status.inReplyToUserId,
    extra = when (status.platformType) {
        PlatformType.Twitter -> status.extra?.fromJson<TwitterStatusExtra>()
        PlatformType.StatusNet -> TODO()
        PlatformType.Fanfou -> TODO()
        PlatformType.Mastodon -> status.extra?.fromJson<MastodonStatusExtra>()
    },
    referenceStatus = references.mapValues { it.value.toUi() },
    user = user.toUi(),
    media = medias.map { it.toUi() },
    url = urls.map { it.toUi() }
)

private fun UiPoll.toDbPoll() = DbPoll(
    id = id,
    options = options.map { DbOption(text = it.text, count = it.count) },
    expiresAt = expiresAt,
    multiple = multiple,
    expired = expired,
    voted = voted,
    votersCount = votersCount,
    votesCount = votesCount,
    ownVotes = ownVotes
)

private fun DbPoll.toUi() = UiPoll(
    id = id,
    options = options.map { Option(text = it.text, count = it.count) },
    expiresAt = expiresAt,
    multiple = multiple,
    expired = expired,
    voted = voted,
    votersCount = votersCount,
    votesCount = votesCount,
    ownVotes = ownVotes
)

private fun UiCard.toDbCard() = DbCard(
    link = link,
    displayLink = displayLink,
    title = title,
    description = description,
    image = image
)

private fun DbCard.toUi() = UiCard(
    link = link,
    displayLink = displayLink,
    title = title,
    description = description,
    image = image
)

private fun UiGeo.toDbGeo() = DbGeo(
    name = name,
    lat = lat,
    long = long
)

private fun DbGeo.toUi() = UiGeo(
    name = name,
    lat = lat,
    long = long
)

private fun StatusMetrics.toDbStatusMetrics() = DbStatusMetrics(
    like = like,
    reply = reply,
    retweet = retweet
)

private fun DbStatusMetrics.toUi() = StatusMetrics(
    like = like,
    reply = reply,
    retweet = retweet
)
