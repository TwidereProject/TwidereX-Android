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

import com.twidere.services.http.authorization.BearerAuthorization
import com.twidere.services.http.httpClient
import com.twidere.services.twitter.api.GuestApi
import com.twidere.services.twitter.model.guest.ActivateResponse
import com.twidere.services.twitter.model.guest.User
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header

class TwitterGuestService(
  guestToken: String,
) {
  private val guestApi by lazy {
    GuestApi(
      httpClient(
        TWITTER_BASE_URL,
        BearerAuthorization(GUEST_TOKEN_AUTHORIZATION),
      ) {
        defaultRequest {
          header("x-guest-token", guestToken)
        }
      }
    )
  }

  suspend fun userTimeline(
    userId: String,
    count: Int,
    cursor: String? = null,
  ) = guestApi.userTimeline(userId, cursor, count)

  suspend fun conversation(
    tweetId: String,
    count: Int,
    cursor: String? = null,
  ) = guestApi.conversation(tweetId, cursor, count)

  suspend fun user(
    userId: String? = null,
    screenName: String? = null,
  ): User {
    require(userId != null || screenName != null) {
      "userId or screenName must be not null"
    }
    return guestApi.user(userId = userId, screenName = screenName)
  }

  companion object {
    suspend fun getGuestToken(): ActivateResponse {
      return GuestApi(
        httpClient(
          TWITTER_BASE_URL,
          BearerAuthorization(GUEST_TOKEN_AUTHORIZATION),
        )
      ).activate()
    }
  }
}

private const val GUEST_TOKEN_AUTHORIZATION =
  "AAAAAAAAAAAAAAAAAAAAAPYXBAAAAAAACLXUNDekMxqa8h%2F40K4moUkGsoc%3DTYfbDKbT3jJPCEVnMYqilB28NHfOPqkca3qaAxGfsyKCs0wRbw"
