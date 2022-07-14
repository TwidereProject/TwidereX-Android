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

import com.twidere.services.twitter.model.ListUserResponse
import com.twidere.services.twitter.model.TwitterList
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ListsResources {
    @GET("/1.1/lists/list.json")
    suspend fun lists(
        @Query("user_id") user_id: String? = null,
        @Query("screen_name") screen_name: String? = null,
        @Query("reverse") reverse: Boolean = true,
    ): List<TwitterList>

    /**
     *You can identify a list by its slug instead of its numerical id.
     * If you decide to do so, note that you'll also have to specify
     * the list owner using the owner_id or owner_screen_name parameters.
     */
    @GET("/1.1/lists/subscribers.json")
    suspend fun listSubscribers(
        @Query("list_id") list_id: String,
        @Query("slug") slug: String? = null,
        @Query("owner_screen_name") owner_screen_name: String? = null,
        @Query("owner_id") owner_id: String? = null,
        @Query("count") count: Int = 20,
        @Query("cursor") cursor: String? = null,
        @Query("include_entities") include_entities: Boolean? = null,
        @Query("skip_status") skip_status: Boolean? = null,
    ): ListUserResponse

    /**
     * slug => same as @listSubscribers
     */
    @GET("/1.1/lists/members.json")
    suspend fun listMembers(
        @Query("list_id") list_id: String,
        @Query("slug") slug: String? = null,
        @Query("owner_screen_name") owner_screen_name: String? = null,
        @Query("owner_id") owner_id: String? = null,
        @Query("count") count: Int = 20,
        @Query("cursor") cursor: String? = null,
        @Query("include_entities") include_entities: Boolean? = null,
        @Query("skip_status") skip_status: Boolean? = null,
    ): ListUserResponse

    @POST("/1.1/lists/create.json")
    suspend fun createList(
        @Query("name") name: String,
        @Query("mode") mode: String? = null,
        @Query("description") description: String? = null,
    ): TwitterList

    /**
     * slug => same as @listSubscribers
     */
    @POST("/1.1/lists/update.json")
    suspend fun updateList(
        @Query("name") name: String? = null,
        @Query("mode") mode: String? = null,
        @Query("description") description: String? = null,
        @Query("list_id") list_id: String,
        @Query("slug") slug: String? = null,
        @Query("owner_id") owner_id: String? = null,
        @Query("owner_screen_name") owner_screen_name: String? = null,
    ): TwitterList

    /**
     * slug => same as @listSubscribers
     */
    @POST("/1.1/lists/destroy.json")
    suspend fun destroyList(
        @Query("list_id") list_id: String,
        @Query("slug") slug: String? = null,
        @Query("owner_id") owner_id: String? = null,
        @Query("owner_screen_name") owner_screen_name: String? = null,
    ): TwitterList

    /**
     * slug => same as @listSubscribers
     */
    @POST("/1.1/lists/members/create.json")
    suspend fun addMember(
        @Query("list_id") list_id: String,
        @Query("user_id") user_id: String,
        @Query("screen_name") screen_name: String,
        @Query("slug") slug: String? = null,
        @Query("owner_id") owner_id: String? = null,
        @Query("owner_screen_name") owner_screen_name: String? = null,
    ): TwitterList

    @POST("/1.1/lists/members/destroy.json")
    suspend fun removeMember(
        @Query("list_id") list_id: String,
        @Query("user_id") user_id: String,
        @Query("screen_name") screen_name: String,
        @Query("slug") slug: String? = null,
        @Query("owner_id") owner_id: String? = null,
        @Query("owner_screen_name") owner_screen_name: String? = null,
    ): TwitterList

    @POST("/1.1/lists/subscribers/destroy.json")
    suspend fun unsubscribeLists(
        @Query("list_id") list_id: String,
        @Query("slug") slug: String? = null,
        @Query("owner_id") owner_id: String? = null,
        @Query("owner_screen_name") owner_screen_name: String? = null,
    ): TwitterList

    @POST("/1.1/lists/subscribers/create.json")
    suspend fun subscribeLists(
        @Query("list_id") list_id: String,
        @Query("slug") slug: String? = null,
        @Query("owner_id") owner_id: String? = null,
        @Query("owner_screen_name") owner_screen_name: String? = null,
    ): TwitterList
}
