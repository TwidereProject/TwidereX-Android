package com.twidere.services.twitter.api

import com.twidere.services.twitter.model.StatusV2
import com.twidere.services.twitter.model.TwitterResponseV2
import com.twidere.services.twitter.model.UserV2
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface LookupResource {
    @GET("/2/users/{id}")
    suspend fun user(
        @Path(value = "id") id: String,
        @Query("tweet.fields", encoded = true) tweetFields: String? = null,
        @Query("user.fields", encoded = true) userFields: String? = null,
    ): TwitterResponseV2<UserV2>

    @GET("/2/users/by/username/{name}")
    suspend fun userByName(
        @Path(value = "name") name: String,
        @Query("tweet.fields", encoded = true) tweetFields: String? = null,
        @Query("user.fields", encoded = true) userFields: String? = null,
    ): TwitterResponseV2<UserV2>

    @GET("/2/tweets/:id")
    suspend fun tweet(
        @Path(value = "id") id: String,
        @Query("tweet.fields", encoded = true) tweetFields: String? = null,
        @Query("expansions", encoded = true) expansions: String? = null,
        @Query("media.fields", encoded = true) mediaFields: String? = null,
        @Query("place.fields", encoded = true) placeFields: String? = null,
        @Query("poll.fields", encoded = true) pollFields: String? = null,
        @Query("user.fields", encoded = true) userFields: String? = null,
    ): TwitterResponseV2<StatusV2>
}
