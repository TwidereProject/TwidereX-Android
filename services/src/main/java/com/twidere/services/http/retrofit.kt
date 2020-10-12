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
 
package com.twidere.services.http

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.twidere.services.http.authorization.Authorization
import com.twidere.services.serializer.DateQueryConverterFactory
import com.twidere.services.utils.DEBUG
import com.twidere.services.utils.JSON
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

internal inline fun <reified T> retrofit(
    baseUrl: String,
    authorization: Authorization,
): T {
    return Retrofit
        .Builder()
        .baseUrl(baseUrl)
        .client(
            OkHttpClient
                .Builder()
                .addInterceptor(AuthorizationInterceptor(authorization))
                .apply {
                    if (DEBUG) {
                        addInterceptor(
                            HttpLoggingInterceptor().apply {
                                setLevel(HttpLoggingInterceptor.Level.BODY)
                            }
                        )
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
