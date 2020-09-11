package com.twidere.twiderex.service

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import androidx.core.os.bundleOf


class AccountAuthenticatorService : Service() {

    private lateinit var authenticator: TwidereAccountAuthenticator

    override fun onCreate() {
        super.onCreate()
        authenticator = TwidereAccountAuthenticator(this)
    }

    override fun onBind(intent: Intent): IBinder {
        return authenticator.iBinder
    }

    private class TwidereAccountAuthenticator(val context: Context) :
        AbstractAccountAuthenticator(context) {

        override fun addAccount(
            response: AccountAuthenticatorResponse, accountType: String,
            authTokenType: String?, requiredFeatures: Array<String>?,
            options: Bundle?
        ): Bundle {
            // TODO: Launch sign in
            return bundleOf()
        }

        override fun getAuthToken(
            response: AccountAuthenticatorResponse,
            account: Account,
            authTokenType: String,
            options: Bundle?
        ): Bundle {
            val am = AccountManager.get(context)
            val authToken = am.peekAuthToken(account, authTokenType)
            if (authToken.isNullOrEmpty()) {
                // TODO: Launch sign in
            }
            return bundleOf(
                AccountManager.KEY_ACCOUNT_NAME to account.name,
                AccountManager.KEY_ACCOUNT_TYPE to account.type,
                AccountManager.KEY_AUTHTOKEN to authToken,
            )
        }

        override fun confirmCredentials(
            response: AccountAuthenticatorResponse,
            account: Account,
            options: Bundle?
        ): Bundle {
            return bundleOf(AccountManager.KEY_BOOLEAN_RESULT to true)
        }

        override fun editProperties(
            response: AccountAuthenticatorResponse,
            accountType: String
        ): Bundle {
            return bundleOf(AccountManager.KEY_BOOLEAN_RESULT to true)
        }

        override fun getAuthTokenLabel(authTokenType: String): String {
            return authTokenType
        }

        override fun hasFeatures(
            response: AccountAuthenticatorResponse,
            account: Account,
            features: Array<String>
        ): Bundle {
            return bundleOf(AccountManager.KEY_BOOLEAN_RESULT to true)
        }

        override fun updateCredentials(
            response: AccountAuthenticatorResponse, account: Account,
            authTokenType: String, options: Bundle?
        ): Bundle {
            return bundleOf(AccountManager.KEY_BOOLEAN_RESULT to true)
        }
    }

}