/*
 *  Twidere X
 *
 *  Copyright (C) TwidereProject and Contributors
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

import androidx.paging.cachedIn
import androidx.paging.filter
import com.twidere.services.microblog.ListsService
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.ListsMode
import com.twidere.twiderex.model.ui.UiList
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.ListsRepository
import com.twidere.twiderex.utils.notifyError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class ListsViewModel(
    private val listsRepository: ListsRepository,
    private val accountRepository: AccountRepository,
) : ViewModel() {
    private val account by lazy {
        accountRepository.activeAccount.mapNotNull { it }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val source by lazy {
        account.mapNotNull { it }.flatMapLatest { account ->
            listsRepository.fetchLists(
                accountKey = account.accountKey,
                service = account.service as ListsService
            )
        }.cachedIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val ownerSource by lazy {
        account.mapNotNull { it }.flatMapLatest { account ->
            source.map {
                it.filter { it.isOwner(account.user.userId) }
            }
        }.cachedIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val subscribedSource by lazy {
        account.mapNotNull { it }.flatMapLatest { account ->
            source.map { pagingData ->
                pagingData.filter { !it.isOwner(account.user.userId) && it.isFollowed }
            }
        }.cachedIn(viewModelScope)
    }
}

abstract class ListsOperatorViewModel(
    protected val inAppNotification: InAppNotification,
) : ViewModel() {
    val modifySuccess = MutableStateFlow(false)
    val loading = MutableStateFlow(false)

    protected fun loadingRequest(
        onResult: (success: Boolean, list: UiList?) -> Unit,
        request: suspend () -> UiList?
    ) {
        loading.value = true
        viewModelScope.launch {
            runCatching {
                val result = request()
                modifySuccess.value = true
                onResult(true, result)
            }.onFailure {
                inAppNotification.notifyError(it)
                modifySuccess.value = false
                onResult(false, null)
                loading.value = false
            }.onSuccess {
                loading.value = false
            }
        }
    }
}

class ListsCreateViewModel(
    inAppNotification: InAppNotification,
    private val listsRepository: ListsRepository,
    private val accountRepository: AccountRepository,
) : ListsOperatorViewModel(inAppNotification) {
    private val account by lazy {
        accountRepository.activeAccount.mapNotNull { it }
    }

    suspend fun createList(
        title: String,
        description: String? = null,
        private: Boolean = false
    ): UiList? {
        loading.value = true
        return try {
            account.firstOrNull()?.let { account ->
                listsRepository.createLists(
                    accountKey = account.accountKey,
                    service = account.service as ListsService,
                    title = title,
                    description = description,
                    mode = if (private) ListsMode.PRIVATE.value else ListsMode.PUBLIC.value
                )
            }.let {
                modifySuccess.value = true
                it
            }
        } catch (e: Throwable) {
            inAppNotification.notifyError(e)
            modifySuccess.value = false
            null
        } finally {
            loading.value = false
        }
    }
}

class ListsModifyViewModel(
    private val listsRepository: ListsRepository,
    inAppNotification: InAppNotification,
    private val accountRepository: AccountRepository,
    private val listKey: MicroBlogKey,
) : ListsOperatorViewModel(inAppNotification) {
    private val account by lazy {
        accountRepository.activeAccount.mapNotNull { it }
    }

    val editName = MutableStateFlow("")
    var editDesc = MutableStateFlow("")
    var editPrivate = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val source by lazy {
        account.flatMapLatest { account ->
            listsRepository.findListWithListKey(
                accountKey = account.accountKey,
                listKey = listKey
            )
        }
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
            account.firstOrNull()?.let { account ->
                listsRepository.updateLists(
                    accountKey = account.accountKey,
                    service = account.service as ListsService,
                    listId = listId,
                    title = title,
                    description = description,
                    mode = if (private) ListsMode.PRIVATE.value else ListsMode.PUBLIC.value
                )
            }
        }
    }

    fun deleteList(
        listId: String = this.listKey.id,
        listKey: MicroBlogKey = this.listKey,
        onResult: (success: Boolean, list: UiList?) -> Unit = { _, _ -> }
    ) {
        loadingRequest(onResult) {
            account.firstOrNull()?.let { account ->
                listsRepository.deleteLists(
                    accountKey = account.accountKey,
                    service = account.service as ListsService,
                    listKey = listKey,
                    listId = listId,
                )
            }
        }
    }

    fun subscribeList(
        listKey: MicroBlogKey = this.listKey,
        onResult: (success: Boolean, list: UiList?) -> Unit = { _, _ -> }
    ) {
        loadingRequest(onResult) {
            account.firstOrNull()?.let { account ->
                listsRepository.subscribeLists(
                    accountKey = account.accountKey,
                    service = account.service as ListsService,
                    listKey = listKey
                )
            }
        }
    }

    fun unsubscribeList(
        listKey: MicroBlogKey = this.listKey,
        onResult: (success: Boolean, list: UiList?) -> Unit = { _, _ -> }
    ) {
        loadingRequest(onResult) {
            account.firstOrNull()?.let { account ->
                listsRepository.unsubscribeLists(
                    accountKey = account.accountKey,
                    service = account.service as ListsService,
                    listKey = listKey
                )
            }
        }
    }
}
