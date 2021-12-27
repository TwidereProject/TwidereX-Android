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
package com.twidere.services.twitter.api

import com.twidere.services.twitter.model.Status
import com.twidere.services.twitter.model.StatusReactionsV2
import com.twidere.services.twitter.model.TwitterResponseV2
import com.twidere.services.twitter.model.request.TwitterReactionRequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface StatusResources {
    @POST("/1.1/statuses/retweet/{id}.json")
    suspend fun retweet(@Path(value = "id") id: String): Status

    @POST("/1.1/statuses/unretweet/{id}.json")
    suspend fun unretweet(@Path(value = "id") id: String): Status

    @POST("/1.1/favorites/create.json")
    suspend fun like(@Query(value = "id") id: String): Status

    @POST("/1.1/favorites/destroy.json")
    suspend fun unlike(@Query(value = "id") id: String): Status

    @POST("/1.1/statuses/update.json")
    suspend fun update(
        @Query("status") status: String,
        @Query("auto_populate_reply_metadata") auto_populate_reply_metadata: Boolean? = null,
        @Query("in_reply_to_status_id") in_reply_to_status_id: String? = null,
        @Query("repost_status_id") repost_status_id: String? = null,
        @Query("display_coordinates") display_coordinates: Boolean? = null,
        @Query("exclude_reply_user_ids") exclude_reply_user_ids: String? = null,
        @Query("lat") lat: Double? = null,
        @Query("long") long: Double? = null,
        @Query("media_ids") media_ids: String? = null,
        @Query("attachment_url") attachment_url: String? = null,
        @Query("possibly_sensitive") possibly_sensitive: Boolean? = null,
    ): Status

    @POST("/1.1/statuses/destroy/{id}.json")
    suspend fun destroy(@Path(value = "id") id: String): Status

    @POST("/2/users/{userId}/retweets")
    suspend fun retweetV2(@Path(value = "userId") userId: String, @Body body: TwitterReactionRequestBody): TwitterResponseV2<StatusReactionsV2>

    @DELETE("/2/users/{userId}/retweets/{tweetId}")
    suspend fun unRetweetV2(@Path(value = "userId") userId: String, @Path(value = "tweetId") tweetId: String): TwitterResponseV2<StatusReactionsV2>

    @POST("/2/users/{userId}/likes")
    suspend fun likeV2(@Path(value = "userId") userId: String, @Body body: TwitterReactionRequestBody): TwitterResponseV2<StatusReactionsV2>

    @DELETE("/2/users/{userId}/likes/{tweetId}")
    suspend fun unlikeV2(@Path(value = "userId") userId: String, @Path(value = "tweetId") tweetId: String): TwitterResponseV2<StatusReactionsV2>
}
