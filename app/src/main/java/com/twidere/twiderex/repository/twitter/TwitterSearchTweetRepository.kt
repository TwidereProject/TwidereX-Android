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

import androidx.lifecycle.map
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.twidere.services.twitter.TwitterService
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.mapper.toDbTimeline
import com.twidere.twiderex.db.model.TimelineType
import com.twidere.twiderex.db.model.saveToDb
import com.twidere.twiderex.defaultLoadCount
import com.twidere.twiderex.model.UserKey
import com.twidere.twiderex.model.ui.UiStatus.Companion.toUi
import com.twidere.twiderex.repository.twitter.model.SearchResult

class TwitterSearchTweetRepository @AssistedInject constructor(
    private val database: AppDatabase,
    @Assisted private val userKey: UserKey,
    @Assisted private val service: TwitterService,
) {
    @AssistedInject.Factory
    interface AssistedFactory {
        fun create(
            userKey: UserKey,
            service: TwitterService,
        ): TwitterSearchTweetRepository
    }

    val liveData by lazy {
        database.timelineDao().getAllWithLiveData(userKey, TimelineType.SearchTweets).map { list ->
            list.map { status ->
                status.toUi(userKey)
            }
        }
    }

    suspend fun loadTweets(query: String, nextPage: String? = null): SearchResult {
        val searchResponse = service.searchTweets(
            query,
            count = defaultLoadCount,
            nextPage = nextPage
        )
        val status = searchResponse.data ?: emptyList()
        val db = status.map { it.toDbTimeline(userKey, TimelineType.SearchTweets) }
        db.saveToDb(database)
        return SearchResult(status, searchResponse.nextPage)
    }
}
