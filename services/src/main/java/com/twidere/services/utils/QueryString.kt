package com.twidere.services.utils

import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.decodeFromJsonElement
import java.net.URLDecoder
import java.nio.charset.Charset

inline fun <reified T> String.queryString(charset: Charset = Charsets.UTF_8) : T {
    val map = split("&")
        .map {
            it.split("=")
                .map {
                    URLDecoder.decode(it)
                }
                .let {
                    it[0] to it[1]
                }
        }
        .toMap()
    return JSON.encodeToJsonElement(MapSerializer(String.serializer(), String.serializer()), map).let {
        JSON.decodeFromJsonElement<T>(it)
    }
}
