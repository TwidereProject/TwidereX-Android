package com.twidere.services.http

import com.twidere.services.http.authorization.Authorization
import okhttp3.Interceptor
import okhttp3.Response

internal class AuthorizationInterceptor(private val authorization: Authorization) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = if (authorization.hasAuthorization) {
            chain.request().let {
                it.newBuilder().addHeader("Authorization", authorization.getAuthorizationHeader(it))
            }.build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}