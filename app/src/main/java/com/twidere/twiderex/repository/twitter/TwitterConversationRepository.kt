/*
 *  TwidereX
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of TwidereX.
 * 
 *  TwidereX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  TwidereX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with TwidereX. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.repository.twitter

import androidx.lifecycle.map
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.twidere.services.microblog.LookupService
import com.twidere.services.microblog.SearchService
import com.twidere.services.twitter.model.ReferencedTweetType
import com.twidere.services.twitter.model.StatusV2
import com.twidere.services.twitter.model.TwitterSearchResponseV2
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.db.mapper.toDbTimeline
import com.twidere.twiderex.db.model.DbTimelineWithStatus
import com.twidere.twiderex.db.model.TimelineType
import com.twidere.twiderex.defaultLoadCount
import com.twidere.twiderex.model.UserKey
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.model.ui.UiStatus.Companion.toUi
import javax.inject.Singleton

data class ConversationData(
    val root: StatusV2,
    val subs: List<List<StatusV2>>,
)

@Singleton
class TwitterConversationRepository @AssistedInject constructor(
    private val database: AppDatabase,
    private val cache: CacheDatabase,
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
        cache.timelineDao().getAllWithLiveData(userKey, TimelineType.Conversation).map { list ->
            list.map { status ->
                status.toUi()
            }
        }
    }

    suspend fun loadConversation(data: UiStatus): ConversationData {
        val isRetweet = data.retweet != null//TODO: retweet
        val tweet = lookupService.lookupStatus((data.retweet ?: data).statusId) as StatusV2
        val conversationId = tweet.conversationID ?: return ConversationData(tweet, emptyList())
        val root = if (conversationId == tweet.id) {
            tweet
        } else {
            lookupService.lookupStatus(conversationId) as StatusV2
        }
        val searchResponse = searchService.searchTweets(
            "conversation_id:$conversationId",
            count = defaultLoadCount,
        ) as TwitterSearchResponseV2
        val result = arrayListOf<ArrayList<StatusV2>>()

        searchResponse.data?.forEach { status ->
            val referencedTweetId = status.referencedTweets
                ?.firstOrNull { it.type == ReferencedTweetType.replied_to }?.id
            if (!result.any { list -> list.any { it.id == status.id } }) {
                if (referencedTweetId == conversationId) { // direct reply to the main tweet
                    result.firstOrNull { list -> // find if the reply tweet has other replies to it
                        list.any { statusV2 ->
                            statusV2.referencedTweets?.firstOrNull {
                                it.type == ReferencedTweetType.replied_to
                            }?.id == status.id
                        }
                    }
                        ?.add(status)
                        ?: result.add(arrayListOf(status)) // if not, just add to list
                } else if (referencedTweetId != null) {
                    result.firstOrNull { list -> // find the reply to tweet
                        list.any { it.id == referencedTweetId }
                    }
                        ?.add(status)
                        ?: searchResponse.includes?.tweets?.firstOrNull { // if not, add it with reply to tweet
                            it.id == referencedTweetId
                        }
                            ?.let {
                                result.add(arrayListOf(it, status))
                            }
                }
            }
        }

        result.forEach { list -> list.sortByDescending { it.createdAt } }
        result.sortWith(
            compareBy(
                // try to figure out how twitter ordering the conversation tweets
                { !(it.size == 1 && it.first().authorID == tweet.authorID) },
                { it.lastOrNull()?.authorID != tweet.authorID },
                { it.lastOrNull()?.publicMetrics?.likeCount?.inv() },
                { it.lastOrNull()?.publicMetrics?.retweetCount?.inv() },
                { it.lastOrNull()?.createdAt },
            )
        )

        result.forEach { it.reverse() }
        val db = result.flatten().let { list ->
            listOf(root) + list
        }.map { it.toDbTimeline(userKey, TimelineType.Conversation) }
        saveData(db)
        return ConversationData(root, result)
    }

    private suspend fun saveData(timeline: List<DbTimelineWithStatus>) {
        val data = timeline
            .map { listOf(it.status, it.quote, it.retweet) }
            .flatten()
            .filterNotNull()
        data.map { it.user }.let {
            cache.userDao().insertAll(it)
            database.userDao().update(*it.toTypedArray())
        }
        cache.mediaDao().insertAll(data.map { it.media }.flatten())
        data.map { it.status }.let {
            cache.statusDao().insertAll(it)
            database.statusDao().update(*it.toTypedArray())
        }
        timeline.map { it.timeline }.let {
            cache.timelineDao().insertAll(it)
            database.timelineDao().update(*it.toTypedArray())
        }
    }
}
