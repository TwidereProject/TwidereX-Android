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
package com.twidere.twiderex.utils

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.twidere.twiderex.http.TwidereServiceFactory
import io.github.reactivecircus.cache4k.Cache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.Locale

@Immutable
data class TranslationParam(
  val key: String,
  val text: String,
  val from: String = "auto",
  val to: String =
    Locale.getDefault().language.lowercase(),
)

interface TranslationState {
  data class Success(
    val result: String,
    val origin: String,
    val from: String,
    val to: String,
  ) : TranslationState
  object InProgress : TranslationState

  // is the same language
  object NoNeed : TranslationState

  interface Failed : TranslationState {
    object NetWorkError : Failed
    object DecodeError : Failed
    object NoData : Failed
  }
}

interface ITranslationRepo {
  fun translation(
    param: TranslationParam
  ): MutableState<TranslationState>
}

fun isSameLang(lang1: String?, lang2: String?): Boolean {
  return lang1?.isNotBlank() == true &&
    lang2?.isNotBlank() == true &&
    (
      lang1.trim().startsWith(lang2.trim(), ignoreCase = true) ||
        lang2.trim().startsWith(lang1.trim(), ignoreCase = true)
      )
}

fun String.isDefaultLanguage() = isSameLang(this, Locale.getDefault().language.lowercase())

class TranslationRepo : ITranslationRepo {

  private val translationClient: OkHttpClient by lazy {
    TwidereServiceFactory
      .createHttpClientFactory()
      .createHttpClientBuilder()
      .build()
  }

  private val loadingList: MutableList<String> by lazy {
    mutableListOf()
  }

  private fun isInLoading(id: String): Boolean {
    return loadingList.contains(id)
  }

  private val cache: Cache<String, MutableState<TranslationState>> by lazy {
    Cache.Builder().apply {
      maximumCacheSize(300)
    }.build()
  }

  private val json by lazy {
    Json {
      ignoreUnknownKeys = true
      isLenient = true
      coerceInputValues = true
    }
  }

  private val httpUrl by lazy {
    "https://translate.google.com/translate_a/single".toHttpUrl()
  }

  private val scope by lazy {
    CoroutineScope(Dispatchers.IO)
  }

  override fun translation(
    param: TranslationParam
  ): MutableState<TranslationState> {
    cache.get(param.key)?.let {
      return it
    }
    val params = mapOf(
      "client" to "gtx",
      "sl" to param.from,
      "tl" to param.to,
      "dt" to "t",
      "ie" to "UTF-8",
      "oe" to "UTF-8",
      "q" to param.text,
    )
    val state = mutableStateOf<TranslationState>(TranslationState.InProgress)
    if (!isInLoading(param.key)) {
      scope.launch {
        loadingList.add(param.key)
        val request = Request.Builder().apply {
          url(
            httpUrl.newBuilder().apply {
              params.forEach { (t, u) ->
                addQueryParameter(t, u)
              }
            }.build().toUrl()
          )
        }.build()
        val response = kotlin.runCatching {
          translationClient.newCall(
            request
          ).execute()
        }.getOrNull()
        if (response?.isSuccessful == true) {
          var originLanguage = ""
          runCatching {
            json.decodeFromString<JsonElement>(
              response.body?.string() ?: ""
            ).jsonArray.apply {
              lastOrNull()
                ?.jsonArray
                ?.firstOrNull()
                ?.jsonArray
                ?.firstOrNull()
                ?.jsonPrimitive
                ?.content
                ?.let {
                  originLanguage = it.lowercase()
                }
            }.firstOrNull()
              ?.jsonArray
              ?.fold(
                initial = ""
              ) { r, t ->
                r + (
                  t.jsonArray
                    .firstOrNull()
                    ?.jsonPrimitive
                    ?.content
                    ?: ""
                  )
              }
          }.onFailure {
            state.value = TranslationState.Failed.DecodeError
          }.onSuccess {
            state.value = (
              if (it.isNullOrBlank()) {
                TranslationState.Failed.NoData
              } else if (isSameLang(originLanguage, param.to)) {
                TranslationState.NoNeed
              } else {
                TranslationState.Success(
                  origin = param.text,
                  result = it,
                  from = originLanguage,
                  to = param.to,
                )
              }
              ).apply {
              cache.put(
                param.key,
                mutableStateOf(this)
              )
            }
          }
        } else {
          state.value = TranslationState.Failed.NetWorkError
        }
        loadingList.remove(param.key)
      }
    }
    return state
  }
}
