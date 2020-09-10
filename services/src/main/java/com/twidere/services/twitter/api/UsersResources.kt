package com.twidere.services.twitter.api

import com.twidere.services.twitter.model.User
import retrofit2.http.GET


interface UsersResources {
    @GET("/1.1/account/verify_credentials.json")
    suspend fun verifyCredentials(): User?
}