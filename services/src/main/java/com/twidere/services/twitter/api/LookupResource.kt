package com.twidere.services.twitter.api

import com.twidere.services.twitter.model.TwitterResponseV2
import com.twidere.services.twitter.model.TwitterSearchResponseV2
import com.twidere.services.twitter.model.UserV2
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.*


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

    @GET("/2/tweets/search/recent")
    suspend fun tweets(
        @Query("query") query: String,
        @Query("max_results") max_results: Int? = null,
        @Query("next_token") next_token: String? = null,
        @Query("since_id") since_id: String? = null,
        @Query("until_id") until_id: String? = null,
        @Query("start_time") start_time: Date? = null,
        @Query("end_time") end_time: Date? = null,
        @Query("tweet.fields", encoded = true) tweetFields: String? = null,
        @Query("expansions", encoded = true) expansions: String? = null,
        @Query("media.fields", encoded = true) mediaFields: String? = null,
        @Query("place.fields", encoded = true) placeFields: String? = null,
        @Query("poll.fields", encoded = true) pollFields: String? = null,
        @Query("user.fields", encoded = true) userFields: String? = null,
    ): TwitterSearchResponseV2
}
