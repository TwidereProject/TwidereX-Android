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
package com.twidere.services.api.common

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.twidere.services.serializer.DateQueryConverterFactory
import com.twidere.services.utils.DEBUG
import com.twidere.services.utils.JSON
import kotlinx.serialization.ExperimentalSerializationApi
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

@OptIn(ExperimentalSerializationApi::class)
internal inline fun <reified T> mockRetrofit(
    baseUrl: String,
    pathConvertor: Request2AssetPathConvertor,
    vararg interceptors: (chain: Interceptor.Chain) -> Response
): T {
    return Retrofit
        .Builder()
        .baseUrl(baseUrl)
        .client(
            OkHttpClient
                .Builder()
                .addInterceptor(
                    Interceptor {
                        val content = MockApiAsset.fetchAsset(path = pathConvertor.url2Path(it.request()))
                        Response.Builder()
                            .request(it.request())
                            .code(200)
                            .protocol(Protocol.HTTP_1_1)
                            .message("api unit test")
                            .body(content.toResponseBody("application/json".toMediaType()))
                            .build()
                    }
                )
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
                        addInterceptor(it)
                    }
                }
                .build()
        )
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(JSON.asConverterFactory("application/json".toMediaType()))
        .addConverterFactory(DateQueryConverterFactory())
        .build()
        .create(T::class.java)
}
