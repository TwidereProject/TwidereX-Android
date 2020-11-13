package com.twidere.services.twitter.api

import com.twidere.services.twitter.model.User
import retrofit2.http.POST
import retrofit2.http.Query

interface FriendshipResources {
    @POST("/1.1/friendships/create.json")
    suspend fun follow(
        @Query(value = "screen_name") screen_name: String,
    ): User

    @POST("/1.1/friendships/destroy.json")
    suspend fun unfollow(
        @Query(value = "screen_name") screen_name: String,
    ): User
}