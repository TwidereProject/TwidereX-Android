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
package com.twidere.twiderex.viewmodel.twitter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.twidere.services.microblog.LookupService
import com.twidere.services.microblog.SearchService
import com.twidere.services.twitter.model.ReferencedTweetType
import com.twidere.services.twitter.model.StatusV2
import com.twidere.twiderex.di.assisted.IAssistedFactory
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.repository.twitter.TwitterConversationRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TwitterStatusViewModel @AssistedInject constructor(
    private val factory: TwitterConversationRepository.AssistedFactory,
    @Assisted private val account: AccountDetails,
    @Assisted private val statusId: String,
) : ViewModel() {

    @AssistedInject.Factory
    interface AssistedFactory : IAssistedFactory {
        fun create(account: AccountDetails, statusId: String): TwitterStatusViewModel
    }

    private lateinit var targetTweet: StatusV2
    private var nextPage: String? = null
    private val repository by lazy {
        account.service.let {
            factory.create(account.key, it as SearchService, it as LookupService)
        }
    }

    val moreConversations = repository.liveData.switchMap { list ->
        liveData {
            emit(
                conversations.mapNotNull { conversation ->
                    list.firstOrNull {
                        it.statusId == conversation.id
                    }
                }
            )
        }
    }

    val previousConversations = repository.liveData.switchMap { list ->
        liveData {
            emit(
                previous.mapNotNull { conversation ->
                    list.firstOrNull { it.statusId == conversation.id }
                }
            )
        }
    }

    val status = liveData {
        emitSource(repository.getStatusLiveData(statusId))
    }
    val loadingPrevious = MutableLiveData(false)
    val loadingMore = MutableLiveData(false)
    private val conversations = arrayListOf<StatusV2>()
    private val previous = arrayListOf<StatusV2>()

    init {
        viewModelScope.launch {
            loadingPrevious.postValue(true)
            loadingMore.postValue(true)
            val tweet = repository.loadTweetFromNetwork(statusId)
            repository.toUiStatus(tweet)
            targetTweet =
                tweet.referencedTweets?.firstOrNull { it.type == ReferencedTweetType.retweeted }?.status
                ?: tweet
            async {
                val list = repository.loadPrevious(targetTweet)
                previous.addAll(list)
                loadingPrevious.postValue(false)
            }
            async {
                val result = repository.loadConversation(targetTweet)
                nextPage = result.nextPage
                conversations.addAll(result.result)
                loadingMore.postValue(false)
            }
        }
    }

    suspend fun loadMore() {
        if (nextPage == null || loadingMore.value == true) {
            return
        }
        loadingMore.postValue(true)
        val result = repository.loadConversation(targetTweet, nextPage = nextPage)
        nextPage = result.nextPage
        conversations.addAll(result.result)
        loadingMore.postValue(false)
    }
}
