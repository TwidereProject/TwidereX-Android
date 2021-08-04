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

import com.twidere.services.microblog.LookupService
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.db.mapper.toDbUser
import com.twidere.twiderex.db.model.DbUser
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.toAmUser
import com.twidere.twiderex.model.transform.toUi
import com.twidere.twiderex.model.ui.UiUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepository(
    private val database: CacheDatabase,
    private val accountRepository: AccountRepository,
) {
    suspend fun lookupUserByName(name: String, accountKey: MicroBlogKey, lookupService: LookupService): UiUser {
        val user = lookupService.lookupUserByName(name).toDbUser(accountKey)
        saveUser(user)
        return user.toUi()
    }

    suspend fun lookupUserById(id: String, accountKey: MicroBlogKey, lookupService: LookupService): UiUser {
        val user = lookupService.lookupUser(id).toDbUser(accountKey)
        saveUser(user)
        return user.toUi()
    }

    suspend fun lookupUsersByName(name: List<String>, accountKey: MicroBlogKey, lookupService: LookupService): List<UiUser> {
        return lookupService.lookupUsersByName(name = name).map { it.toDbUser(accountKey).toUi() }
    }

    fun getUserFlow(userKey: MicroBlogKey): Flow<UiUser?> {
        return database.userDao().findWithUserKeyFlow(userKey = userKey).map {
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
