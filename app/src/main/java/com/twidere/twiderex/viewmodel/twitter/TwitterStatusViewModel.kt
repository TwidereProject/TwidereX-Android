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
package com.twidere.twiderex.viewmodel.twitter

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.twidere.services.microblog.LookupService
import com.twidere.services.microblog.SearchService
import com.twidere.services.twitter.model.ReferencedTweetType
import com.twidere.services.twitter.model.StatusV2
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.twitter.TwitterConversationRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class TwitterStatusViewModel @ViewModelInject constructor(
    private val accountRepository: AccountRepository,
    private val factory: TwitterConversationRepository.AssistedFactory,
) : ViewModel() {
    private lateinit var targetTweet: StatusV2
    private var nextPage: String? = null
    private val repository = accountRepository.getCurrentAccount().let { accountDetails ->
        accountDetails.service.let {
            factory.create(accountDetails.key, it as SearchService, it as LookupService)
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

    val status = MutableLiveData<UiStatus>()
    val loadingPrevious = MutableLiveData(false)
    val loadingMore = MutableLiveData(false)
    private val conversations = arrayListOf<StatusV2>()
    private val previous = arrayListOf<StatusV2>()

    suspend fun init(data: UiStatus) = coroutineScope {
        if (conversations.any() || previous.any() || loadingPrevious.value == true || loadingMore.value == true) {
            return@coroutineScope
        }
        status.postValue(data)
        loadingPrevious.postValue(true)
        loadingMore.postValue(true)
        val tweet = repository.loadTweet(data)
        val ui = repository.toUiStatus(tweet)
        targetTweet =
            tweet.referencedTweets?.firstOrNull { it.type == ReferencedTweetType.retweeted }?.status
            ?: tweet
        status.postValue(ui)
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
