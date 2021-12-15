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
package com.twidere.services.mastodon.api

import com.twidere.services.mastodon.model.SearchResult
import com.twidere.services.mastodon.model.SearchType
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchResources {
    @GET("/api/v2/search")
    suspend fun searchV2(
        @Query("q") query: String,
        @Query("account_id") account_id: String? = null,
        @Query("max_id") max_id: String? = null,
        @Query("min_id") min_id: String? = null,
        @Query("type") type: SearchType? = null,
        @Query("exclude_unreviewed") exclude_unreviewed: Boolean? = null,
        @Query("resolve") resolve: Boolean? = null,
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null,
        @Query("following") following: Boolean? = null,
    ): SearchResult
}
