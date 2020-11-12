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
package com.twidere.twiderex.viewmodel.twitter.search

import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.twidere.services.twitter.TwitterService
import com.twidere.services.twitter.model.StatusV2
import com.twidere.twiderex.di.assisted.IAssistedFactory
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.repository.twitter.TwitterSearchTweetRepository
import kotlinx.coroutines.launch

class TwitterSearchMediaViewModel @AssistedInject constructor(
    private val factory: TwitterSearchTweetRepository.AssistedFactory,
    @Assisted private val account: AccountDetails,
    @Assisted keyword: String,
) : TwitterSearchListViewModelBase(keyword = keyword) {

    @AssistedInject.Factory
    interface AssistedFactory : IAssistedFactory {
        fun create(account: AccountDetails, keyword: String): TwitterSearchMediaViewModel
    }

    private var nextPage: String? = null
    private val tweets = arrayListOf<StatusV2>()
    private val service by lazy {
        account.service as TwitterService
    }
    private val repository by lazy {
        factory.create(account.key, service)
    }

    val source by lazy {
        repository.liveData.switchMap { list ->
            liveData {
                emit(tweets.mapNotNull { tweet -> list.firstOrNull { it.statusId == tweet.id } })
            }
        }
    }

    override fun reset(keyword: String) {
        if (this.keyword == keyword) {
            return
        }
        super.reset(keyword)
        hasMore = true
        nextPage = null
        tweets.clear()
    }

    override suspend fun refresh() {
        refreshing.postValue(true)
        reset(keyword)
        loadData()
        refreshing.postValue(false)
    }

    override suspend fun loadMore() {
        if (!hasMore) {
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

    init {
        viewModelScope.launch {
            refresh()
        }
    }
}
