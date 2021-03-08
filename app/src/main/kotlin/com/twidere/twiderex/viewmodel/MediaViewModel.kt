/*
 *  Twidere X
 *
 *  Copyright (C) 2020-2021 Tlaster <tlaster@outlook.com>
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
import com.twidere.services.http.MicroBlogException
import com.twidere.services.microblog.LookupService
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.repository.twitter.TwitterTweetsRepository
import com.twidere.twiderex.utils.notify
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class MediaViewModel @AssistedInject constructor(
    private val factory: TwitterTweetsRepository.AssistedFactory,
    private val inAppNotification: InAppNotification,
    @Assisted private val account: AccountDetails,
    @Assisted private val statusKey: MicroBlogKey,
) : ViewModel() {

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(account: AccountDetails, statusKey: MicroBlogKey): MediaViewModel
    }

    private val repository by lazy {
        account.service.let {
            factory.create(account.accountKey, it as LookupService)
        }
    }
    val loading = MutableLiveData(false)
    val status = liveData {
        emitSource(repository.loadTweetFromCache(statusKey))
    }

    init {
        viewModelScope.launch {
            loading.postValue(true)
            try {
                repository.loadTweetFromNetwork(statusKey.id)
            } catch (e: MicroBlogException) {
                e.notify(inAppNotification)
            } catch (e: IOException) {
                e.message?.let { inAppNotification.show(it) }
            } catch (e: HttpException) {
                e.message?.let { inAppNotification.show(it) }
            }
            loading.postValue(false)
        }
    }
}
