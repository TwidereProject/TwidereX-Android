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
package com.twidere.twiderex.viewmodel.lists

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.repository.ListUsersRepository
import com.twidere.twiderex.utils.notify
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch

class ListsUserViewModel @AssistedInject constructor(
    private val listsUsersRepository: ListUsersRepository,
    @Assisted private val account: AccountDetails,
    @Assisted private val listId: String,
) : ViewModel() {
    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(account: AccountDetails, listId: String): ListsUserViewModel
    }

    val members by lazy {
        listsUsersRepository.fetchMembers(account = account, listId = listId).cachedIn(viewModelScope)
    }

    val subscribers by lazy {
        listsUsersRepository.fetchSubscribers(account = account, listId = listId).cachedIn(viewModelScope)
    }
}

class ListsUserModifyViewModel @AssistedInject constructor(
    private val listsUsersRepository: ListUsersRepository,
    private val inAppNotification: InAppNotification,
    @Assisted private val account: AccountDetails,
    @Assisted private val listId: String,
) : ViewModel() {

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(account: AccountDetails, listId: String): ListsUserModifyViewModel
    }

    val modifySuccess = MutableLiveData<Boolean>(false)
    val loading = MutableLiveData<Boolean>(false)

    fun addMember(
        userId: String,
        screenName: String
    ) {
        loadingRequest {
            listsUsersRepository.addMember(
                account = account,
                listId = listId,
                userId = userId,
                screenName = screenName
            )
        }
    }

    fun removeMember(
        userId: String,
        screenName: String
    ) {
        loadingRequest {
            listsUsersRepository.removeMember(
                account = account,
                listId = listId,
                userId = userId,
                screenName = screenName
            )
        }
    }

    private fun loadingRequest(request: suspend () -> Unit) {
        loading.postValue(true)
        viewModelScope.launch {
            try {
                request()
                modifySuccess.postValue(true)
            } catch (e: Throwable) {
                e.notify(inAppNotification)
                modifySuccess.postValue(false)
            } finally {
                loading.postValue(false)
            }
        }
    }
}
