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
package com.twidere.twiderex.repository.twitter

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.twidere.services.http.MicroBlogException
import com.twidere.services.twitter.TwitterService
import com.twidere.services.twitter.model.ReferencedTweetType
import com.twidere.services.twitter.model.StatusV2
import com.twidere.services.twitter.model.exceptions.TwitterApiExceptionV2
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.db.mapper.toDbTimeline
import com.twidere.twiderex.db.model.DbTimelineWithStatus
import com.twidere.twiderex.db.model.TimelineType
import com.twidere.twiderex.db.model.saveToDb
import com.twidere.twiderex.defaultLoadCount
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.model.ui.UiStatus.Companion.toUi
import com.twidere.twiderex.repository.twitter.model.SearchResult
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class TwitterConversationRepository @AssistedInject constructor(
    private val database: CacheDatabase,
    @Assisted private val accountKey: MicroBlogKey,
    @Assisted private val service: TwitterService,
) {

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(
            accountKey: MicroBlogKey,
            service: TwitterService,
        ): TwitterConversationRepository
    }

    val liveData by lazy {
        database.timelineDao().getAllWithLiveData(accountKey, TimelineType.Conversation)
            .map { list ->
                list.map { status ->
                    status.toUi(accountKey)
                }
            }
    }

    suspend fun loadPrevious(statusV2: StatusV2): List<StatusV2> {
        var current = statusV2
        val list = arrayListOf<StatusV2>()
        while (true) {
            val referencedTweetId = current.referencedTweets
                ?.firstOrNull { it.type == ReferencedTweetType.replied_to }?.id
            if (referencedTweetId == null) {
                break
            } else {
                try {
                    val result = service.lookupStatus(referencedTweetId)
                    val db = result.toDbTimeline(accountKey, TimelineType.Conversation)
                    listOf(db).saveToDb(database)
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

    suspend fun loadTweetFromCache(statusKey: MicroBlogKey): UiStatus? {
        return database.timelineDao().findWithStatusKey(statusKey, accountKey)?.toUi(accountKey)
    }

    fun getStatusLiveData(statusKey: MicroBlogKey): LiveData<UiStatus?> {
        return database.statusDao().findWithStatusKeyWithReferenceLiveData(statusKey).map {
            it?.toUi(accountKey)
        }
    }

    suspend fun loadTweetFromNetwork(statusId: String): StatusV2 {
        return service.lookupStatus(statusId)
    }

    suspend fun toUiStatus(status: StatusV2): UiStatus {
        val db = status.toDbTimeline(accountKey, TimelineType.Conversation)
        listOf(db).saveToDb(database)
        return db.toUi(accountKey)
    }

    private fun buildConversation(
        status: DbTimelineWithStatus,
        searchResponse: List<DbTimelineWithStatus>
    ): List<DbTimelineWithStatus> {
        return searchResponse.filter {
            it.status.status.data.replyStatusKey?.id == status.status.status.data.statusId
        }
//        return searchResponse.filter {
//            it.status.replyTo?.data?.statusId == status.status.status.data.statusId
//        }
//            .map {
//                listOf(it) + buildConversation(it, searchResponse).flatten()
//            }
    }

    suspend fun loadConversation(tweet: StatusV2, nextPage: String? = null): SearchResult {
        val dbTweet = tweet.toDbTimeline(
            accountKey = accountKey,
            TimelineType.Conversation
        )
        val conversationId = tweet.conversationID ?: return SearchResult(emptyList(), null)
        val searchResponse = try {
            service.searchTweets(
                "conversation_id:$conversationId",
                count = defaultLoadCount,
                nextPage = nextPage
            )
        } catch (e: TwitterApiExceptionV2) {
            service.searchTweetsV1(
                "to:${dbTweet.status.status.user.user.screenName} since_id:${dbTweet.status.status.data.statusId}",
                count = defaultLoadCount,
                max_id = nextPage
            )
        }
        val status = searchResponse.status
        val db = status.map { it.toDbTimeline(accountKey, TimelineType.Conversation) }
        val result = buildConversation(
            dbTweet,
            db,
        )
        db.saveToDb(database)
        return SearchResult(
            result.map { it.toUi(accountKey = accountKey) },
            searchResponse.nextPage
        )
    }
}
