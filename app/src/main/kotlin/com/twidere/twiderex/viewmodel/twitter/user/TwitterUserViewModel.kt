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
package com.twidere.twiderex.viewmodel.twitter.user

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.twidere.services.microblog.LookupService
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.repository.UserRepository
import com.twidere.twiderex.utils.notify
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class TwitterUserViewModel @AssistedInject constructor(
    private val factory: UserRepository.AssistedFactory,
    private val inAppNotification: InAppNotification,
    @Assisted private val account: AccountDetails,
    @Assisted private val screenName: String,
) : ViewModel() {

    private val repository by lazy {
        account.service.let {
            factory.create(account.accountKey, it as LookupService)
        }
    }

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(
            account: AccountDetails,
            screenName: String?,
        ): TwitterUserViewModel
    }

    val error = MutableLiveData<Throwable>(null)

    val user = liveData {
        runCatching {
            repository.lookupUserByName(screenName)
        }.onSuccess {
            emit(it)
        }.onFailure {
            it.notify(inAppNotification)
            emit(null)
            error.postValue(it)
        }
    }
}
