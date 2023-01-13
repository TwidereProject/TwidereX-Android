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

import com.twidere.services.twitter.TwitterGuestService
import com.twidere.services.twitter.TwitterService
import com.twidere.services.twitter.model.ReferencedTweetType
import com.twidere.services.twitter.model.exceptions.TwitterApiException
import com.twidere.twiderex.dataprovider.mapper.nextCursor
import com.twidere.twiderex.dataprovider.mapper.toPagingTimeline
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.paging.mediator.paging.TimelinePagingMediator

internal class TwitterGuestTweetThreadMediator(
  private val statusKey: MicroBlogKey,
  private val service: TwitterService,
  database: CacheDatabase,
  accountKey: MicroBlogKey,
) : TimelinePagingMediator<TwitterGuestTweetThreadMediator.PagingKey>(
  database,
  pagingKey = "status:$statusKey",
  accountKey = accountKey,
) {
  data class PagingKey(
    val conversationId: String,
    val cursor: String,
    val index: Long,
  )

  override suspend fun load(nextKey: PagingKey?, pageSize: Int): TimelineData<PagingKey> {
    val conversationId: String = nextKey?.conversationId ?: getConversationId(statusKey.id)
    val result = conversation(
      conversationId,
      pageSize,
      nextKey?.cursor,
    )
    val orderIndex = nextKey?.index ?: 0
    val data = result.toPagingTimeline(
      pagingKey = pagingKey,
      accountKey = accountKey,
      orderKey = orderIndex,
    )
    val cursor = result.nextCursor("Bottom")
    return TimelineData(
      data = data,
      key = cursor?.let {
        PagingKey(
          conversationId = conversationId,
          cursor = cursor,
          index = orderIndex + data.size,
        )
      }
    )
  }

  private suspend fun conversation(
    tweetId: String,
    count: Int,
    cursor: String? = null,
  ) = tryTwitterRequest {
    it.conversation(
      tweetId = tweetId,
      count = count,
      cursor = cursor,
    )
  }

  private suspend inline fun <reified T> tryTwitterRequest(
    request: (service: TwitterGuestService) -> T
  ): T {
    val token = TwitterGuestService.getGuestToken().guestToken
      ?: throw TwitterApiException(error = "Failed to get guest token")
    val service = TwitterGuestService(token)
    return request(service)
  }

  private suspend fun getConversationId(tweetId: String): String {
    val current = service.lookupStatus(tweetId)
    val actualTweet = current.referencedTweets
      ?.firstOrNull { it.type == ReferencedTweetType.retweeted }
      ?.status ?: current
    return requireNotNull(actualTweet.id ?: tweetId) {
      "Failed to get conversationId with status:$tweetId"
    }
  }
}
