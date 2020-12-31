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
package com.twidere.twiderex.viewmodel.twitter

import android.accounts.Account
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.twidere.services.http.MicroBlogException
import com.twidere.services.twitter.TwitterOAuthService
import com.twidere.twiderex.db.mapper.toDbUser
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.PlatformType
import com.twidere.twiderex.model.cred.CredentialsType
import com.twidere.twiderex.model.cred.OAuthCredentials
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.repository.ACCOUNT_TYPE
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.utils.json
import java.io.IOException
import retrofit2.HttpException

class TwitterSignInViewModel @ViewModelInject constructor(
    private val repository: AccountRepository,
    private val inAppNotification: InAppNotification,
) : ViewModel() {
    val loading = MutableLiveData(false)

    suspend fun beginOAuth(
        consumerKey: String,
        consumerSecret: String,
        pinCodeProvider: suspend (url: String) -> String,
    ): Boolean {
        loading.postValue(true)
        try {
            val service = TwitterOAuthService(consumerKey, consumerSecret)
            val token = service.getOAuthToken()
            val pinCode = pinCodeProvider.invoke(service.getWebOAuthUrl(token))
            if (pinCode.isNotEmpty()) {
                val accessToken = service.getAccessToken(pinCode, token)
                val user = service.verifyCredentials(accessToken)
                if (user != null) {
                    val name = user.screenName
                    val id = user.idStr
                    if (name != null && id != null) {
                        val displayKey = MicroBlogKey.twitter(name)
                        val internalKey = MicroBlogKey.twitter(id)
                        val credentials_json = OAuthCredentials(
                            consumer_key = consumerKey,
                            consumer_secret = consumerSecret,
                            access_token = accessToken.oauth_token,
                            access_token_secret = accessToken.oauth_token_secret,
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
                                    type = PlatformType.Twitter,
                                    accountKey = internalKey,
                                    credentials_type = CredentialsType.OAuth,
                                    credentials_json = credentials_json,
                                    extras_json = "",
                                    user = user.toDbUser(),
                                    lastActive = System.currentTimeMillis()
                                )
                            )
                        }
                        return true
                    }
                }
            }
        } catch (e: MicroBlogException) {
            e.microBlogErrorMessage?.let { inAppNotification.show(it) }
        } catch (e: IOException) {
            e.message?.let { inAppNotification.show(it) }
        } catch (e: HttpException) {
            e.message?.let { inAppNotification.show(it) }
        }
        loading.postValue(false)
        return false
    }

    fun cancel() {
        loading.postValue(false)
    }
}
