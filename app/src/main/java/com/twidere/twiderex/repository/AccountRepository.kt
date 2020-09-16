package com.twidere.twiderex.repository

import android.accounts.Account
import android.accounts.AccountManager
import android.content.SharedPreferences
import androidx.core.content.edit
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.PlatformType
import com.twidere.twiderex.model.UserKey
import com.twidere.twiderex.model.cred.CredentialsType
import javax.inject.Inject
import javax.inject.Singleton


const val ACCOUNT_TYPE ="com.twidere.twiderex.account"
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

private const val PREFERENCE_CURRENT_ACTIVE_ACCOUNT = "current_active_account"

@Singleton
class AccountRepository @Inject constructor(
    private val manager: AccountManager,
    private val sharedPreferences: SharedPreferences,
) {
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
        sharedPreferences.edit {
            putString(PREFERENCE_CURRENT_ACTIVE_ACCOUNT, detail.key.toString())
        }
    }

    fun getCurrentAccount(): AccountDetails {
        return if (sharedPreferences.contains(PREFERENCE_CURRENT_ACTIVE_ACCOUNT)) {
            sharedPreferences.getString(PREFERENCE_CURRENT_ACTIVE_ACCOUNT, "")
                ?.takeIf {
                    it.isNotEmpty()
                }?.let {
                    UserKey.valueOf(it)
                }?.let {
                    findByAccountKey(it)
                }?.let {
                    getAccountDetails(it)
                } ?: getAccountDetails(getAccounts().first())
        } else {
            getAccountDetails(getAccounts().first())
        }
    }

    fun addAccount(detail: AccountDetails) {
        manager.addAccountExplicitly(detail.account, null, null)
        updateAccount(detail)
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
        )
    }

    private fun getAccountKey(account: Account): UserKey =
        UserKey.valueOf(manager.getUserData(account, ACCOUNT_USER_DATA_KEY))

    fun containsAccount(key: UserKey) : Boolean {
        return findByAccountKey(key) != null;
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
    }
}
