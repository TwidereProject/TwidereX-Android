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
import com.twidere.twiderex.extensions.asStateIn
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.UserRepository
import com.twidere.twiderex.utils.notifyError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import java.util.UUID

class UserViewModel(
    private val repository: UserRepository,
    private val accountRepository: AccountRepository,
    private val inAppNotification: InAppNotification,
    private val userKey: MicroBlogKey,
) : ViewModel() {
    private val refreshFlow = MutableStateFlow(UUID.randomUUID())
    private val account by lazy {
        accountRepository.activeAccount.asStateIn(viewModelScope, null).mapNotNull { it }
    }

    val refreshing = MutableStateFlow(false)
    val loadingRelationship = MutableStateFlow(false)
    val user = repository.getUserFlow(userKey)
    val relationship = combine(account.mapNotNull { it }, refreshFlow) { account, _ ->
        loadingRelationship.value = true
        val relationshipService = account.service as RelationshipService
        try {
            relationshipService.showRelationship(userKey.id)
        } catch (e: Throwable) {
            null
        } finally {
            loadingRelationship.value = false
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val isMe by lazy {
        account.transformLatest {
            emit(it.accountKey == userKey)
        }.asStateIn(viewModelScope, false)
    }

    private fun collectUser() = viewModelScope.launch {
        combine(account.mapNotNull { it }, refreshFlow) { account, _ ->
            refreshing.value = true
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
    }

    fun follow() = viewModelScope.launch {
        loadingRelationship.value = true
        val account = account.firstOrNull() ?: return@launch
        val relationshipService = account.service as? RelationshipService ?: return@launch
        runCatching {
            relationshipService.follow(userKey.id)
        }.onSuccess {
            refresh()
        }.onFailure {
            loadingRelationship.value = false
            inAppNotification.notifyError(it)
        }
    }

    fun unfollow() = viewModelScope.launch {
        loadingRelationship.value = true
        val account = account.firstOrNull() ?: return@launch
        val relationshipService = account.service as? RelationshipService ?: return@launch
        runCatching {
            relationshipService.unfollow(userKey.id)
        }.onSuccess {
            refresh()
        }.onFailure {
            loadingRelationship.value = false
            inAppNotification.notifyError(it)
        }
    }

    fun refresh() {
        refreshFlow.value = UUID.randomUUID()
    }

    init {
        collectUser()
    }
}
