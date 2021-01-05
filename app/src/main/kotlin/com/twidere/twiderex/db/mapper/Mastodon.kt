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
import com.twidere.services.mastodon.model.Status
import com.twidere.twiderex.db.model.DbMedia
import com.twidere.twiderex.db.model.DbStatusReaction
import com.twidere.twiderex.db.model.DbStatusV2
import com.twidere.twiderex.db.model.DbStatusWithMediaAndUser
import com.twidere.twiderex.db.model.DbStatusWithReference
import com.twidere.twiderex.db.model.DbTimeline
import com.twidere.twiderex.db.model.DbTimelineWithStatus
import com.twidere.twiderex.db.model.DbUrlEntity
import com.twidere.twiderex.db.model.DbUser
import com.twidere.twiderex.db.model.TimelineType
import com.twidere.twiderex.model.MediaType
import com.twidere.twiderex.model.MicroBlogKey
import java.util.UUID

fun Status.toDbTimeline(
    accountKey: MicroBlogKey,
    timelineType: TimelineType,
): DbTimelineWithStatus {
    val status = this.toDbStatusWithMediaAndUser(accountKey)
    val retweet = this.reblog?.toDbStatusWithMediaAndUser(
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
            replyTo = null,
            quote = null,
            retweet = retweet,
            status = status
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
        htmlText = content ?: "",
        timestamp = createdAt?.time ?: 0,
        retweetCount = reblogsCount ?: 0,
        likeCount = favouritesCount ?: 0,
        replyCount = repliesCount ?: 0,
        placeString = "",
        hasMedia = !mediaAttachments.isNullOrEmpty(),
        source = application?.name ?: "",
        userKey = user.userKey,
        lang = null,
        replyStatusId = null,
        retweetStatusId = reblog?.id,
        quoteStatusId = null,
        statusKey = MicroBlogKey(
            id ?: throw IllegalArgumentException("mastodon Status.idStr should not be null"),
            host = user.userKey.host,
        ),
        is_possibly_sensitive = sensitive ?: false
    )
    return DbStatusWithMediaAndUser(
        data = status,
        media = (mediaAttachments ?: emptyList()).mapIndexed { index, it ->
            DbMedia(
                _id = UUID.randomUUID().toString(),
                statusKey = status.statusKey,
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
                        com.twidere.services.mastodon.model.MediaType.unknown -> MediaType.photo
                        com.twidere.services.mastodon.model.MediaType.gifv -> MediaType.video
                        com.twidere.services.mastodon.model.MediaType.video -> MediaType.video
                        com.twidere.services.mastodon.model.MediaType.audio -> MediaType.photo
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
        url = card?.let {
            listOf(
                DbUrlEntity(
                    _id = UUID.randomUUID().toString(),
                    statusKey = status.statusKey,
                    url = it.url ?: "",
                    expandedUrl = it.url ?: "",
                    displayUrl = it.url ?: "",
                    title = it.title,
                    description = it.description,
                    image = it.image
                )
            )
        } ?: emptyList()
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
            acct?.let { MicroBlogKey.valueOf(it) }?.host ?: accountKey.host,
        ),
        profileImage = avatar ?: avatarStatic ?: "",
        profileBackgroundImage = header ?: headerStatic ?: "",
        followersCount = followersCount ?: 0,
        friendsCount = followingCount ?: 0,
        listedCount = 0,
        desc = note ?: "",
        website = null,
        location = null,
        verified = false,
        isProtected = false,
    )
}
