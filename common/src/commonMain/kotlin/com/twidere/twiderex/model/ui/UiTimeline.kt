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

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.LayoutDirection
import com.twidere.twiderex.component.status.ResolvedLink
import com.twidere.twiderex.extensions.humanizedCount
import com.twidere.twiderex.extensions.humanizedTimestamp
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.MastodonNotificationType
import com.twidere.twiderex.model.enums.MastodonVisibility
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.enums.TwitterReplySettings
import com.twidere.twiderex.model.ui.mastodon.MastodonMention
import java.text.Bidi
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import moe.tlaster.twitter.parser.Token
import moe.tlaster.twitter.parser.TwitterParser

sealed interface UiTimeline {
  val key: String
  val statusKey: MicroBlogKey
  val contentType: String
}

@Immutable
data class UiGap(
  val maxId: String,
  val sinceId: String,
  val loading: Boolean,
) : UiTimeline {
  override val contentType: String = "gap"
  override val key = "$maxId-$sinceId"
  override val statusKey: MicroBlogKey = MicroBlogKey.gap(maxId, sinceId)
}

private val parser = TwitterParser()

sealed interface UiStatusTimeline : UiTimeline {
  val data: UiStatusMetaData
}

data class UiStatusMetaData(
  val statusKey: MicroBlogKey,
  val metrics: UiStatusMetrics,
  val user: UiUser,
  val content: String,
  val platformType: PlatformType,
  val source: String,
  val geo: UiGeo?,
  val url: ImmutableList<UiUrlEntity>,
  val menu: UiStatusTimelineMenu,
  val owned: Boolean,
  val sensitive: Boolean,
  val timestamp: Long,
) {
  val humanizedTime = timestamp.humanizedTimestamp()
  val parsedContent: ImmutableList<Token> = parser.parse(content).toPersistentList()
  val contentDirection = if (Bidi(content, Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT).baseIsLeftToRight()) {
    LayoutDirection.Ltr
  } else {
    LayoutDirection.Rtl
  }

  fun resolveLink(
    href: String,
  ): ResolvedLink {
    val entity = url.firstOrNull { it.url == href }
    return when {
      entity != null -> {
        ResolvedLink(
          expanded = entity.expandedUrl,
          display = entity.displayUrl,
        )
      }
      else -> {
        ResolvedLink(expanded = null)
      }
    }
  }
}

data class UiStatusTimelineMenu(
  val retweetOpened: Boolean,
  val moreOpened: Boolean,
)

sealed interface UiStatusWithExtra : UiTimeline {
  val status: UiStatusTimeline
}

@Immutable
data class UiTwitterStatus(
  override val statusKey: MicroBlogKey,
  override val data: UiStatusMetaData,
  val replySettings: TwitterReplySettings,
) : UiTimeline, UiStatusTimeline {
  override val key = data.statusKey.toString()
  override val contentType = "twitter-status"
}

@Immutable
data class UiMastodonStatus(
  override val statusKey: MicroBlogKey,
  override val data: UiStatusMetaData,
  val expanded: Boolean,
  val spoilerText: String?,
  val notificationType: MastodonNotificationType?,
  val emoji: ImmutableList<UiEmoji>,
  val visibility: MastodonVisibility,
  val mentions: ImmutableList<MastodonMention>?,
) : UiTimeline, UiStatusTimeline {
  override val key = data.statusKey.toString()
  override val contentType = "mastodon-status"
  val parsedSpoilerText: ImmutableList<Token> = parser.parse(spoilerText ?: "").toPersistentList()
}

@Immutable
data class UiTwitterThreadStatus(
  override val statusKey: MicroBlogKey,
  val status: UiTwitterStatus,
) : UiTimeline {
  override val key: String = status.key
  override val contentType: String = "twitter-thread-status-${status.contentType}"
}

@Immutable
data class UiStatusMetrics(
  val retweetCount: Long,
  val likeCount: Long,
  val replyCount: Long,
  val quoteCount: Long,
  val liked: Boolean,
  val retweeted: Boolean,
) {
  val humanizedRetweetCount = retweetCount.humanizedCount()
  val humanizedLikeCount = likeCount.humanizedCount()
  val humanizedReplyCount = replyCount.humanizedCount()
  val humanizedQuoteCount = quoteCount.humanizedCount()
  val hasRetweetCount = retweetCount > 0
  val hasLikeCount = likeCount > 0
  val hasReplyCount = replyCount > 0
  val hasQuoteCount = quoteCount > 0
}

@Immutable
data class UiStatusWithPoll(
  override val statusKey: MicroBlogKey,
  override val status: UiStatusTimeline,
  val poll: UiPoll,
) : UiStatusWithExtra {
  override val key = status.key
  override val contentType: String = "${status.contentType}-poll"
}

@Immutable
data class UiStatusWithMedia(
  override val statusKey: MicroBlogKey,
  override val status: UiStatusTimeline,
  val media: ImmutableList<UiMedia>,
) : UiStatusWithExtra {
  override val key = status.key
  override val contentType: String = "${status.contentType}-media"
}

@Immutable
data class UiStatusWithCard(
  override val statusKey: MicroBlogKey,
  override val status: UiStatusTimeline,
  val card: UiCard,
) : UiStatusWithExtra {
  override val key = status.key
  override val contentType: String = "${status.contentType}-card"
}

@Immutable
data class UiRetweetStatus(
  override val statusKey: MicroBlogKey,
  val status: UiStatusWithExtra,
  val retweet: UiStatusWithExtra,
) : UiTimeline {
  override val key = status.key
  override val contentType: String = "retweet-status-${retweet.contentType}"
}

@Immutable
data class UiQuoteStatus(
  override val statusKey: MicroBlogKey,
  val status: UiStatusWithExtra,
  val quote: UiStatusWithExtra,
) : UiTimeline {
  override val key = status.key
  override val contentType: String = "status-quote-${quote.contentType}"
}

@Immutable
data class UiRetweetAndQuoteStatus(
  override val statusKey: MicroBlogKey,
  val retweet: UiStatusWithExtra,
  val status: UiQuoteStatus,
) : UiTimeline {
  override val key = status.key
  override val contentType: String = "status-retweet-quote-${status.contentType}"
}

@Immutable
data class UiFollow(
  override val statusKey: MicroBlogKey,
  val user: UiUser,
) : UiTimeline {
  override val key = user.userKey.copy(id = user.userKey.id + "-follow").toString()
  override val contentType: String = "follow"
}

@Immutable
data class UiFollowRequest(
  override val statusKey: MicroBlogKey,
  val user: UiUser,
) : UiTimeline {
  override val key = user.userKey.copy(id = user.userKey.id + "-follow-request").toString()
  override val contentType: String = "follow-request"
}
