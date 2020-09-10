package com.twidere.services.http.authorization

import okhttp3.Request

interface Authorization {
    val hasAuthorization: Boolean
    fun getAuthorizationHeader(request: Request): String
}

