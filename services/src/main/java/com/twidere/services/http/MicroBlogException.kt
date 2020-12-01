package com.twidere.services.http

import java.io.IOException
import kotlinx.serialization.Serializable

@Serializable
data class MicroBlogException(
    val error: String? = null,
    val request: String? = null,
    val errors: List<Errors>? = null,
) : IOException()