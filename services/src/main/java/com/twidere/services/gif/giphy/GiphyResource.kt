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
package com.twidere.services.gif.giphy

import retrofit2.http.GET
import retrofit2.http.Query

internal interface GiphyResource {
    @GET("/v1/gifs/trending")
    suspend fun getTrending(
        @Query("api_key") apiKey: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("rating") rating: String? = null,
        @Query("random_id") random_id: String? = null,
    ): GiphyPagingResponse

    @GET("/v1/gifs/search")
    suspend fun search(
        @Query("q") query: String,
        @Query("api_key") apiKey: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("lang") lang: String,
        @Query("rating") rating: String? = null,
        @Query("random_id") random_id: String? = null,
    ): GiphyPagingResponse
}
