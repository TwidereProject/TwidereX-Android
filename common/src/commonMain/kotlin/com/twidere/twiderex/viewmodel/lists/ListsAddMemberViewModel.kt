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

import androidx.compose.runtime.mutableStateMapOf
import com.twidere.services.microblog.ListsService
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.ListsUsersRepository
import com.twidere.twiderex.utils.notifyError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class ListsAddMemberViewModel(
    private val listsUsersRepository: ListsUsersRepository,
    private val inAppNotification: InAppNotification,
    private val accountRepository: AccountRepository,
    private val listId: String,
) : ViewModel() {
    private val account by lazy {
        accountRepository.activeAccount.mapNotNull { it }
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
                        service = account.service as ListsService,
                    )
                    pendingMap[user.userKey] = user
                }
            }
        } else {
            loadingRequest {
                account.firstOrNull()?.let { account ->
                    listsUsersRepository.removeMember(
                        service = account.service as ListsService,
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
                inAppNotification.notifyError(it)
                loading.value = false
            }.onSuccess {
                loading.value = false
            }
        }
    }
}
