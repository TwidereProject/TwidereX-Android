package com.twidere.twiderex.paging.mediator

import androidx.paging.ExperimentalPagingApi
import com.twidere.services.microblog.model.IStatus
import com.twidere.services.twitter.TwitterService
import com.twidere.services.twitter.model.ReferencedTweetType
import com.twidere.services.twitter.model.StatusV2
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.model.DbPagingTimelineWithStatus
import com.twidere.twiderex.model.UserKey

@OptIn(ExperimentalPagingApi::class)
class ConversationMediator(
    private val conversationId: String,
    private val statusId: String,
    private val service: TwitterService,
    userKey: UserKey,
    database: AppDatabase,
) : PagingTimelineMediatorBase(userKey, database) {
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
        return nextPage == null
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