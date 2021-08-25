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

import androidx.compose.runtime.mutableStateMapOf
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.twidere.twiderex.ext.asStateIn
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.ListsUsersRepository
import com.twidere.twiderex.utils.notify
import com.twidere.twiderex.viewmodel.user.UserListViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class ListsUserViewModel(
    private val listsUsersRepository: ListsUsersRepository,
    private val accountRepository: AccountRepository,
    private val listId: String,
    private val viewMembers: Boolean = true,
) : UserListViewModel() {
    private val account by lazy {
        accountRepository.activeAccount.asStateIn(viewModelScope, null)
    }

    private val members by lazy {
        account.flatMapLatest {
            it?.let { account ->
                listsUsersRepository.fetchMembers(account = account, listId = listId)
            } ?: emptyFlow()
        }.cachedIn(viewModelScope)
    }

    private val subscribers by lazy {
        account.flatMapLatest {
            it?.let { account ->
                listsUsersRepository.fetchSubscribers(account = account, listId = listId)
            } ?: emptyFlow()
        }.cachedIn(viewModelScope)
    }

    override val source: Flow<PagingData<UiUser>>
        get() {
            return if (viewMembers) members else subscribers
        }

    fun removeMember(user: UiUser) {
        try {
            viewModelScope.launch {
                account.lastOrNull()?.let { account ->
                    listsUsersRepository.removeMember(
                        account = account,
                        listId = listId,
                        user = user
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

class ListsAddMemberViewModel(
    private val listsUsersRepository: ListsUsersRepository,
    private val inAppNotification: InAppNotification,
    private val accountRepository: AccountRepository,
    private val listId: String,
) : ViewModel() {
    private val account by lazy {
        accountRepository.activeAccount.asStateIn(viewModelScope, null)
    }

    val loading = MutableStateFlow(false)
    val pendingMap = mutableStateMapOf<MicroBlogKey, UiUser>()

    fun addToOrRemove(user: UiUser) {
        if (pendingMap[user.userKey] == null) {
            loading.value = true
            loadingRequest {
                account.firstOrNull()?.let { account ->
                    listsUsersRepository.addMember(
                        listId = listId,
                        user = user,
                        account = account
                    )
                    pendingMap[user.userKey] = user
                }
            }
        } else {
            loadingRequest {
                account.firstOrNull()?.let { account ->
                    listsUsersRepository.removeMember(
                        account = account,
                        listId = listId,
                        user = user
                    )
                    pendingMap.remove(user.userKey)
                }
            }
        }
    }

    fun isInPendingList(user: UiUser): Boolean {
        return pendingMap[user.userKey] != null
    }

    private fun loadingRequest(request: suspend () -> Unit) {
        loading.value = true
        viewModelScope.launch {
            runCatching {
                request()
            }.onFailure {
                it.notify(inAppNotification)
                loading.value = false
            }.onSuccess {
                loading.value = false
            }
        }
    }
}
