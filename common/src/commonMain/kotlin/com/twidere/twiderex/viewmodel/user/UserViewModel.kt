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
package com.twidere.twiderex.viewmodel.user

import com.twidere.services.microblog.LookupService
import com.twidere.services.microblog.RelationshipService
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.UserRepository
import com.twidere.twiderex.utils.notifyError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.mapNotNull
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
        accountRepository.activeAccount.mapNotNull { it }
    }

    val refreshing = MutableStateFlow(false)
    val loadingRelationship = MutableStateFlow(false)
    val user = repository.getUserFlow(userKey)
    val relationship = combine(account, refreshFlow) { account, _ ->
        loadingRelationship.compareAndSet(expect = false, update = true)
        val relationshipService = account.service as RelationshipService
        try {
            relationshipService.showRelationship(userKey.id)
        } catch (e: Throwable) {
            null
        } finally {
            loadingRelationship.compareAndSet(expect = true, update = false)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val isMe by lazy {
        account.mapLatest {
            it.accountKey == userKey
        }
    }

    private fun collectUser() = combine(account, refreshFlow) { account, _ ->
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
    }.launchIn(viewModelScope)

    fun follow() = viewModelScope.launch {
        loadingRelationship.compareAndSet(expect = false, update = true)
        val account = account.firstOrNull() ?: return@launch
        val relationshipService = account.service as? RelationshipService ?: return@launch
        try {
            relationshipService.follow(userKey.id)
            refresh()
        } catch (e: Throwable) {
            inAppNotification.notifyError(e)
        } finally {
            loadingRelationship.compareAndSet(expect = true, update = false)
        }
    }

    fun unfollow() = viewModelScope.launch {
        loadingRelationship.compareAndSet(expect = false, update = true)
        val account = account.firstOrNull() ?: return@launch
        val relationshipService = account.service as? RelationshipService ?: return@launch
        try {
            relationshipService.unfollow(userKey.id)
            refresh()
        } catch (e: Throwable) {
            inAppNotification.notifyError(e)
        } finally {
            loadingRelationship.compareAndSet(expect = true, update = false)
        }
    }

    fun block() = viewModelScope.launch {
        loadingRelationship.compareAndSet(expect = false, update = true)
        val account = account.firstOrNull() ?: return@launch
        val relationshipService = account.service as? RelationshipService ?: return@launch
        loadingRelationship.value = true
        try {
            relationshipService.block(id = userKey.id)
            refresh()
        } catch (e: Throwable) {
            inAppNotification.notifyError(e)
        } finally {
            loadingRelationship.compareAndSet(expect = true, update = false)
        }
    }

    fun unblock() = viewModelScope.launch {
        loadingRelationship.compareAndSet(expect = false, update = true)
        val account = account.firstOrNull() ?: return@launch
        val relationshipService = account.service as? RelationshipService ?: return@launch
        loadingRelationship.value = true
        try {
            relationshipService.unblock(id = userKey.id)
            refresh()
        } catch (e: Throwable) {
            inAppNotification.notifyError(e)
        } finally {
            loadingRelationship.compareAndSet(expect = true, update = false)
        }
    }

    fun refresh() {
        refreshFlow.value = UUID.randomUUID()
    }

    init {
        collectUser()
    }
}
