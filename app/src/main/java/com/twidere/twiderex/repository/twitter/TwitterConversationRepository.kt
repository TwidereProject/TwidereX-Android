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
package com.twidere.twiderex.repository.twitter

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.twidere.services.microblog.LookupService
import com.twidere.services.microblog.SearchService
import com.twidere.services.twitter.model.ReferencedTweetType
import com.twidere.services.twitter.model.StatusV2
import com.twidere.services.twitter.model.TwitterSearchResponseV2
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.mapper.toDbTimeline
import com.twidere.twiderex.db.model.TimelineType
import com.twidere.twiderex.db.model.saveToDb
import com.twidere.twiderex.defaultLoadCount
import com.twidere.twiderex.model.UserKey
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.model.ui.UiStatus.Companion.toUi
import com.twidere.twiderex.repository.twitter.model.SearchResult

class TwitterConversationRepository @AssistedInject constructor(
    private val database: AppDatabase,
    @Assisted private val userKey: UserKey,
    @Assisted private val searchService: SearchService,
    @Assisted private val lookupService: LookupService,
) {

    @AssistedInject.Factory
    interface AssistedFactory {
        fun create(
            userKey: UserKey,
            searchService: SearchService,
            lookupService: LookupService,
        ): TwitterConversationRepository
    }

    val liveData by lazy {
        database.timelineDao().getAllWithLiveData(userKey, TimelineType.Conversation).map { list ->
            list.map { status ->
                status.toUi(userKey)
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
                val result = lookupService.lookupStatus(referencedTweetId) as StatusV2
                val db = result.toDbTimeline(userKey, TimelineType.Conversation)
                listOf(db).saveToDb(database)
                list.add(result)
                current = result
            }
        }
        return list.reversed()
    }

    suspend fun loadTweetFromCache(statusId: String): UiStatus? {
        return database.timelineDao().findWithStatusId(statusId, userKey)?.toUi(userKey)
    }

    fun getStatusLiveData(statusId: String): LiveData<UiStatus?> {
        return database.statusDao().findWithStatusIdWithReferenceLiveData(statusId).map {
            it?.toUi(userKey)
        }
    }

    suspend fun loadTweetFromNetwork(statusId: String): StatusV2 {
        return lookupService.lookupStatus(statusId) as StatusV2
    }

    suspend fun toUiStatus(status: StatusV2): UiStatus {
        val db = status.toDbTimeline(userKey, TimelineType.Conversation)
        listOf(db).saveToDb(database)
        return db.toUi(userKey)
    }

    private fun buildConversation(
        status: StatusV2,
        searchResponse: List<StatusV2>
    ): List<List<StatusV2>> {
        return searchResponse.filter {
            it.referencedTweets?.firstOrNull {
                it.type == ReferencedTweetType.replied_to
            }?.id == status.id
        }
            .map {
                listOf(it) + buildConversation(it, searchResponse).flatten()
            }
    }

    suspend fun loadConversation(tweet: StatusV2, nextPage: String? = null): SearchResult {
        val conversationId = tweet.conversationID ?: return SearchResult(emptyList(), null)
        val searchResponse = searchService.searchTweets(
            "conversation_id:$conversationId",
            count = defaultLoadCount,
            nextPage = nextPage
        ) as TwitterSearchResponseV2
        val status = searchResponse.data ?: emptyList()
        val result = buildConversation(tweet, status)
        val db = result.flatten().map { it.toDbTimeline(userKey, TimelineType.Conversation) }
        db.saveToDb(database)
        return SearchResult(result.flatten(), searchResponse.nextPage)
    }
}
