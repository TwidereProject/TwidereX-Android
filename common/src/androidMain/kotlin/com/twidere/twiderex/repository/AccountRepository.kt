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

import android.accounts.Account
import android.accounts.AccountManager
import android.os.Build
import com.twidere.twiderex.dataprovider.mapper.toAmUser
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.AccountPreferences
import com.twidere.twiderex.model.AccountPreferencesFactory
import com.twidere.twiderex.model.AmUser
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.cred.CredentialsType
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.room.db.transform.toAndroid
import com.twidere.twiderex.room.db.transform.toTwidere
import com.twidere.twiderex.utils.fromJson
import com.twidere.twiderex.utils.json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

private const val ACCOUNT_TYPE = "com.twidere.twiderex.account"
private const val ACCOUNT_AUTH_TOKEN_TYPE = "com.twidere.twiderex.account.token"
private const val ACCOUNT_USER_DATA_KEY = "key"
private const val ACCOUNT_USER_DATA_TYPE = "type"
private const val ACCOUNT_USER_DATA_CREDS_TYPE = "creds_type"
private const val ACCOUNT_USER_DATA_ACTIVATED = "activated"
private const val ACCOUNT_USER_DATA_USER = "user"
private const val ACCOUNT_USER_DATA_EXTRAS = "extras"
private const val ACCOUNT_USER_DATA_COLOR = "color"
private const val ACCOUNT_USER_DATA_POSITION = "position"
private const val ACCOUNT_USER_DATA_TEST = "test"
private const val ACCOUNT_USER_DATA_LAST_ACTIVE = "last_active"

actual class AccountRepository(
    private val manager: AccountManager,
    private val accountPreferencesFactory: AccountPreferencesFactory,
) {
    actual fun updateAccount(user: UiUser) {
        findByAccountKey(user.userKey)?.copy(
            user = user.toAmUser()
        )?.let {
            updateAccount(it)
        }
    }

    private val preferencesCache = linkedMapOf<MicroBlogKey, AccountPreferences>()
    private val _activeAccount =
        MutableStateFlow(if (hasAccount()) getCurrentAccount() else null)

    actual val activeAccount: Flow<AccountDetails?>
        get() = _activeAccount

    private val _accounts = MutableStateFlow(
        getAccounts()
    )

    actual val accounts: Flow<List<AccountDetails>>
        get() = _accounts

    actual fun hasAccount(): Boolean {
        return getAccounts().isNotEmpty()
    }

    actual fun setCurrentAccount(detail: AccountDetails) {
        detail.lastActive = System.currentTimeMillis()
        updateAccount(detail)
        _activeAccount.value = detail
    }

    private fun getCurrentAccount(): AccountDetails? {
        return getAccounts().maxByOrNull { it.lastActive }
    }

    actual fun addAccount(
        displayKey: MicroBlogKey,
        type: PlatformType,
        accountKey: MicroBlogKey,
        credentials_type: CredentialsType,
        credentials_json: String,
        extras_json: String,
        user: AmUser,
        lastActive: Long,
    ) {
        val account = Account(displayKey.toString(), ACCOUNT_TYPE)
        manager.addAccountExplicitly(account, null, null)
        val detail = AccountDetails(
            account = account.toTwidere(),
            type = type,
            accountKey = accountKey,
            credentials_type = credentials_type,
            credentials_json = credentials_json,
            extras_json = extras_json,
            user = user,
            lastActive = lastActive,
            preferences = getAccountPreferences(accountKey)
        )
        updateAccount(detail)
        setCurrentAccount(detail)
        _accounts.value = getAccounts()
    }

    actual fun getAccountPreferences(accountKey: MicroBlogKey): AccountPreferences {
        return preferencesCache.getOrPut(accountKey) {
            accountPreferencesFactory.create(accountKey)
        }
    }

    private fun getAccountKey(account: Account): MicroBlogKey =
        MicroBlogKey.valueOf(manager.getUserData(account, ACCOUNT_USER_DATA_KEY))

    actual fun containsAccount(key: MicroBlogKey): Boolean {
        return findByAccountKey(key) != null
    }

    actual fun updateAccount(detail: AccountDetails) {
        val account = detail.account.toAndroid()
        manager.setUserData(account, ACCOUNT_USER_DATA_TYPE, detail.type.name)
        manager.setUserData(account, ACCOUNT_USER_DATA_KEY, detail.accountKey.toString())
        manager.setUserData(
            account,
            ACCOUNT_USER_DATA_CREDS_TYPE,
            detail.credentials_type.name
        )
        manager.setAuthToken(account, ACCOUNT_AUTH_TOKEN_TYPE, detail.credentials_json)
        manager.setUserData(account, ACCOUNT_USER_DATA_EXTRAS, detail.extras_json)
        manager.setUserData(account, ACCOUNT_USER_DATA_USER, detail.user.json())
        manager.setUserData(
            account,
            ACCOUNT_USER_DATA_LAST_ACTIVE,
            detail.lastActive.toString()
        )
        _activeAccount.value = getCurrentAccount()
        _accounts.value = getAccounts()
    }

    actual fun delete(detail: AccountDetails) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            manager.removeAccountExplicitly(detail.account.toAndroid())
            _accounts.value = getAccounts()
            _activeAccount.value = getCurrentAccount()
            preferencesCache.remove(detail.accountKey)?.close()
        }
    }

    actual fun getFirstByType(type: PlatformType): AccountDetails? {
        return _accounts.value.sortedByDescending { it.lastActive }.firstOrNull { it.type == type }
    }

    actual fun getAccounts(): List<AccountDetails> {
        return manager.getAccountsByType(ACCOUNT_TYPE).map {
            getAccountDetails(it)
        }
    }

    // Note that UserKey that being used in AccountRepository is idStr@domain, not screenName@domain
    actual fun findByAccountKey(accountKey: MicroBlogKey): AccountDetails? {
        for (account in getAccounts()) {
            if (accountKey == getAccountKey(account.account.toAndroid())) {
                return account
            }
        }
        return null
    }

    private fun getAccountDetails(
        account: Account,
    ): AccountDetails {
        return AccountDetails(
            account = account.toTwidere(),
            type = PlatformType.valueOf(manager.getUserData(account, ACCOUNT_USER_DATA_TYPE)),
            accountKey = getAccountKey(account),
            credentials_type = CredentialsType.valueOf(
                manager.getUserData(
                    account,
                    ACCOUNT_USER_DATA_CREDS_TYPE
                )
            ),
            credentials_json = manager.peekAuthToken(account, ACCOUNT_AUTH_TOKEN_TYPE),
            extras_json = manager.getUserData(account, ACCOUNT_USER_DATA_EXTRAS),
            user = manager.getUserData(account, ACCOUNT_USER_DATA_USER).fromJson(),
            lastActive = manager.getUserData(account, ACCOUNT_USER_DATA_LAST_ACTIVE)?.toLongOrNull()
                ?: 0,
            preferences = getAccountPreferences(getAccountKey(account))
        )
    }
}
