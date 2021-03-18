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
package com.twidere.twiderex.model.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.res.stringResource
import com.twidere.twiderex.R
import com.twidere.twiderex.db.model.DbMastodonStatusExtra
import com.twidere.twiderex.db.model.DbPagingTimelineWithStatus
import com.twidere.twiderex.db.model.DbStatusWithMediaAndUser
import com.twidere.twiderex.db.model.DbStatusWithReference
import com.twidere.twiderex.db.model.DbTimelineWithStatus
import com.twidere.twiderex.db.model.ReferenceType
import com.twidere.twiderex.model.MastodonStatusType
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.PlatformType
import com.twidere.twiderex.model.ui.UiMedia.Companion.toUi
import com.twidere.twiderex.model.ui.UiUrlEntity.Companion.toUi
import com.twidere.twiderex.model.ui.UiUser.Companion.toUi

@Immutable
data class UiStatus(
    val statusId: String,
    val statusKey: MicroBlogKey,
    val htmlText: String,
    val rawText: String,
    val timestamp: Long,
    val retweetCount: Long,
    val likeCount: Long,
    val replyCount: Long,
    val retweeted: Boolean,
    val liked: Boolean,
    val placeString: String?,
    val hasMedia: Boolean,
    val user: UiUser,
    val media: List<UiMedia>,
    val source: String,
    val isGap: Boolean,
    val url: List<UiUrlEntity>,
    val platformType: PlatformType,
    val mastodonExtra: DbMastodonStatusExtra? = null,
    val referenceStatus: Map<ReferenceType, UiStatus> = emptyMap(),
) {
    val retweet: UiStatus? by lazy {
        if (platformType == PlatformType.Mastodon && mastodonExtra != null && mastodonExtra.type != MastodonStatusType.Status) {
            referenceStatus[ReferenceType.MastodonNotification]
        } else {
            referenceStatus[ReferenceType.Retweet]?.copy(
                referenceStatus = referenceStatus.filterNot { it.key == ReferenceType.Retweet }
            )
        }
    }

    val quote: UiStatus? by lazy {
        referenceStatus[ReferenceType.Quote]
    }

    fun generateShareLink() = "https://${statusKey.host}" + when (platformType) {
        PlatformType.Twitter -> "/${user.screenName}/status/$statusId"
        PlatformType.StatusNet -> TODO()
        PlatformType.Fanfou -> TODO()
        PlatformType.Mastodon -> "/web/statuses/$statusId"
    }

    companion object {
        @Composable
        fun sample() = UiStatus(
            statusId = "",
            htmlText = stringResource(id = R.string.scene_settings_display_preview_thank_for_using_twidere_x),
            timestamp = System.currentTimeMillis(),
            retweetCount = 1200,
            likeCount = 123,
            replyCount = 1100,
            retweeted = false,
            liked = false,
            placeString = null,
            hasMedia = true,
            user = UiUser.sample(),
            media = UiMedia.sample(),
            source = "TwidereX",
            isGap = false,
            url = emptyList(),
            statusKey = MicroBlogKey.Empty,
            rawText = "",
            platformType = PlatformType.Twitter,
        )

        @Composable
        fun placeHolder() = UiStatus(
            statusId = "",
            htmlText = "",
            timestamp = System.currentTimeMillis(),
            retweetCount = 0,
            likeCount = 0,
            replyCount = 0,
            retweeted = false,
            liked = false,
            placeString = null,
            hasMedia = true,
            user = UiUser.placeHolder(),
            media = emptyList(),
            source = "",
            isGap = false,
            url = emptyList(),
            statusKey = MicroBlogKey.Empty,
            rawText = "",
            platformType = PlatformType.Twitter,
        )

        fun DbTimelineWithStatus.toUi(
            accountKey: MicroBlogKey,
        ) = with(status.status) {
            val reaction = reactions.firstOrNull { it.accountKey == accountKey }
            UiStatus(
                statusId = data.statusId,
                htmlText = data.htmlText,
                timestamp = data.timestamp,
                retweetCount = data.retweetCount,
                likeCount = data.likeCount,
                replyCount = data.replyCount,
                retweeted = reaction?.retweeted ?: false,
                liked = reaction?.liked ?: false,
                placeString = data.placeString,
                hasMedia = data.hasMedia,
                user = user.toUi(),
                media = media.toUi(),
                isGap = timeline.isGap,
                source = data.source,
                url = url.toUi(),
                statusKey = data.statusKey,
                rawText = data.rawText,
                platformType = data.platformType,
                mastodonExtra = data.mastodonExtra,
                referenceStatus = status.references.map {
                    it.reference.referenceType to it.status.toUi(
                        accountKey = accountKey
                    )
                }.toMap()
            )
        }

        fun DbStatusWithMediaAndUser.toUi(
            accountKey: MicroBlogKey,
        ): UiStatus {
            val reaction = reactions.firstOrNull { it.accountKey == accountKey }
            return UiStatus(
                statusId = data.statusId,
                htmlText = data.htmlText,
                timestamp = data.timestamp,
                retweetCount = data.retweetCount,
                likeCount = data.likeCount,
                replyCount = data.replyCount,
                retweeted = reaction?.retweeted ?: false,
                liked = reaction?.liked ?: false,
                placeString = data.placeString,
                hasMedia = data.hasMedia,
                user = user.toUi(),
                media = media.toUi(),
                isGap = false,
                source = data.source,
                url = url.toUi(),
                statusKey = data.statusKey,
                rawText = data.rawText,
                platformType = data.platformType,
                mastodonExtra = data.mastodonExtra,
            )
        }

        fun DbStatusWithReference.toUi(
            accountKey: MicroBlogKey,
        ) = with(status) {
            val reaction = reactions.firstOrNull { it.accountKey == accountKey }
            UiStatus(
                statusId = data.statusId,
                htmlText = data.htmlText,
                timestamp = data.timestamp,
                retweetCount = data.retweetCount,
                likeCount = data.likeCount,
                replyCount = data.replyCount,
                retweeted = reaction?.retweeted ?: false,
                liked = reaction?.liked ?: false,
                placeString = data.placeString,
                hasMedia = data.hasMedia,
                user = user.toUi(),
                media = media.toUi(),
                isGap = false,
                source = data.source,
                url = url.toUi(),
                statusKey = data.statusKey,
                rawText = data.rawText,
                platformType = data.platformType,
                mastodonExtra = data.mastodonExtra,
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
            UiStatus(
                statusId = data.statusId,
                htmlText = data.htmlText,
                timestamp = data.timestamp,
                retweetCount = data.retweetCount,
                likeCount = data.likeCount,
                replyCount = data.replyCount,
                retweeted = reaction?.retweeted ?: false,
                liked = reaction?.liked ?: false,
                placeString = data.placeString,
                hasMedia = data.hasMedia,
                user = user.toUi(),
                media = media.toUi(),
                isGap = timeline.isGap,
                source = data.source,
                url = url.toUi(),
                statusKey = data.statusKey,
                rawText = data.rawText,
                platformType = data.platformType,
                mastodonExtra = data.mastodonExtra,
                referenceStatus = status.references.map {
                    it.reference.referenceType to it.status.toUi(
                        accountKey = accountKey
                    )
                }.toMap()
            )
        }
    }
}
