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
package com.twidere.twiderex.viewmodel.mastodon

import androidx.compose.ui.text.input.TextFieldValue
import com.twidere.services.mastodon.MastodonOAuthService
import com.twidere.twiderex.dataprovider.mapper.toAmUser
import com.twidere.twiderex.dataprovider.mapper.toUi
import com.twidere.twiderex.http.TwidereServiceFactory
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.cred.CredentialsType
import com.twidere.twiderex.model.cred.OAuth2Credentials
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.navigation.RootDeepLinks
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.utils.OAuthLauncher
import com.twidere.twiderex.utils.json
import com.twidere.twiderex.utils.notifyError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import java.net.URI

class MastodonSignInViewModel(
    private val repository: AccountRepository,
    private val inAppNotification: InAppNotification,
    private val oAuthLauncher: OAuthLauncher,
) : ViewModel() {

    val loading = MutableStateFlow(false)
    val host = MutableStateFlow(TextFieldValue())
    fun setHost(value: TextFieldValue) {
        host.value = value
    }

    fun beginOAuth(
        host: String,
        finished: (success: Boolean) -> Unit,
    ) = viewModelScope.launch {
        loading.value = true
        val realHost = runCatching {
            URI.create(host)
        }.getOrNull()?.takeIf { !it.scheme.isNullOrEmpty() }?.toString() ?: "https://$host"
        runCatching {
            val service = MastodonOAuthService(
                host = realHost,
                client_name = "Twidere X",
                website = "https://github.com/TwidereProject/TwidereX-Android",
                redirect_uri = RootDeepLinks.Callback.SignIn.Mastodon,
                httpClientFactory = TwidereServiceFactory.createHttpClientFactory()
            )
            val application = service.createApplication()
            val target = service.getWebOAuthUrl(application)
            val code = oAuthLauncher.launchOAuth(target, "code")
            if (code.isNotBlank()) {
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
                                it.credentials_json = credentials_json
                                repository.updateAccount(it)
                            }
                        } else {
                            repository.addAccount(
                                displayKey = displayKey,
                                type = PlatformType.Mastodon,
                                accountKey = internalKey,
                                credentials_type = CredentialsType.OAuth2,
                                credentials_json = credentials_json,
                                extras_json = "",
                                user = user.toUi(accountKey = internalKey).toAmUser(),
                                lastActive = System.currentTimeMillis()
                            )
                        }
                        finished.invoke(true)
                    }
                }
            }
        }.onFailure {
            inAppNotification.notifyError(it)
        }
        loading.value = false
        finished.invoke(false)
    }
}
