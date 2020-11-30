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
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.twidere.services.microblog.LookupService
import com.twidere.services.microblog.RelationshipService
import com.twidere.services.microblog.model.IRelationship
import com.twidere.twiderex.di.assisted.IAssistedFactory
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel @AssistedInject constructor(
    private val factory: UserRepository.AssistedFactory,
    @Assisted private val account: AccountDetails,
    @Assisted private val screenName: String,
    @Assisted private val host: String,
) : ViewModel() {

    @AssistedInject.Factory
    interface AssistedFactory : IAssistedFactory {
        fun create(
            account: AccountDetails,
            screenName: String,
            host: String,
        ): UserViewModel
    }

    private val repository by lazy {
        account.service.let {
            factory.create(account.accountKey, it as LookupService, it as RelationshipService)
        }
    }
    val userKey = MutableLiveData<MicroBlogKey>()
    val refreshing = MutableLiveData(false)
    val loadingRelationship = MutableLiveData(false)
    val user = liveData {
        emitSource(userKey.switchMap { repository.getUserLiveData(it) })
    }
    val relationship = MutableLiveData<IRelationship>()
    val isMe = liveData {
        emitSource(
            userKey.map {
                it == account.accountKey
            }
        )
    }

    fun refresh() = viewModelScope.launch {
        refreshing.postValue(true)
        userKey.postValue(repository.lookupUserByName(screenName).userKey)
        refreshing.postValue(false)
    }

    fun follow() = viewModelScope.launch {
        loadingRelationship.postValue(true)
        repository.follow(screenName)
        loadRelationShip()
    }

    fun unfollow() = viewModelScope.launch {
        loadingRelationship.postValue(true)
        repository.unfollow(screenName)
        loadRelationShip()
    }

    private suspend fun loadRelationShip() {
        loadingRelationship.postValue(true)
        repository.showRelationship(screenName).let {
            relationship.postValue(it)
        }
        loadingRelationship.postValue(false)
    }

    init {
        viewModelScope.launch {
            launch {
                refresh()
            }
            launch {
                loadRelationShip()
            }
        }
    }
}
