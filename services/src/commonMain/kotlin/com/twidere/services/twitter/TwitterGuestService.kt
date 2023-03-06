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
package com.twidere.services.twitter

import com.twidere.services.http.HttpClientFactory
import com.twidere.services.http.authorization.BearerAuthorization
import com.twidere.services.twitter.api.GuestResources
import com.twidere.services.twitter.model.guest.ActivateResponse
import com.twidere.services.twitter.model.guest.TwitterGuestResponse
import com.twidere.services.twitter.model.guest.User

class TwitterGuestService(
  private val httpClientFactory: HttpClientFactory,
) {
  private val guestResources by lazy {
    httpClientFactory.createResources(GuestResources::class.java, TWITTER_BASE_URL, BearerAuthorization(GUEST_TOKEN_AUTHORIZATION), useCache = true)
  }

  suspend fun userTimeline(
    userId: String,
    count: Int,
    cursor: String? = null,
  ): TwitterGuestResponse {
    val token = getGuestToken()
    require(token.guestToken != null)
    return guestResources.userTimeline(token.guestToken, userId, cursor, count)
  }

  suspend fun conversation(
    tweetId: String,
    count: Int,
    cursor: String? = null,
  ): TwitterGuestResponse {
    val token = getGuestToken()
    require(token.guestToken != null)
    return guestResources.conversation(token.guestToken, tweetId, cursor, count)
  }

  suspend fun user(
    userId: String? = null,
    screenName: String? = null,
  ): User {
    require(userId != null || screenName != null) {
      "userId or screenName must be not null"
    }
    val token = getGuestToken()
    require(token.guestToken != null)
    return guestResources.user(token.guestToken, userId = userId, screenName = screenName)
  }

  suspend fun getGuestToken(): ActivateResponse {
    return guestResources.activate()
  }
}

private const val GUEST_TOKEN_AUTHORIZATION =
  "AAAAAAAAAAAAAAAAAAAAAPYXBAAAAAAACLXUNDekMxqa8h%2F40K4moUkGsoc%3DTYfbDKbT3jJPCEVnMYqilB28NHfOPqkca3qaAxGfsyKCs0wRbw"
