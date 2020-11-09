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
package com.twidere.twiderex.viewmodel.twitter.search

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.twidere.services.twitter.TwitterService
import com.twidere.services.twitter.model.StatusV2
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.twitter.TwitterSearchTweetRepository

class TwitterSearchMediasViewModel @ViewModelInject constructor(
    private val accountRepository: AccountRepository,
    private val factory: TwitterSearchTweetRepository.AssistedFactory,
) : TwitterSearchListViewModelBase() {
    private var nextPage: String? = null
    private val tweets = arrayListOf<StatusV2>()
    private val service by lazy {
        accountRepository.getCurrentAccount().service as TwitterService
    }
    private val repository by lazy {
        factory.create(accountRepository.getCurrentAccount().key, service)
    }

    val source by lazy {
        repository.liveData.switchMap { list ->
            liveData {
                emit(tweets.mapNotNull { tweet -> list.firstOrNull { it.statusId == tweet.id } })
            }
        }
    }

    override fun reset(keyword: String) {
        super.reset(keyword)
        hasMore = true
        nextPage = null
        tweets.clear()
    }

    override suspend fun refresh() {
        if (refreshing.value == true || loaded.value == true) {
            return
        }
        refreshing.postValue(true)
        reset(keyword)
        loadData()
        loaded.postValue(true)
        refreshing.postValue(false)
    }

    override suspend fun loadMore() {
        if (!hasMore || loadingMore.value == true) {
            return
        }
        loadingMore.postValue(true)
        loadData()
        loadingMore.postValue(false)
    }

    private suspend fun loadData() {
        val result = repository.loadTweets("$keyword has:media -is:retweet")
        nextPage = result.nextPage
        hasMore = result.nextPage != null
        tweets.addAll(result.result)
    }
}
