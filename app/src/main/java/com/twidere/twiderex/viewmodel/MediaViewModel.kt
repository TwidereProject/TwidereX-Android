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
package com.twidere.twiderex.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.twidere.services.microblog.LookupService
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.twitter.TwitterTweetsRepository
import kotlinx.coroutines.coroutineScope

class MediaViewModel @ViewModelInject constructor(
    accountRepository: AccountRepository,
    private val factory: TwitterTweetsRepository.AssistedFactory,
) : ViewModel() {

    private val repository = accountRepository.getCurrentAccount().let { accountDetails ->
        accountDetails.service.let {
            factory.create(accountDetails.key, it as LookupService)
        }
    }
    val loading = MutableLiveData(false)
    val status = MutableLiveData<UiStatus>()

    suspend fun init(statusId: String) = coroutineScope {
        if (status.value != null) {
            return@coroutineScope
        }
        loading.postValue(true)
        repository.loadTweetFromCache(statusId)?.let {
            status.postValue(it)
        } ?: run {
            repository.loadTweetFromNetwork(statusId).let {
                status.postValue(it)
            }
        }
        loading.postValue(false)
    }
}
