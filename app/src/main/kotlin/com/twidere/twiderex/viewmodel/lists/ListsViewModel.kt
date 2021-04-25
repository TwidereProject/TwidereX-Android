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
import androidx.paging.filter
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.ListsMode
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.repository.ListsRepository
import com.twidere.twiderex.utils.notify
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ListsViewModel @AssistedInject constructor(
    private val listsRepository: ListsRepository,
    @Assisted private val account: AccountDetails,
) : ViewModel() {
    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(account: AccountDetails): ListsViewModel
    }

    val source by lazy {
        listsRepository.fetchLists(account = account).cachedIn(viewModelScope)
    }

    val ownerSource by lazy {
        source.map { pagingData ->
            pagingData.filter { it.isOwner(account.user.userId) }
        }
    }

    val subscribedSource by lazy {
        source.map { pagingData ->
            pagingData.filter { !it.isOwner(account.user.userId) }
        }
    }
}

class ListsModifyViewModel @AssistedInject constructor(
    private val listsRepository: ListsRepository,
    private val inAppNotification: InAppNotification,
    @Assisted private val account: AccountDetails,
) : ViewModel() {

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(account: AccountDetails): ListsModifyViewModel
    }

    val modifySuccess = MutableLiveData<Boolean>(false)
    val loading = MutableLiveData<Boolean>(false)

    fun createList(
        title: String,
        description: String? = null,
        private: Boolean = false
    ) {
        loadingRequest {
            listsRepository.createLists(
                account = account,
                title = title,
                description = description,
                mode = if (private)ListsMode.PRIVATE.value else ListsMode.PUBLIC.value
            )
        }
    }

    fun editList(
        listId: String,
        title: String,
        description: String? = null,
        private: Boolean = false
    ) {
        loadingRequest {
            listsRepository.updateLists(
                account = account,
                listId = listId,
                title = title,
                description = description,
                mode = if (private)ListsMode.PRIVATE.value else ListsMode.PUBLIC.value
            )
        }
    }

    fun deleteList(
        listId: String,
        listKey: MicroBlogKey
    ) {
        loadingRequest {
            listsRepository.deleteLists(
                account = account,
                listKey = listKey,
                listId = listId,
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
