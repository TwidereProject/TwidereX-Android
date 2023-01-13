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

import com.twidere.services.twitter.model.guest.Entry
import com.twidere.services.twitter.model.guest.TweetValue
import com.twidere.services.twitter.model.guest.TwitterGuestResponse
import com.twidere.services.twitter.model.guest.User
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.MediaType
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.enums.ReferenceType
import com.twidere.twiderex.model.enums.TwitterReplySettings
import com.twidere.twiderex.model.paging.PagingTimeLine
import com.twidere.twiderex.model.paging.PagingTimeLineWithStatus
import com.twidere.twiderex.model.ui.StatusMetrics
import com.twidere.twiderex.model.ui.UiCard
import com.twidere.twiderex.model.ui.UiGeo
import com.twidere.twiderex.model.ui.UiMedia
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.model.ui.UiUrlEntity
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.model.ui.UserMetrics
import com.twidere.twiderex.model.ui.twitter.TwitterStatusExtra
import com.twidere.twiderex.model.ui.twitter.TwitterUserExtra
import kotlinx.datetime.Instant

internal fun TwitterGuestResponse.nextCursor(cursorType: String): String? =
  timeline?.instructions?.firstOrNull { it.addEntries != null }?.addEntries?.entries
    ?.lastOrNull { it.content?.operation?.cursor?.cursorType == cursorType }?.content?.operation?.cursor?.value

internal fun TwitterGuestResponse.toPagingTimeline(
  pagingKey: String,
  accountKey: MicroBlogKey,
  orderKey: Long?,
): List<PagingTimeLineWithStatus> {
  val result = arrayListOf<PagingTimeLineWithStatus>()
  var order = orderKey
  timeline?.instructions?.firstOrNull { it.addEntries != null }?.addEntries?.entries?.forEach {
    it.toDbTimelineWithStatus(
      this,
      pagingKey = pagingKey,
      accountKey = accountKey,
      orderKey = if (order == null) null else -order,
    ).apply {
      result.addAll(this)
      if (order != null) {
        order += this.size
      }
    }
  }
  return result
}

internal fun Entry.toDbTimelineWithStatus(
  response: TwitterGuestResponse,
  pagingKey: String,
  accountKey: MicroBlogKey,
  orderKey: Long?,
): List<PagingTimeLineWithStatus> {
  if (/*content?.item?.content?.tweet?.displayType == "Tweet" && */!content?.item?.content?.tweet?.id.isNullOrEmpty()) {
    return listOfNotNull(
      response.globalObjects
        ?.tweets
        ?.get(content?.item?.content?.tweet?.id)
        ?.toDbTimelineWithStatus(response, pagingKey, accountKey, orderKey)
    )
  }
  if (!content?.item?.content?.conversationThread?.conversationComponents.isNullOrEmpty()) {
    return content?.item?.content?.conversationThread?.conversationComponents?.mapIndexedNotNull { index, conversationComponent ->
      if (/*conversationComponent.conversationTweetComponent?.tweet?.displayType == "Tweet" && */!conversationComponent.conversationTweetComponent?.tweet?.id.isNullOrEmpty()) {
        response.globalObjects
          ?.tweets
          ?.get(conversationComponent.conversationTweetComponent?.tweet?.id)
          ?.toDbTimelineWithStatus(response, pagingKey, accountKey, if (orderKey != null) orderKey - index else null)
      } else {
        null
      }
    } ?: emptyList()
  }
  return emptyList()
}

private fun TweetValue.toDbTimelineWithStatus(
  response: TwitterGuestResponse,
  pagingKey: String,
  accountKey: MicroBlogKey,
  orderKey: Long?,
  isGap: Boolean = false,
): PagingTimeLineWithStatus? {
  val statusKey = MicroBlogKey.twitter(idStr.orEmpty())
  // val user = response.globalObjects?.users?.get(userIDStr)?.toDbUser() ?: return null
  val status = toUiStatus(response, isGap) ?: return null
  return PagingTimeLineWithStatus(
    timeline = PagingTimeLine(
      statusKey = statusKey,
      pagingKey = pagingKey,
      accountKey = accountKey,
      timestamp = createdAt?.toEpochMilliseconds() ?: Instant.DISTANT_FUTURE.toEpochMilliseconds(),
      sortId = orderKey ?: createdAt?.toEpochMilliseconds() ?: Instant.DISTANT_FUTURE.toEpochMilliseconds(),
      isGap = isGap,
    ),
    status = status,
    // user = DbUserWithRelation(
    //   user = user,
    // ),
    // status = DbStatusWithReactions(
    //   status = DbStatus(
    //     statusKey = statusKey,
    //     userKey = user.userKey,
    //     content = toDbTwitter(response) ?: return null
    //   ),
    //   reactions = DbStatusReactions(
    //     statusKey = statusKey,
    //     accountKey = accountKey,
    //     liked = 0, // TODO USE REAL VALUE
    //     retweeted = 0, // TODO USE REAL VALUE
    //   ),
    // )
  )
}

private fun TweetValue.toUiStatus(
  response: TwitterGuestResponse,
  isGap: Boolean = false,
): UiStatus? {
  val quote = response.globalObjects?.tweets?.get(quotedStatusIDStr)?.toUiStatus(response)
  val retweet = response.globalObjects?.tweets?.get(retweetedStatusIDStr)?.toUiStatus(response)
  val reply = response.globalObjects?.tweets?.get(inReplyToStatusIDStr)?.toUiStatus(response)
  val user = response.globalObjects?.users?.get(userIDStr)?.toUiUser() ?: return null
  val statusKey = MicroBlogKey.twitter(
    idStr ?: throw IllegalArgumentException("Status.idStr should not be null")
  )
  return UiStatus(
    statusId = idStr ?: throw IllegalArgumentException("Status.idStr should not be null"),
    sensitive = possiblySensitive ?: false,
    rawText = fullText ?: text ?: "",
    htmlText = autolink.autoLink(fullText ?: text ?: ""),
    timestamp = createdAt?.toEpochMilliseconds() ?: 0,
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
    card = card?.let {
      UiCard(
        link = it.bindingValues?.cardUrl?.stringValue.orEmpty(),
        title = it.bindingValues?.title?.stringValue.orEmpty(),
        description = it.bindingValues?.description?.stringValue,
        image = it.bindingValues?.thumbnailImage?.imageValue?.url,
        displayLink = "",
      )
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
      reply?.let { this[ReferenceType.Reply] = it }
    },
    language = lang,
  )
}

private fun User.toUiUser(): UiUser {
  return UiUser(
    id = idStr ?: throw IllegalArgumentException("user.idStr should not be null"),
    name = name.orEmpty(),
    screenName = screenName.orEmpty(),
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
