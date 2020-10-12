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
