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

import com.twidere.twiderex.http.TwidereServiceFactory
import io.github.reactivecircus.cache4k.Cache
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.Locale

val languageMap by lazy {
  mapOf(
    "af" to "Afrikaans",
    "sq" to "Albanian",
    "am" to "Amharic",
    "ar" to "Arabic",
    "hy" to "Armenian",
    "az" to "Azerbaijani",
    "eu" to "Basque",
    "be" to "Belarusian",
    "bn" to "Bengali",
    "bs" to "Bosnian",
    "bg" to "Bulgarian",
    "ca" to "Catalan",
    "ceb" to "Cebuano",
    "ny" to "Chichewa",
    "zh-cn" to "Chinese Simplified",
    "zh-tw" to "Chinese Traditional",
    "co" to "Corsican",
    "hr" to "Croatian",
    "cs" to "Czech",
    "da" to "Danish",
    "nl" to "Dutch",
    "en" to "English",
    "eo" to "Esperanto",
    "et" to "Estonian",
    "tl" to "Filipino",
    "fi" to "Finnish",
    "fr" to "French",
    "fy" to "Frisian",
    "gl" to "Galician",
    "ka" to "Georgian",
    "de" to "German",
    "el" to "Greek",
    "gu" to "Gujarati",
    "ht" to "Haitian Creole",
    "ha" to "Hausa",
    "haw" to "Hawaiian",
    "iw" to "Hebrew",
    "hi" to "Hindi",
    "hmn" to "Hmong",
    "hu" to "Hungarian",
    "is" to "Icelandic",
    "ig" to "Igbo",
    "id" to "Indonesian",
    "ga" to "Irish",
    "it" to "Italian",
    "ja" to "Japanese",
    "jw" to "Javanese",
    "kn" to "Kannada",
    "kk" to "Kazakh",
    "km" to "Khmer",
    "ko" to "Korean",
    "ku" to "Kurdish (Kurmanji)",
    "ky" to "Kyrgyz",
    "lo" to "Lao",
    "la" to "Latin",
    "lv" to "Latvian",
    "lt" to "Lithuanian",
    "lb" to "Luxembourgish",
    "mk" to "Macedonian",
    "mg" to "Malagasy",
    "ms" to "Malay",
    "ml" to "Malayalam",
    "mt" to "Maltese",
    "mi" to "Maori",
    "mr" to "Marathi",
    "mn" to "Mongolian",
    "my" to "Myanmar (Burmese)",
    "ne" to "Nepali",
    "no" to "Norwegian",
    "ps" to "Pashto",
    "fa" to "Persian",
    "pl" to "Polish",
    "pt" to "Portuguese",
    "ma" to "Punjabi",
    "ro" to "Romanian",
    "ru" to "Russian",
    "sm" to "Samoan",
    "gd" to "Scots Gaelic",
    "sr" to "Serbian",
    "st" to "Sesotho",
    "sn" to "Shona",
    "sd" to "Sindhi",
    "si" to "Sinhala",
    "sk" to "Slovak",
    "sl" to "Slovenian",
    "so" to "Somali",
    "es" to "Spanish",
    "su" to "Sundanese",
    "sw" to "Swahili",
    "sv" to "Swedish",
    "tg" to "Tajik",
    "ta" to "Tamil",
    "te" to "Telugu",
    "th" to "Thai",
    "tr" to "Turkish",
    "uk" to "Ukrainian",
    "ur" to "Urdu",
    "uz" to "Uzbek",
    "vi" to "Vietnamese",
    "cy" to "Welsh",
    "xh" to "Xhosa",
    "yi" to "Yiddish",
    "yo" to "Yoruba",
    "zu" to "Zulu",
  )
}

data class TranslationParam(
  val text: String,
  val from: String = "auto",
  val to: String =
    Locale.getDefault().language.lowercase(),
)

data class TranslationResult(
  val result: String,
  val origin: String,
  val from: String,
  val to: String,
)

interface ITranslationRepo {
  suspend fun translation(
    param: TranslationParam
  ): TranslationResult?
}

class TranslationRepo : ITranslationRepo {

  private val translationClient: OkHttpClient by lazy {
    TwidereServiceFactory
      .createHttpClientFactory()
      .createHttpClientBuilder()
      .build()
  }

  private val cache: Cache<String, TranslationResult> by lazy {
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

  override suspend fun translation(
    param: TranslationParam
  ): TranslationResult? {
    cache.get(param.toString())?.let {
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
    val request = Request.Builder().apply {
      url(
        httpUrl.newBuilder().apply {
          params.forEach { (t, u) ->
            addQueryParameter(t, u)
          }
        }.build().toUrl()
      )
    }.build()
    val response = translationClient.newCall(
      request
    ).execute()
    return if (response.isSuccessful) {
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
      }.getOrNull()
        ?.takeIf {
          it.isNotBlank()
        }?.let {
          TranslationResult(
            origin = param.text,
            result = it,
            from = originLanguage,
            to = param.to,
          ).apply {
            cache.put(
              key = param.toString(),
              value = this,
            )
          }
        }
    } else null
  }
}
