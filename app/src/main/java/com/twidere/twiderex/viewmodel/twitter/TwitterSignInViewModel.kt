package com.twidere.twiderex.viewmodel.twitter

import android.accounts.Account
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.twidere.services.twitter.TwitterOAuthService
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.AccountType
import com.twidere.twiderex.model.UserKey
import com.twidere.twiderex.model.cred.CredentialsType
import com.twidere.twiderex.model.cred.OAuthCredentials
import com.twidere.twiderex.repository.ACCOUNT_TYPE
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.utils.json

class TwitterSignInViewModel @ViewModelInject constructor(
    private val repository: AccountRepository
) : ViewModel() {
    suspend fun beginOAuth(
        consumerKey: String,
        consumerSecret: String,
        pinCodeProvider: suspend (url: String) -> String,
    ) {
        val service = TwitterOAuthService(consumerKey, consumerSecret)
        val token = service.getOAuthToken()
        val pinCode = pinCodeProvider.invoke(service.getWebOAuthUrl(token))
        val accessToken = service.getAccessToken(pinCode, token)
        val user = service.verifyCredentials(accessToken)
        if (user != null) {
            val name = user.screenName
            if (name != null) {
                val key = UserKey(name, "twitter.com")
                if (repository.containsAccount(key)) {

                } else {
                    repository.addAccount(
                        AccountDetails(
                            account = Account(key.toString(), ACCOUNT_TYPE),
                            type = AccountType.Twitter,
                            key = key,
                            credentials_type = CredentialsType.OAuth,
                            credentials_json = OAuthCredentials(
                                consumer_key = consumerKey,
                                consumer_secret = consumerSecret,
                                access_token = accessToken.oauth_token,
                                access_token_secret = accessToken.oauth_token_secret,
                            ).json(),
                            extras_json = "",
                        )
                    )
                }
            }
        }
    }
}