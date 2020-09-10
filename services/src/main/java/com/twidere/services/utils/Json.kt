package com.twidere.services.utils

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val JSON by lazy {
    Json {
        ignoreUnknownKeys = true
    }
}

internal inline fun <reified T> T.encodeJson(): String =
    JSON.encodeToString<T>(this)

internal inline fun <reified T> String.decodeJson(): T =
    JSON.decodeFromString<T>(this)