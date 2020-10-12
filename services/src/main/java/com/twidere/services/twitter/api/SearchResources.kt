/*
 *  TwidereX
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of TwidereX.
 * 
 *  TwidereX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  TwidereX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with TwidereX. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.services.twitter.api

import com.twidere.services.twitter.model.TwitterSearchResponseV2
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
}
