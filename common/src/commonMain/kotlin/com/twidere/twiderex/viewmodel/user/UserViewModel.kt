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
package com.twidere.twiderex.viewmodel.user

import com.twidere.services.microblog.LookupService
import com.twidere.services.microblog.RelationshipService
import com.twidere.services.microblog.model.IRelationship
import com.twidere.twiderex.extensions.asStateIn
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.UserRepository
import com.twidere.twiderex.utils.notifyError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class UserViewModel(
    private val repository: UserRepository,
    private val accountRepository: AccountRepository,
    private val inAppNotification: InAppNotification,
    private val userKey: MicroBlogKey,
) : ViewModel() {

    private val account by lazy {
        accountRepository.activeAccount.asStateIn(viewModelScope, null)
    }

    val refreshing = MutableStateFlow(false)
    val loadingRelationship = MutableStateFlow(false)
    val user = repository.getUserFlow(userKey)
    val relationship = MutableStateFlow<IRelationship?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val isMe by lazy {
        account.transformLatest {
            it?.let {
                emit(it.accountKey == userKey)
            }
        }.asStateIn(viewModelScope, false)
    }

    fun refresh() = viewModelScope.launch {
        refreshing.value = true
        val account = account.lastOrNull() ?: return@launch
        runCatching {
            repository.lookupUserById(
                userKey.id,
                accountKey = account.accountKey,
                lookupService = account.service as LookupService,
            )
        }.onFailure {
            inAppNotification.notifyError(it)
        }
        refreshing.value = false
    }

    fun follow() = viewModelScope.launch {
        loadingRelationship.value = true
        val account = account.lastOrNull() ?: return@launch
        val relationshipService = account.service as? RelationshipService ?: return@launch
        runCatching {
            relationshipService.follow(userKey.id)
        }.onSuccess {
            loadRelationShip()
        }.onFailure {
            loadingRelationship.value = false
            inAppNotification.notifyError(it)
        }
    }

    fun unfollow() = viewModelScope.launch {
        loadingRelationship.value = true
        val account = account.lastOrNull() ?: return@launch
        val relationshipService = account.service as? RelationshipService ?: return@launch
        runCatching {
            relationshipService.unfollow(userKey.id)
        }.onSuccess {
            loadRelationShip()
        }.onFailure {
            loadingRelationship.value = false
            inAppNotification.notifyError(it)
        }
    }

    private fun loadRelationShip() = viewModelScope.launch {
        loadingRelationship.value = true
        val account = account.lastOrNull() ?: return@launch
        val relationshipService = account.service as? RelationshipService ?: return@launch
        try {
            relationshipService.showRelationship(userKey.id).let {
                relationship.value = it
            }
        } catch (e: Exception) {
        }
        loadingRelationship.value = false
    }

    init {
        refresh()
        loadRelationShip()
    }
}
