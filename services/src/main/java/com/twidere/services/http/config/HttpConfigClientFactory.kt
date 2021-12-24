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
package com.twidere.services.http.config

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.twidere.services.http.AuthorizationInterceptor
import com.twidere.services.http.HttpClientFactory
import com.twidere.services.http.HttpConfigProvider
import com.twidere.services.http.MicroBlogHttpException
import com.twidere.services.http.authorization.Authorization
import com.twidere.services.mastodon.api.MastodonResources
import com.twidere.services.mastodon.model.exceptions.MastodonException
import com.twidere.services.proxy.ProxyConfig
import com.twidere.services.proxy.ReverseProxyInterceptor
import com.twidere.services.serializer.DateQueryConverterFactory
import com.twidere.services.twitter.api.TwitterResources
import com.twidere.services.twitter.model.exceptions.TwitterApiException
import com.twidere.services.twitter.model.exceptions.TwitterApiExceptionV2
import com.twidere.services.utils.DEBUG
import com.twidere.services.utils.JSON
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.net.InetSocketAddress
import java.net.Proxy

class HttpConfigClientFactory(private val configProvider: HttpConfigProvider) : HttpClientFactory {
    private val resourceCache = mutableMapOf<Class<*>, Pair<*, CacheIdentifier>>()

    @OptIn(ExperimentalSerializationApi::class)
    @Suppress("UNCHECKED_CAST")
    override fun <T> createResources(
        clazz: Class<T>,
        baseUrl: String,
        authorization: Authorization,
        useCache: Boolean
    ): T {
        val cache = resourceCache[clazz]
        val cacheIdentifier = CacheIdentifier(configProvider.provideConfig(), baseUrl)
        val result: T = if (cache != null && cache.second == cacheIdentifier && useCache) {
            cache.first as T
        } else {
            val interceptors = when (clazz) {
                TwitterResources::class.java -> Interceptor { chain ->
                    val response = chain.proceed(chain.request())
                    if (!response.isSuccessful) {
                        response.body?.string()?.takeIf {
                            it.isNotEmpty()
                        }?.let { content ->
                            runCatching {
                                JSON.decodeFromString<TwitterApiException>(content)
                            }.getOrNull()?.takeIf {
                                !it.microBlogErrorMessage.isNullOrEmpty()
                            }.let {
                                it ?: runCatching {
                                    JSON.decodeFromString<TwitterApiExceptionV2>(content)
                                }.getOrNull()
                            }?.let {
                                throw it
                            } ?: throw MicroBlogHttpException(response.code)
                        } ?: throw MicroBlogHttpException(response.code)
                    } else {
                        response
                    }
                }
                MastodonResources::class.java -> Interceptor { chain ->
                    val response = chain.proceed(chain.request())
                    if (!response.isSuccessful) {
                        response.body?.string()?.takeIf {
                            it.isNotEmpty()
                        }?.let { content ->
                            runCatching {
                                JSON.decodeFromString<MastodonException>(content)
                            }.getOrNull()?.let {
                                throw it
                            } ?: throw MicroBlogHttpException(response.code)
                        } ?: throw MicroBlogHttpException(response.code)
                    } else {
                        response
                    }
                }
                else -> null
            }
            retrofit(
                clazz,
                baseUrl,
                authorization,
                createHttpClientBuilder(),
                interceptors
            )
        }
        if (useCache) {
            resourceCache[clazz] = Pair(result, cacheIdentifier)
        }
        return result
    }

    override fun createHttpClientBuilder(): OkHttpClient.Builder {
        val config = configProvider.provideConfig()
        return proxy(OkHttpClient.Builder(), config.proxyConfig)
    }

    private fun proxy(builder: OkHttpClient.Builder, proxyConfig: ProxyConfig): OkHttpClient.Builder {
        return if (proxyConfig.enable) {
            when (proxyConfig.type) {
                ProxyConfig.Type.HTTP -> {
                    if (proxyConfig.port !in (0..65535)) {
                        return builder
                    }
                    val address = InetSocketAddress.createUnresolved(
                        proxyConfig.server,
                        proxyConfig.port
                    )
                    builder.proxy(Proxy(Proxy.Type.HTTP, address))
                        .proxyAuthenticator { _, response ->
                            val b = response.request.newBuilder()
                            if (response.code == 407) {
                                if (proxyConfig.userName.isNotEmpty() &&
                                    proxyConfig.password.isNotEmpty()
                                ) {
                                    val credential = Credentials.basic(
                                        proxyConfig.userName,
                                        proxyConfig.password
                                    )
                                    b.header("Proxy-Authorization", credential)
                                }
                            }
                            b.build()
                        }
                }
                ProxyConfig.Type.REVERSE -> {
                    builder.addInterceptor(ReverseProxyInterceptor(proxyConfig.server, proxyConfig.userName, proxyConfig.password))
                }
            }
        } else {
            builder
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun <T> retrofit(
        clazz: Class<T>,
        baseUrl: String,
        authorization: Authorization,
        clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder(),
        vararg interceptors: Interceptor?
    ): T {
        return Retrofit
            .Builder()
            .baseUrl(baseUrl)
            .client(
                clientBuilder
                    .addInterceptor(AuthorizationInterceptor(authorization))
                    .apply {
                        if (DEBUG) {
                            addInterceptor(
                                HttpLoggingInterceptor().apply {
                                    setLevel(HttpLoggingInterceptor.Level.BODY)
                                }
                            )
                        }
                        addInterceptor {
                            it.proceed(
                                it.request().let { request ->
                                    request.newBuilder().url(request.url.toString().replace("%20", "+")).build()
                                }
                            )
                        }
                        interceptors.forEach {
                            it?.let { addInterceptor(it) }
                        }
                    }
                    .build()
            )
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(JSON.asConverterFactory("application/json".toMediaType()))
            .addConverterFactory(DateQueryConverterFactory())
            .build()
            .create(clazz)
    }

    private data class CacheIdentifier(
        val config: HttpConfig,
        val baseUrl: String
    ) {
        override fun equals(other: Any?): Boolean {
            if (other !is CacheIdentifier) return false
            return config.toString() == other.config.toString() &&
                baseUrl == other.baseUrl
        }

        override fun hashCode(): Int {
            var result = config.hashCode()
            result = 31 * result + baseUrl.hashCode()
            return result
        }
    }
}
