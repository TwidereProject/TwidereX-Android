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
package com.twidere.twiderex.service

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import androidx.core.os.bundleOf
import com.twidere.twiderex.navigation.RootDeepLinks

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
            response: AccountAuthenticatorResponse,
            accountType: String,
            authTokenType: String?,
            requiredFeatures: Array<String>?,
            options: Bundle?
        ): Bundle {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(RootDeepLinks.SignIn))
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
            return bundleOf(
                AccountManager.KEY_INTENT to intent,
            )
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
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(RootDeepLinks.SignIn))
                intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
                return bundleOf(
                    AccountManager.KEY_INTENT to intent,
                )
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
            response: AccountAuthenticatorResponse,
            account: Account,
            authTokenType: String,
            options: Bundle?
        ): Bundle {
            return bundleOf(AccountManager.KEY_BOOLEAN_RESULT to true)
        }
    }
}
