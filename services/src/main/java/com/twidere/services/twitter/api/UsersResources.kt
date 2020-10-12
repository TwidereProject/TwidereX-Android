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

import com.twidere.services.twitter.model.ProfileBanner
import com.twidere.services.twitter.model.RelationshipResponse
import com.twidere.services.twitter.model.User
import retrofit2.http.GET
import retrofit2.http.Query

interface UsersResources {
    @GET("/1.1/account/verify_credentials.json")
    suspend fun verifyCredentials(): User?

    @GET("/1.1/users/profile_banner.json")
    suspend fun profileBanners(@Query("screen_name") screenName: String): ProfileBanner

    @GET("/1.1/friendships/show.json")
    suspend fun showFriendships(@Query("target_id") targetId: String): RelationshipResponse
}
