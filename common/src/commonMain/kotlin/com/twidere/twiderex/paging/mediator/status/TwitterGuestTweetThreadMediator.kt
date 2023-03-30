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
package com.twidere.twiderex.paging.mediator.status

import androidx.paging.PagingState
import com.twidere.services.microblog.model.IStatus
import com.twidere.services.twitter.TwitterGuestService
import com.twidere.services.twitter.TwitterService
import com.twidere.services.twitter.model.ReferencedTweetType
import com.twidere.twiderex.dataprovider.mapper.nextCursor
import com.twidere.twiderex.dataprovider.mapper.toPagingTimeline
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.paging.PagingTimeLineWithStatus
import com.twidere.twiderex.paging.ArrayListCompat
import com.twidere.twiderex.paging.IPagination
import com.twidere.twiderex.paging.mediator.paging.PagingTimelineMediatorBase

internal class TwitterGuestTweetThreadMediator(
  private val service: TwitterService,
  private val statusKey: MicroBlogKey,
  private val twitterGuestService: TwitterGuestService,
  accountKey: MicroBlogKey,
  database: CacheDatabase,
) : PagingTimelineMediatorBase<TwitterGuestTweetThreadMediator.PagingKey>(
  accountKey,
  database
) {
  data class PagingKey(
    val conversationId: String,
    val cursor: String?,
  ) : IPagination

  class PagingResult(
    data: List<IStatus>,
    val conversationId: String,
    val order: List<PagingTimeLineWithStatus>,
    val cursor: String? = null,
  ) : ArrayListCompat<IStatus>(data)

  override suspend fun load(
    pageSize: Int,
    paging: PagingKey?
  ): List<IStatus> {
    val conversationId = if (paging == null) {
      val tweet = service.lookupStatus(statusKey.id)
      val actualTweet =
        tweet.referencedTweets?.firstOrNull { it.type == ReferencedTweetType.retweeted }?.status
          ?: tweet
      actualTweet.id ?: statusKey.id
    } else {
      paging.conversationId
    }
    val conversationResult = twitterGuestService.conversation(
      conversationId,
      pageSize,
      paging?.cursor,
    )
    val cursor = conversationResult.nextCursor("Bottom")

    val data = conversationResult.toPagingTimeline(
      pagingKey,
      accountKey,
    )

    val result = service.lookupStatuses(
      data.map {
        if (conversationId != statusKey.id && it.status.statusKey.id == conversationId) {
          statusKey.id
        } else {
          it.status.statusId
        }
      }
    )

    return PagingResult(
      order = data,
      data = result,
      cursor = cursor,
      conversationId = conversationId,
    )
  }

  override fun provideNextPage(
    raw: List<IStatus>,
    result: List<PagingTimeLineWithStatus>
  ): PagingKey {
    return if (raw is PagingResult) {
      PagingKey(cursor = raw.cursor, conversationId = raw.conversationId)
    } else {
      PagingKey(cursor = null, conversationId = statusKey.id)
    }
  }

  override fun hasMore(
    raw: List<IStatus>,
    result: List<PagingTimeLineWithStatus>,
    pageSize: Int
  ): Boolean {
    return if (raw is PagingResult) {
      raw.cursor != null
    } else {
      super.hasMore(raw, result, pageSize)
    }
  }

  override fun transform(
    state: PagingState<Int, PagingTimeLineWithStatus>,
    data: List<PagingTimeLineWithStatus>,
    list: List<IStatus>
  ): List<PagingTimeLineWithStatus> {
    return if (list is PagingResult) {
      val conversationId = list.conversationId
      data.map {
        it.copy(
          timeline = it.timeline.copy(
            sortId = list.order.firstOrNull { item ->
              val id = if (conversationId != statusKey.id && item.status.statusKey.id == conversationId) {
                statusKey.id
              } else {
                item.status.statusId
              }
              id == it.status.statusKey.id
            }?.timeline?.sortId ?: it.timeline.sortId
          )
        )
      }
    } else {
      super.transform(state, data, list)
    }
  }

  override val pagingKey: String = "status:$statusKey"
}
