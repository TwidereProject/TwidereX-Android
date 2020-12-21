/*
 *  Twidere X
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
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
package com.twidere.twiderex.paging.mediator

import androidx.paging.ExperimentalPagingApi
import com.twidere.services.microblog.model.IStatus
import com.twidere.services.twitter.TwitterService
import com.twidere.services.twitter.model.ReferencedTweetType
import com.twidere.services.twitter.model.StatusV2
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.model.DbPagingTimelineWithStatus
import com.twidere.twiderex.model.MicroBlogKey

@OptIn(ExperimentalPagingApi::class)
class ConversationMediator(
    private val conversationId: String,
    private val statusId: String,
    private val service: TwitterService,
    accountKey: MicroBlogKey,
    database: AppDatabase,
) : PagingTimelineMediatorBase(accountKey, database) {
    override val pagingKey: String
        get() = "conversation:$conversationId"
    private var nextPage: String? = null
    override suspend fun load(pageSize: Int, max_id: String?): List<IStatus> {
        val result = service.searchTweets(
            "conversation_id:$conversationId",
            count = pageSize,
            nextPage = nextPage,
        )
        nextPage = result.nextPage
        return buildConversation(result.data ?: emptyList()).flatten()
    }

    override fun hasMore(result: List<DbPagingTimelineWithStatus>, pageSize: Int): Boolean {
        return nextPage != null
    }

    private fun buildConversation(
        searchResponse: List<StatusV2>
    ): List<List<StatusV2>> {
        return searchResponse.filter {
            it.referencedTweets?.firstOrNull {
                it.type == ReferencedTweetType.replied_to
            }?.id == statusId
        }
            .map {
                listOf(it) + buildConversation(searchResponse).flatten()
            }
    }
}
