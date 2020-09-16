package com.twidere.twiderex.utils

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.twidere.twiderex.model.adapter.AndroidAccountAdapter

inline fun <reified T> T.json(): String =
    Moshi.Builder()
        .add(AndroidAccountAdapter())
        .add(KotlinJsonAdapterFactory())
        .build().adapter<T>(T::class.java).toJson(this)

inline fun <reified T> String.fromJson() =
    Moshi.Builder()
        .add(AndroidAccountAdapter())
        .add(KotlinJsonAdapterFactory())
        .build().adapter<T>(T::class.java).fromJson(this)