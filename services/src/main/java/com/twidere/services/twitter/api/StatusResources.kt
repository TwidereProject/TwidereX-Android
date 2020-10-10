package com.twidere.services.twitter.api

import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface StatusResources {
    @POST("/1.1/statuses/retweet/{id}.json")
    suspend fun retweet(@Path(value = "id")id: String)
    @POST("/1.1/statuses/unretweet/{id}.json")
    suspend fun unretweet(@Path(value = "id")id: String)
    @POST("/1.1/favorites/create.json")
    suspend fun like(@Query(value = "id")id: String)
    @POST("/1.1/favorites/destroy.json")
    suspend fun unlike(@Query(value = "id")id: String)
}