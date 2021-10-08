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
package com.twidere.twiderex.viewmodel.twitter

import com.twidere.services.twitter.TwitterOAuthService
import com.twidere.services.twitter.TwitterService
import com.twidere.twiderex.DefaultConfig
import com.twidere.twiderex.dataprovider.mapper.toAmUser
import com.twidere.twiderex.dataprovider.mapper.toUi
import com.twidere.twiderex.http.TwidereServiceFactory
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.cred.CredentialsType
import com.twidere.twiderex.model.cred.OAuthCredentials
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.navigation.RootDeepLinksRoute
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.utils.json
import com.twidere.twiderex.utils.notifyError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

typealias OauthVerifierProvider = suspend (url: String) -> String?
typealias PinCodeProvider = suspend (url: String) -> String?
typealias OnResult = (success: Boolean) -> Unit

class TwitterSignInViewModel(
    private val repository: AccountRepository,
    private val inAppNotification: InAppNotification,
    private val consumerKey: String,
    private val consumerSecret: String,
    private val oauthVerifierProvider: OauthVerifierProvider,
    private val pinCodeProvider: PinCodeProvider,
    private val onResult: OnResult,
) : ViewModel() {

    val success = MutableStateFlow(false)
    val loading = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            val result = beginOAuth()
            onResult.invoke(result)
        }
    }

    private suspend fun beginOAuth(): Boolean {
        loading.value = true
        try {
            val service = TwitterOAuthService(
                consumerKey,
                consumerSecret,
                TwidereServiceFactory.createHttpClientFactory()
            )
            val token = service.getOAuthToken(
                if (isBuiltInKey()) {
                    RootDeepLinksRoute.Callback.SignIn.Twitter
                } else {
                    "oob"
                }
            )
            val pinCode = if (isBuiltInKey()) {
                oauthVerifierProvider.invoke(service.getWebOAuthUrl(token))
            } else {
                pinCodeProvider.invoke(service.getWebOAuthUrl(token))
            }
            if (!pinCode.isNullOrBlank()) {
                val accessToken = service.getAccessToken(pinCode, token)
                val user = (
                    TwidereServiceFactory.createApiService(
                        type = PlatformType.Twitter,
                        credentials = OAuthCredentials(
                            consumer_key = consumerKey,
                            consumer_secret = consumerSecret,
                            access_token = accessToken.oauth_token,
                            access_token_secret = accessToken.oauth_token_secret
                        ),
                    ) as TwitterService
                    ).verifyCredentials()
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
                                it.credentials_json = credentials_json
                                repository.updateAccount(it)
                            }
                        } else {
                            repository.addAccount(
                                displayKey = displayKey,
                                type = PlatformType.Twitter,
                                accountKey = internalKey,
                                credentials_type = CredentialsType.OAuth,
                                credentials_json = credentials_json,
                                extras_json = "",
                                user = user.toUi(accountKey = internalKey).toAmUser(),
                                lastActive = System.currentTimeMillis()
                            )
                        }
                        return true
                    }
                }
            }
        } catch (e: Throwable) {
            inAppNotification.notifyError(e)
        }
        loading.value = false
        return false
    }

    private fun isBuiltInKey(): Boolean {
        return consumerKey == DefaultConfig.ConsumerKey && consumerSecret == DefaultConfig.ConsumerSecret
    }

    fun cancel() {
        loading.value = false
    }
}
