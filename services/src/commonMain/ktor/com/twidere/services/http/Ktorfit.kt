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
package com.twidere.services.http

import com.twidere.services.http.authorization.Authorization
import com.twidere.services.utils.JSON
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.headers
import io.ktor.serialization.kotlinx.json.json
import kotlin.time.Duration.Companion.seconds

fun httpClient(
  baseUrl: String,
  authorization: Authorization? = null,
  config: HttpClientConfig<*>.() -> Unit = {},
) = HttpClient(
  OkHttp.create {
    if (authorization != null) {
      addInterceptor(AuthorizationInterceptor(authorization))
    }
  }
) {
  defaultRequest {
    url(baseUrl)
    headers {
      append("Content-Type", "application/json")
    }
  }
  install(HttpTimeout) {
    connectTimeoutMillis = 30.seconds.inWholeMilliseconds
    requestTimeoutMillis = 30.seconds.inWholeMilliseconds
    socketTimeoutMillis = 30.seconds.inWholeMilliseconds
  }
  install(Logging) {
    level = LogLevel.ALL
    logger = object : Logger {
      override fun log(message: String) {
        Napier.d(tag = "HttpClient") { message }
      }
    }
  }
  install(ContentNegotiation) {
    json(JSON)
  }
  config.invoke(this)
}
