package com.twidere.services.twitter.api

import com.twidere.services.twitter.model.Status
import retrofit2.http.GET
import retrofit2.http.Query

interface TimelineResources {
    @GET("/1.1/statuses/home_timeline.json")
    suspend fun homeTimeline(
        @Query("count") count: Int = 20,
        @Query("since_id") since_id: String? = null,
        @Query("max_id") max_id: String? = null,
        @Query("trim_user") trim_user: Boolean? = null,
        @Query("exclude_replies") exclude_replies: Boolean? = null,
        @Query("include_entities") include_entities: Boolean? = null,
    ): List<Status>

    @GET("/1.1/statuses/mentions_timeline.json")
    suspend fun mentionsTimeline(
        @Query("count") count: Int = 20,
        @Query("since_id") since_id: String? = null,
        @Query("max_id") max_id: String? = null,
        @Query("trim_user") trim_user: Boolean? = null,
        @Query("exclude_replies") exclude_replies: Boolean? = null,
        @Query("include_entities") include_entities: Boolean? = null,
    ): List<Status>



    @GET("/1.1/statuses/user_timeline.json")
    suspend fun userTimeline(
        @Query("user_id") user_id: String,
        @Query("count") count: Int = 20,
        @Query("since_id") since_id: String? = null,
        @Query("max_id") max_id: String? = null,
        @Query("trim_user") trim_user: Boolean? = null,
        @Query("exclude_replies") exclude_replies: Boolean? = null,
        @Query("include_entities") include_entities: Boolean? = null,
        @Query("include_rts") include_rts: Boolean? = null,
    ): List<Status>
}
