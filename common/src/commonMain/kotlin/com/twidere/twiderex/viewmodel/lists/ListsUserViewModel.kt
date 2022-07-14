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

import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.twidere.services.microblog.ListsService
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.ListsUsersRepository
import com.twidere.twiderex.viewmodel.user.UserListViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.viewModelScope

class ListsUserViewModel(
    private val listsUsersRepository: ListsUsersRepository,
    private val accountRepository: AccountRepository,
    private val listId: String,
    private val viewMembers: Boolean = true,
) : UserListViewModel() {
    private val account by lazy {
        accountRepository.activeAccount.mapNotNull { it }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val members by lazy {
        account.flatMapLatest { account ->
            listsUsersRepository.fetchMembers(
                accountKey = account.accountKey,
                service = account.service as ListsService,
                listId = listId
            )
        }.cachedIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val subscribers by lazy {
        account.flatMapLatest { account ->
            listsUsersRepository.fetchSubscribers(
                accountKey = account.accountKey,
                service = account.service as ListsService,
                listId = listId
            )
        }.cachedIn(viewModelScope)
    }

    override val source: Flow<PagingData<UiUser>>
        get() {
            return if (viewMembers) members else subscribers
        }

    fun removeMember(user: UiUser) {
        try {
            viewModelScope.launch {
                account.firstOrNull()?.let { account ->
                    listsUsersRepository.removeMember(
                        service = account.service as ListsService,
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
