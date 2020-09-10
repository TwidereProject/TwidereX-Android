package com.twidere.twiderex.viewmodel.twitter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.twidere.services.twitter.TwitterOAuthService

class TwitterSignInViewModel : ViewModel() {
    suspend fun beginOAuth(
        consumerKey: String,
        consumerSecret: String,
        pinCodeProvider: suspend (url: String) -> String,
    ) {
        val service = TwitterOAuthService(consumerKey, consumerSecret)
        val token = service.getOAuthToken()
        val pinCode = pinCodeProvider.invoke(service.getWebOAuthUrl(token))
        val accessToken = service.getAccessToken(pinCode)
        val user = service.verifyCredentials(accessToken)
    }
}