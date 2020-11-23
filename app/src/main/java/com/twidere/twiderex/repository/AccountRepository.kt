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
package com.twidere.twiderex.repository

import android.accounts.Account
import android.accounts.AccountManager
import android.os.Build
import androidx.lifecycle.MutableLiveData
import com.twidere.twiderex.db.model.DbUser
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.PlatformType
import com.twidere.twiderex.model.UserKey
import com.twidere.twiderex.model.cred.CredentialsType
import com.twidere.twiderex.utils.fromJson
import com.twidere.twiderex.utils.json
import javax.inject.Inject
import javax.inject.Singleton

const val ACCOUNT_TYPE = "com.twidere.twiderex.account"
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

@Singleton
class AccountRepository @Inject constructor(
    private val manager: AccountManager,
) {
    val activeAccount =
        MutableLiveData<AccountDetails?>(if (hasAccount()) getCurrentAccount() else null)

    val accounts = MutableLiveData(
        getAccounts().map {
            getAccountDetails(it)
        }
    )

    fun getAccounts(): List<Account> {
        return manager.getAccountsByType(ACCOUNT_TYPE).toList()
    }

    fun hasAccount(): Boolean {
        return getAccounts().isNotEmpty()
    }

    fun findByAccountKey(userKey: UserKey): Account? {
        for (account in getAccounts()) {
            if (userKey == getAccountKey(account)) {
                return account
            }
        }
        return null
    }

    fun setCurrentAccount(detail: AccountDetails) {
        detail.lastActive = System.currentTimeMillis()
        updateAccount(detail)
        activeAccount.value = detail
    }

    private fun getCurrentAccount(): AccountDetails? {
        return getAccounts()
            .map { getAccountDetails(it) }.maxByOrNull { it.lastActive }
    }

    fun addAccount(detail: AccountDetails) {
        manager.addAccountExplicitly(detail.account, null, null)
        updateAccount(detail)
        setCurrentAccount(detail)
        accounts.postValue(
            getAccounts().map {
                getAccountDetails(it)
            }
        )
    }

    fun getAccountDetails(
        account: Account,
    ): AccountDetails {
        return AccountDetails(
            account = account,
            type = PlatformType.valueOf(manager.getUserData(account, ACCOUNT_USER_DATA_TYPE)),
            key = getAccountKey(account),
            credentials_type = CredentialsType.valueOf(
                manager.getUserData(
                    account,
                    ACCOUNT_USER_DATA_CREDS_TYPE
                )
            ),
            credentials_json = manager.peekAuthToken(account, ACCOUNT_AUTH_TOKEN_TYPE),
            extras_json = manager.getUserData(account, ACCOUNT_USER_DATA_EXTRAS),
            user = manager.getUserData(account, ACCOUNT_USER_DATA_USER).fromJson<DbUser>()!!,
            lastActive = manager.getUserData(account, ACCOUNT_USER_DATA_LAST_ACTIVE)?.toLongOrNull()
                ?: 0
        )
    }

    private fun getAccountKey(account: Account): UserKey =
        UserKey.valueOf(manager.getUserData(account, ACCOUNT_USER_DATA_KEY))

    fun containsAccount(key: UserKey): Boolean {
        return findByAccountKey(key) != null
    }

    fun updateAccount(detail: AccountDetails) {
        manager.setUserData(detail.account, ACCOUNT_USER_DATA_TYPE, detail.type.name)
        manager.setUserData(detail.account, ACCOUNT_USER_DATA_KEY, detail.key.toString())
        manager.setUserData(
            detail.account,
            ACCOUNT_USER_DATA_CREDS_TYPE,
            detail.credentials_type.name
        )
        manager.setAuthToken(detail.account, ACCOUNT_AUTH_TOKEN_TYPE, detail.credentials_json)
        manager.setUserData(detail.account, ACCOUNT_USER_DATA_EXTRAS, detail.extras_json)
        manager.setUserData(detail.account, ACCOUNT_USER_DATA_USER, detail.user.json())
        manager.setUserData(
            detail.account,
            ACCOUNT_USER_DATA_LAST_ACTIVE,
            detail.lastActive.toString()
        )
    }

    fun delete(detail: AccountDetails) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            manager.removeAccountExplicitly(detail.account)
            accounts.value = getAccounts().map {
                getAccountDetails(it)
            }
            activeAccount.value = getCurrentAccount()
        }
    }
}
