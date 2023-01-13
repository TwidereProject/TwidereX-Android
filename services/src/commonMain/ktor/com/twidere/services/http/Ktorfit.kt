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
) = HttpClient(OkHttp.create {
  if (authorization != null) {
    addInterceptor(AuthorizationInterceptor(authorization))
  }
}) {
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
