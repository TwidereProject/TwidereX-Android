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
package com.twidere.twiderex.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.twidere.services.microblog.LookupService
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.db.mapper.toDbUser
import com.twidere.twiderex.db.model.DbUser
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.toAmUser
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.model.ui.UiUser.Companion.toUi
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class UserRepository @AssistedInject constructor(
    private val database: CacheDatabase,
    private val accountRepository: AccountRepository,
    @Assisted private val accountKey: MicroBlogKey,
    @Assisted private val lookupService: LookupService,
) {
    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(
            accountKey: MicroBlogKey,
            lookupService: LookupService,
        ): UserRepository
    }

    suspend fun lookupUserByName(name: String): UiUser {
        val user = lookupService.lookupUserByName(name).toDbUser(accountKey)
        saveUser(user)
        return user.toUi()
    }

    suspend fun lookupUserById(id: String): UiUser {
        val user = lookupService.lookupUser(id).toDbUser(accountKey)
        saveUser(user)
        return user.toUi()
    }

    suspend fun lookupUsersByName(name: List<String>): List<UiUser> {
        return lookupService.lookupUsersByName(name = name).map { it.toDbUser(accountKey).toUi() }
    }

    fun getUserLiveData(userKey: MicroBlogKey): LiveData<UiUser?> {
        return database.userDao().findWithUserKeyLiveData(userKey = userKey).map {
            it?.toUi()
        }
    }

    private suspend fun saveUser(user: DbUser) {
        database.userDao().insertAll(listOf(user))
        accountRepository.findByAccountKey(user.userKey)?.let {
            accountRepository.getAccountDetails(it)
        }?.let { details ->
            user.let {
                details.user = it.toAmUser()
                accountRepository.updateAccount(details)
            }
        }
    }
}
