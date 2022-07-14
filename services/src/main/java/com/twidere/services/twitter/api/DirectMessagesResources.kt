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

import com.twidere.services.twitter.model.DirectMessageEventObject
import com.twidere.services.twitter.model.DirectMessageResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface DirectMessagesResources {
    @POST("/1.1/direct_messages/events/new.json")
    suspend fun sendMessage(@Body event: DirectMessageEventObject): DirectMessageEventObject

    @GET("/1.1/direct_messages/events/list.json")
    suspend fun getMessages(
        @Query("cursor") cursor: String? = null,
        @Query("count") count: Int? = null // default 20, 50 max
    ): DirectMessageResponse

    @GET("/1.1/direct_messages/events/show.json")
    suspend fun showMessage(@Query("id") id: String): DirectMessageEventObject

    @DELETE("/1.1/direct_messages/events/destroy.json")
    suspend fun destroyMessage(@Query("id") id: String): Response<Unit>
}
