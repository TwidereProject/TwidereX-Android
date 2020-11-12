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
package com.twidere.twiderex.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.twidere.services.microblog.LookupService
import com.twidere.twiderex.di.assisted.IAssistedFactory
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.repository.twitter.TwitterTweetsRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class MediaViewModel @AssistedInject constructor(
    private val factory: TwitterTweetsRepository.AssistedFactory,
    @Assisted private val account: AccountDetails,
    @Assisted private val statusId: String,
) : ViewModel() {

    @AssistedInject.Factory
    interface AssistedFactory : IAssistedFactory {
        fun create(account: AccountDetails, statusId: String): MediaViewModel
    }

    private val repository by lazy {
        account.service.let {
            factory.create(account.key, it as LookupService)
        }
    }
    val loading = MutableLiveData(false)
    val status = liveData {
        emitSource(repository.loadTweetFromCache(statusId))
    }

    init {
        viewModelScope.launch {
            loading.postValue(true)
            repository.loadTweetFromNetwork(statusId)
            loading.postValue(false)
        }
    }
}
