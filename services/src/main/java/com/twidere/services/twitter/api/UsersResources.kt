package com.twidere.services.twitter.api

import com.twidere.services.twitter.model.ProfileBanner
import com.twidere.services.twitter.model.RelationshipResponse
import com.twidere.services.twitter.model.User
import retrofit2.http.GET
import retrofit2.http.Query


interface UsersResources {
    @GET("/1.1/account/verify_credentials.json")
    suspend fun verifyCredentials(): User?

    @GET("/1.1/users/profile_banner.json")
    suspend fun profileBanners(@Query("screen_name") screenName: String): ProfileBanner

    @GET("/1.1/friendships/show.json")
    suspend fun showFriendships(@Query("target_id") targetId: String) : RelationshipResponse
}