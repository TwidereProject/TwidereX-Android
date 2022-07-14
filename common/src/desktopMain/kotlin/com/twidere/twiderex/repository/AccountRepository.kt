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
package com.twidere.twiderex.repository

import com.twidere.twiderex.dataprovider.mapper.toAmUser
import com.twidere.twiderex.db.sqldelight.transform.toDbAccount
import com.twidere.twiderex.db.sqldelight.transform.toUi
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.AccountPreferences
import com.twidere.twiderex.model.AccountPreferencesFactory
import com.twidere.twiderex.model.AmUser
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.TwidereAccount
import com.twidere.twiderex.model.cred.CredentialsType
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.sqldelight.table.AccountQueries
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

actual class AccountRepository(
    private val accountQueries: AccountQueries,
    private val preferencesFactory: AccountPreferencesFactory,
) {
    private val preferencesCache = linkedMapOf<MicroBlogKey, AccountPreferences>()
    private val _accounts = MutableStateFlow(getAccounts())
    private val _activeAccount = MutableStateFlow(
        getCurrentAccount()
    )
    actual val activeAccount: Flow<AccountDetails?>
        get() = _activeAccount
    actual val accounts: Flow<List<AccountDetails>>
        get() = _accounts

    actual fun updateAccount(user: UiUser) {
        findByAccountKey(user.userKey)?.copy(
            user = user.toAmUser()
        )?.let {
            updateAccount(it)
        }
    }

    actual fun updateAccount(detail: AccountDetails) {
        accountQueries.insert(detail.toDbAccount())
        _activeAccount.value = getCurrentAccount()
        _accounts.value = getAccounts()
    }

    actual fun getAccounts(): List<AccountDetails> {
        return accountQueries.findAll().executeAsList().map { account -> account.toUi(getAccountPreferences(account.accountKey)) }
    }

    private fun getCurrentAccount(): AccountDetails? {
        return getAccounts().maxByOrNull { it.lastActive }
    }

    actual fun hasAccount(): Boolean {
        return getAccounts().isNotEmpty()
    }

    actual fun findByAccountKey(accountKey: MicroBlogKey): AccountDetails? {
        return accountQueries.findWithAccountKey(accountKey = accountKey).executeAsOneOrNull()?.let {
            it.toUi(getAccountPreferences(it.accountKey))
        }
    }

    actual fun setCurrentAccount(detail: AccountDetails) {
        detail.lastActive = System.currentTimeMillis()
        updateAccount(detail)
    }

    actual fun addAccount(
        displayKey: MicroBlogKey,
        type: PlatformType,
        accountKey: MicroBlogKey,
        credentials_type: CredentialsType,
        credentials_json: String,
        extras_json: String,
        user: AmUser,
        lastActive: Long
    ) {
        val account = TwidereAccount(displayKey.toString(), "ACCOUNT_TYPE")
        val detail = AccountDetails(
            account = account,
            type = type,
            accountKey = accountKey,
            credentials_type = credentials_type,
            credentials_json = credentials_json,
            extras_json = extras_json,
            user = user,
            lastActive = lastActive,
            preferences = getAccountPreferences(accountKey)
        )
        setCurrentAccount(detail)
    }

    actual fun getAccountPreferences(accountKey: MicroBlogKey): AccountPreferences {
        return preferencesCache.getOrPut(accountKey) {
            preferencesFactory.create(accountKey)
        }
    }

    actual fun containsAccount(key: MicroBlogKey): Boolean {
        return findByAccountKey(key) != null
    }

    actual fun delete(detail: AccountDetails) {
        accountQueries.delete(detail.accountKey)
        preferencesCache.remove(detail.accountKey)?.close()
        _activeAccount.value = getCurrentAccount()
        _accounts.value = getAccounts()
    }

    actual fun getFirstByType(type: PlatformType): AccountDetails? {
        return _accounts.value.sortedByDescending { it.lastActive }.firstOrNull { it.type == type }
    }
}
