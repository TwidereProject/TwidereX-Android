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
                        addInterceptor(HttpLoggingInterceptor().apply {
                            setLevel(HttpLoggingInterceptor.Level.BODY)
                        })
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
