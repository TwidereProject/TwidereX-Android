package com.twidere.twiderex.paging.mediator.status

import com.twidere.services.microblog.model.IStatus
import com.twidere.services.twitter.TwitterService
import com.twidere.services.twitter.model.ReferencedTweetType
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.paging.mediator.paging.CursorWithCustomOrderPagination
import com.twidere.twiderex.paging.mediator.paging.CursorWithCustomOrderPagingMediator

class TwitterGuestTweetThreadMediator(
  private val service: TwitterService,
  private val statusKey: MicroBlogKey,
  accountKey: MicroBlogKey,
  database: CacheDatabase,
) : CursorWithCustomOrderPagingMediator(
  accountKey,
  database
) {
  override suspend fun load(pageSize: Int, paging: CursorWithCustomOrderPagination?): List<IStatus> {
    val conversationId: String = if (paging?.cursor != null) {
      paging.cursor
    } else {
      val current = service.lookupStatus(statusKey.id)
      val actualTweet = current.referencedTweets
        ?.firstOrNull { it.type == ReferencedTweetType.retweeted }
        ?.status ?: current
      actualTweet.id ?: statusKey.id
    }

    TODO("Not yet implemented")
  }
}
