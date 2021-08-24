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

import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.filter
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.ListsMode
import com.twidere.twiderex.model.ui.UiList
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.repository.ListsRepository
import com.twidere.twiderex.utils.notify
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
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
            pagingData.filter { !it.isOwner(account.user.userId) && it.isFollowed }
        }
    }
}

abstract class ListsOperatorViewModel(
    protected val inAppNotification: InAppNotification,
) : ViewModel() {
    val modifySuccess = MutableStateFlow(false)
    val loading = MutableStateFlow(false)

    protected fun loadingRequest(onResult: (success: Boolean, list: UiList?) -> Unit, request: suspend () -> UiList?) {
        loading.value = true
        viewModelScope.launch {
            runCatching {
                val result = request()
                modifySuccess.value = true
                onResult(true, result)
            }.onFailure {
                it.notify(inAppNotification)
                modifySuccess.value = false
                onResult(false, null)
                loading.value = false
            }.onSuccess {
                loading.value = false
            }
        }
    }
}

class ListsCreateViewModel @AssistedInject constructor(
    inAppNotification: InAppNotification,
    private val listsRepository: ListsRepository,
    @Assisted private val account: AccountDetails,
    @Assisted private val onResult: (success: Boolean, list: UiList?) -> Unit
) : ListsOperatorViewModel(inAppNotification) {
    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(account: AccountDetails, onResult: (success: Boolean, list: UiList?) -> Unit): ListsCreateViewModel
    }

    fun createList(
        title: String,
        description: String? = null,
        private: Boolean = false
    ) {
        loadingRequest(onResult) {
            listsRepository.createLists(
                account = account,
                title = title,
                description = description,
                mode = if (private)ListsMode.PRIVATE.value else ListsMode.PUBLIC.value
            )
        }
    }
}

class ListsModifyViewModel @AssistedInject constructor(
    private val listsRepository: ListsRepository,
    inAppNotification: InAppNotification,
    @Assisted private val account: AccountDetails,
    @Assisted private val listKey: MicroBlogKey,
) : ListsOperatorViewModel(inAppNotification) {

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(account: AccountDetails, listKey: MicroBlogKey): ListsModifyViewModel
    }

    val editName = MutableStateFlow("")
    var editDesc = MutableStateFlow("")
    var editPrivate = MutableStateFlow(false)

    val source by lazy {
        listsRepository.findListWithListKey(account = account, listKey = listKey)
    }

    init {
        viewModelScope.launch {
            source.firstOrNull()?.let {
                editName.value = it.title
                editDesc.value = it.descriptions
                editPrivate.value = it.isPrivate
            }
        }
    }

    fun editList(
        listId: String = listKey.id,
        title: String,
        description: String? = null,
        private: Boolean = false,
        onResult: (success: Boolean, list: UiList?) -> Unit = { _, _ -> }
    ) {
        loadingRequest(onResult) {
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
        listId: String = this.listKey.id,
        listKey: MicroBlogKey = this.listKey,
        onResult: (success: Boolean, list: UiList?) -> Unit = { _, _ -> }
    ) {
        loadingRequest(onResult) {
            listsRepository.deleteLists(
                account = account,
                listKey = listKey,
                listId = listId,
            )
        }
    }

    fun subscribeList(
        listKey: MicroBlogKey = this.listKey,
        onResult: (success: Boolean, list: UiList?) -> Unit = { _, _ -> }
    ) {
        loadingRequest(onResult) {
            listsRepository.subscribeLists(
                account = account,
                listKey = listKey
            )
        }
    }

    fun unsubscribeList(
        listKey: MicroBlogKey = this.listKey,
        onResult: (success: Boolean, list: UiList?) -> Unit = { _, _ -> }
    ) {
        loadingRequest(onResult) {
            listsRepository.unsubscribeLists(
                account = account,
                listKey = listKey
            )
        }
    }
}
