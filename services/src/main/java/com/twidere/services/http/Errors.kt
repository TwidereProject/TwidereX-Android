package com.twidere.services.http

import kotlinx.serialization.Serializable

@Serializable
data class Errors(
    val code: Int? = null,
    val message: String? = null,
    val detail: String? = null,
    val title: String? = null,
    val resource_type: String? = null,
    val parameter: String? = null,
    val value: String? = null,
    val type: String? = null,
)