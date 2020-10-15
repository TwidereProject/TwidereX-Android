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
import com.twidere.services.twitter.model.StatusV2
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.twitter.TwitterConversationRepository

class TwitterStatusViewModel @ViewModelInject constructor(
    private val accountRepository: AccountRepository,
    private val factory: TwitterConversationRepository.AssistedFactory,
) : ViewModel() {
    private val repository = accountRepository.getCurrentAccount().let { accountDetails ->
        accountDetails.service.let {
            factory.create(accountDetails.key, it as SearchService, it as LookupService)
        }
    }
    val items = repository.liveData.switchMap { list ->
        liveData {
            emit(
                conversations.mapNotNull { conversation ->
                    val result = list.firstOrNull {
                        it.statusId == conversation.id
                    }
                    if (status.value?.retweet?.let {
                        it.statusId == result?.statusId
                    } == true) {
                        status.value
                    } else {
                        result
                    }
                }
            )
        }
    }
    val status = MutableLiveData<UiStatus>()
    val loadingPrevious = MutableLiveData(false)
    val loadingMore = MutableLiveData(false)
    private val conversations = arrayListOf<StatusV2>()
    val currentStatusIndex = MutableLiveData(0)

    suspend fun init(data: UiStatus) {
        if (conversations.any()) {
            return
        }
        status.postValue(data)
        loadingPrevious.postValue(true)
        loadingMore.postValue(true)
        val result = repository.loadConversation(data)
        val list = listOf(result.root) + result.subs.flatten()
        conversations.addAll(list)
        currentStatusIndex.postValue(list.indexOfFirst { it.id == data.statusId })
        loadingMore.postValue(false)
        loadingPrevious.postValue(false)
    }
}
