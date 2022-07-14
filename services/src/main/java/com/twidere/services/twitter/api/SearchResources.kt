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

import com.twidere.services.twitter.model.TwitterSearchResponseV1
import com.twidere.services.twitter.model.TwitterSearchResponseV2
import com.twidere.services.twitter.model.User
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.Date

interface SearchResources {
    @GET("/2/tweets/search/recent")
    suspend fun search(
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

    @GET("/1.1/search/tweets.json")
    suspend fun searchV1(
        @Query("q") q: String,
        @Query("include_entities") include_entities: Boolean = true,
        @Query("tweet_mode") tweet_mode: String = "extended",
        @Query("include_ext_alt_text") include_ext_alt_text: Boolean = true,
        @Query("count") count: Int = 20,
        @Query("since_id") since_id: String? = null,
        @Query("max_id") max_id: String? = null,
    ): TwitterSearchResponseV1

    @GET("/1.1/users/search.json")
    suspend fun searchUser(
        @Query("q") q: String,
        @Query("page") page: Int? = null,
        @Query("count") count: Int? = null,
    ): List<User>
}
