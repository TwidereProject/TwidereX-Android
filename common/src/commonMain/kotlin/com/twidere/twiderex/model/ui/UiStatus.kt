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
package com.twidere.twiderex.model.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.dataprovider.mapper.autolink
import com.twidere.twiderex.extensions.humanizedCount
import com.twidere.twiderex.extensions.humanizedTimestamp
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.MastodonStatusType
import com.twidere.twiderex.model.enums.MediaType
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.enums.ReferenceType
import com.twidere.twiderex.model.ui.mastodon.MastodonStatusExtra
import com.twidere.twiderex.model.ui.twitter.TwitterStatusExtra
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableMap
import org.jsoup.Jsoup
import java.text.Bidi

@Immutable
data class UiStatus(
  val statusId: String,
  val statusKey: MicroBlogKey,
  val htmlText: String,
  val rawText: String,
  val timestamp: Long,
  val metrics: StatusMetrics,
  val sensitive: Boolean,
  val retweeted: Boolean,
  val liked: Boolean,
  val geo: UiGeo,
  val hasMedia: Boolean,
  val user: UiUser,
  val media: ImmutableList<UiMedia>,
  val source: String,
  val isGap: Boolean,
  val url: ImmutableList<UiUrlEntity>,
  val platformType: PlatformType,
  val spoilerText: String? = null,
  val card: UiCard? = null,
  val poll: UiPoll? = null,
  val referenceStatus: ImmutableMap<ReferenceType, UiStatus> = persistentMapOf(),
  val inReplyToUserId: String? = null,
  val inReplyToStatusId: String? = null,
  val extra: StatusExtra? = null,
  val language: String? = null,
) {
  val contentHtmlDocument = Jsoup.parse(htmlText.replace("\n", "<br>"))
  val contentIsLeftToRight = Bidi(htmlText, Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT).baseIsLeftToRight()
  val humanizedTime = timestamp.humanizedTimestamp()
  val mastodonExtra: MastodonStatusExtra? = if (extra is MastodonStatusExtra) extra else null
  val isMediaEmptyOfContainsAudio = !media.any() || media.any { it.type == MediaType.audio }

  val twitterExtra: TwitterStatusExtra? = if (extra is TwitterStatusExtra) extra else null

  val retweet: UiStatus? by lazy {
    if (platformType == PlatformType.Mastodon && mastodonExtra != null && mastodonExtra.type != MastodonStatusType.Status) {
      referenceStatus[ReferenceType.MastodonNotification]
    } else {
      referenceStatus[ReferenceType.Retweet]?.copy(
        referenceStatus = referenceStatus.filterNot { it.key == ReferenceType.Retweet }.toImmutableMap()
      )
    }
  }

  val quote: UiStatus? by lazy {
    referenceStatus[ReferenceType.Quote]
  }

  fun isInThread(detailStatusId: String? = null): Boolean {
    return if (detailStatusId == null) {
      // show all reply as thread
      inReplyToStatusId != null
    } else {
      // in detail scene only show thread when reply to other status
      // or reply to self
      inReplyToStatusId != detailStatusId || inReplyToUserId == user.id
    }
  }

  fun generateShareLink() = "https://${statusKey.host}" + when (platformType) {
    PlatformType.Twitter -> (retweet ?: this).let { "/${it.user.screenName}/status/${it.statusId}" }
    PlatformType.StatusNet -> TODO()
    PlatformType.Fanfou -> TODO()
    PlatformType.Mastodon -> "/web/statuses/$statusId"
  }

  val isMastodonFollowStatus: Boolean = platformType == PlatformType.Mastodon &&
    mastodonExtra != null &&
    (
      mastodonExtra.type == MastodonStatusType.NotificationFollowRequest ||
        mastodonExtra.type == MastodonStatusType.NotificationFollow
      )

  val itemType = when {
    isMastodonFollowStatus -> "follow"
    isGap -> "gap"
    else -> "status"
  }

  companion object {
    @Composable
    fun sample() = UiStatus(
      statusId = "",
      htmlText = autolink.autoLink(stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_display_preview_thank_for_using_twidere_x)),
      timestamp = System.currentTimeMillis(),
      metrics = StatusMetrics(
        retweet = 1200,
        like = 123,
        reply = 1100,
      ),
      retweeted = false,
      liked = false,
      geo = UiGeo(""),
      hasMedia = true,
      user = UiUser.sample(),
      media = UiMedia.sample(),
      source = "TwidereX",
      isGap = false,
      url = persistentListOf(),
      statusKey = MicroBlogKey.Empty,
      rawText = "",
      platformType = PlatformType.Twitter,
      sensitive = false
    )
  }
}

@Immutable
interface StatusExtra

@Immutable
data class StatusMetrics(
  val like: Long,
  val reply: Long,
  val retweet: Long
) {
  val likeString = like.humanizedCount()
  val replyString = reply.humanizedCount()
  val retweetString = retweet.humanizedCount()
  val showLike = like > 0
  val showReply = reply > 0
  val showRetweet = retweet > 0
}
