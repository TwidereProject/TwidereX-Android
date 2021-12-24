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

import com.twidere.services.http.MicroBlogException
import com.twidere.services.microblog.model.BasicSearchResponse
import com.twidere.services.microblog.model.ISearchResponse
import com.twidere.services.microblog.model.IStatus
import com.twidere.services.nitter.NitterService
import com.twidere.services.twitter.TwitterService
import com.twidere.services.twitter.model.ReferencedTweetType
import com.twidere.services.twitter.model.Status
import com.twidere.services.twitter.model.StatusV2
import com.twidere.services.twitter.model.exceptions.TwitterApiExceptionV2
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.defaultLoadCount
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.paging.mediator.paging.CursorWithCustomOrderPagination
import com.twidere.twiderex.paging.mediator.paging.CursorWithCustomOrderPagingMediator
import com.twidere.twiderex.paging.mediator.paging.CursorWithCustomOrderPagingResult

class TwitterConversationMediator(
    private val service: TwitterService,
    private val statusKey: MicroBlogKey,
    private val nitterService: NitterService?,
    accountKey: MicroBlogKey,
    database: CacheDatabase,
) : CursorWithCustomOrderPagingMediator(
    accountKey,
    database
) {
    private var _targetTweet: StatusV2? = null
    private suspend fun loadPrevious(): List<StatusV2> {
        var current = _targetTweet ?: return emptyList()
        val list = arrayListOf<StatusV2>()
        while (true) {
            val referencedTweetId = current.referencedTweets
                ?.firstOrNull { it.type == ReferencedTweetType.replied_to }?.id
            if (referencedTweetId == null) {
                break
            } else {
                try {
                    val result = service.lookupStatus(referencedTweetId)
                    list.add(result)
                    current = result
                } catch (e: MicroBlogException) {
                    // TODO: show error
                    break
                }
            }
        }
        return list.reversed()
    }

    private suspend fun loadConversation(nextPage: String? = null): ISearchResponse {
        if (nitterService != null) {
            try {
                val nitterResult = nitterService.conversation(statusKey.id, statusKey.id, nextPage)
                return nitterResult?.let {
                    it.items.flatMap { it.statuses }
                }?.takeIf { it.isNotEmpty() }?.let { items ->
                    val result = service.lookupStatuses(items.mapNotNull { it.statusId })
                    BasicSearchResponse(
                        nextPage = nitterResult.nextPage,
                        status = result
                    )
                } ?: BasicSearchResponse(null, emptyList())
            } catch (e: MicroBlogException) {
                e.printStackTrace()
            }
        }
        return try {
            service.searchV2(
                "conversation_id:${_targetTweet?.conversationID}",
                count = defaultLoadCount,
                nextPage = nextPage
            )
        } catch (e: TwitterApiExceptionV2) {
            service.searchV1(
                "to:${_targetTweet?.user?.username} since_id:${_targetTweet?.id}",
                count = defaultLoadCount,
                max_id = nextPage
            )
        }.let {
            BasicSearchResponse(
                nextPage = it.nextPage,
                status = buildConversation(it.status)
            )
        }
    }

    override suspend fun load(
        pageSize: Int,
        paging: CursorWithCustomOrderPagination?
    ): List<IStatus> {
        if (paging != null && paging.cursor == null) {
            return emptyList()
        }
        val ancestors = if (paging == null) {
            val tweet = service.lookupStatus(statusKey.id)
            _targetTweet =
                tweet.referencedTweets?.firstOrNull { it.type == ReferencedTweetType.retweeted }?.status
                    ?: tweet
            loadPrevious() + tweet
        } else {
            emptyList()
        }
        val descendantsResult = loadConversation(paging?.cursor)
        val result = (ancestors + descendantsResult.status)

        return CursorWithCustomOrderPagingResult(
            data = result,
            cursor = descendantsResult.nextPage,
            nextOrder = paging?.nextOrder ?: 0
        )
    }

    private fun buildConversation(status: List<IStatus>): List<IStatus> {
        return status.filter {
            when (it) {
                is Status -> {
                    it.inReplyToStatusID == _targetTweet?.id
                }
                is StatusV2 -> {
                    it.referencedTweets
                        ?.firstOrNull { it.type == ReferencedTweetType.replied_to }
                        ?.status?.id == _targetTweet?.id
                }
                else -> {
                    false
                }
            }
        }
    }

    override val pagingKey: String = "status:$statusKey"
}
