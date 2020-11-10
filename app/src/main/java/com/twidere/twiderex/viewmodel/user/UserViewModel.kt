/*
 *  Twidere X
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
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

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.twidere.services.microblog.LookupService
import com.twidere.services.microblog.RelationshipService
import com.twidere.services.microblog.model.IRelationship
import com.twidere.twiderex.di.assisted.IAssistedFactory
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.UserKey
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.UserRepository

class UserViewModel @AssistedInject constructor(
    private val factory: UserRepository.AssistedFactory,
    private val accountRepository: AccountRepository,
    @Assisted private val account: AccountDetails,
    @Assisted private val screenName: String,
) : ViewModel() {

    private var loaded = false

    @AssistedInject.Factory
    interface AssistedFactory : IAssistedFactory {
        fun create(account: AccountDetails, screenName: String): UserViewModel
    }

    private val repository by lazy {
        account.service.let {
            factory.create(it as LookupService, it as RelationshipService)
        }
    }

    val refreshing = MutableLiveData(false)
    val user = liveData {
        emitSource(repository.getUserLiveData(screenName))
    }
    val relationship = MutableLiveData<IRelationship>()
    val isMe = liveData {
        emitSource(
            user.map {
                val key = UserKey(screenName, "twitter.com")
                key == account.key
            }
        )
    }

    suspend fun refresh() {
        refreshing.postValue(true)
        val key = UserKey(screenName, "twitter.com")
        val isme = account.key == key
        val dbUser = repository.lookupUserByName(screenName)
        if (isme) {
            accountRepository.findByAccountKey(key)?.let {
                accountRepository.getAccountDetails(it)
            }?.let { details ->
                dbUser?.let {
                    details.user = it
                    accountRepository.updateAccount(details)
                }
            }
        }
        if (!isme) {
            dbUser?.userId?.let { userId ->
                repository.showRelationship(userId).let {
                    relationship.postValue(it)
                }
            }
        }
        refreshing.postValue(false)
    }

    suspend fun init() {
        if (loaded) {
            return
        }
        loaded = true
        refresh()
    }
}
