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
package com.twidere.twiderex.viewmodel.mastodon

import android.accounts.Account
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.twidere.services.mastodon.MastodonOAuthService
import com.twidere.twiderex.db.mapper.toDbUser
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.PlatformType
import com.twidere.twiderex.model.cred.CredentialsType
import com.twidere.twiderex.model.cred.OAuth2Credentials
import com.twidere.twiderex.model.toAmUser
import com.twidere.twiderex.repository.ACCOUNT_TYPE
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.scenes.mastodon.MASTODON_CALLBACK_URL
import com.twidere.twiderex.utils.json

class MastodonSignInViewModel @ViewModelInject constructor(
    private val repository: AccountRepository,
) : ViewModel() {
    val loading = MutableLiveData(false)
    val host = MutableLiveData("")
    fun setHost(value: String) {
        host.postValue(value)
    }

    suspend fun beginOAuth(
        host: String,
        codeProvider: suspend (url: String) -> String,
    ): Boolean {
        loading.postValue(true)
        val service = MastodonOAuthService(
            host = "https://$host",
            client_name = "Twidere X",
            website = "https://github.com/TwidereProject/TwidereX-Android",
            redirect_uri = MASTODON_CALLBACK_URL,
        )
        val application = service.createApplication()
        val target = service.getWebOAuthUrl(application)
        val code = codeProvider.invoke(target)
        val accessTokenResponse = service.getAccessToken(code, application)
        val accessToken = accessTokenResponse.accessToken
        if (accessToken != null) {
            val user = service.verifyCredentials(accessToken = accessToken)
            val name = user.username
            val id = user.id
            if (name != null && id != null) {
                val displayKey = MicroBlogKey(name, host = host)
                val internalKey = MicroBlogKey(id, host = host)
                val credentials_json = OAuth2Credentials(
                    access_token = accessToken
                ).json()
                if (repository.containsAccount(internalKey)) {
                    repository.findByAccountKey(internalKey)?.let {
                        repository.getAccountDetails(it)
                    }?.let {
                        it.credentials_json = credentials_json
                        repository.updateAccount(it)
                    }
                } else {
                    repository.addAccount(
                        AccountDetails(
                            account = Account(displayKey.toString(), ACCOUNT_TYPE),
                            type = PlatformType.Mastodon,
                            accountKey = internalKey,
                            credentials_type = CredentialsType.OAuth2,
                            credentials_json = credentials_json,
                            extras_json = "",
                            user = user.toDbUser(accountKey = internalKey).toAmUser(),
                            lastActive = System.currentTimeMillis()
                        )
                    )
                }
                return true
            }
        }
        loading.postValue(false)
        return false
    }
}
