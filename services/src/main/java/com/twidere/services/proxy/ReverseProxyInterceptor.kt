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
package com.twidere.services.proxy

import com.twidere.services.utils.Base64
import okhttp3.Credentials
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import okio.IOException
import java.net.URLEncoder

class ReverseProxyInterceptor(
    private val proxyFormat: String,
    private val proxyUsername: String?,
    private val proxyPassword: String?
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url
        val builder = request.newBuilder()
        val replacedUrl = ReverseProxyHandler.replaceUrl(url, proxyFormat).toHttpUrlOrNull() ?: run {
            throw IOException("Invalid reverse proxy format")
        }
        builder.url(replacedUrl)
        if (!proxyUsername.isNullOrEmpty() && !proxyPassword.isNullOrEmpty()) {
            val credential = Credentials.basic(
                proxyUsername,
                proxyPassword
            )
            builder.addHeader("Proxy-Authorization", credential)
        }
        return chain.proceed(builder.build())
    }
}

object ReverseProxyHandler {
    private val urlSupportedPatterns = listOf(
        "[SCHEME]", "[HOST]", "[PORT]", "[AUTHORITY]",
        "[PATH]", "[/PATH]", "[PATH_ENCODED]", "[QUERY]", "[?QUERY]", "[QUERY_ENCODED]",
        "[FRAGMENT]", "[#FRAGMENT]", "[FRAGMENT_ENCODED]", "[URL_ENCODED]", "[URL_BASE64]"
    )

    /**
     * # Supported patterns
     *
     * * `[SCHEME]`: E.g. `http` or `https`
     * * `[HOST]`: Host address
     * * `[PORT]`: Port number
     * * `[AUTHORITY]`: `[HOST]`:`[PORT]` or `[HOST]` if port is default. Colon **will be** URL encoded
     * * `[PATH]`: Raw path part, **without leading slash**
     * * `[/PATH]`: Raw path part, **with leading slash**
     * * `[PATH_ENCODED]`: Path, **will be** URL encoded again
     * * `[QUERY]`: Raw query part
     * * `[?QUERY]`: Raw query part, with `?` prefix
     * * `[QUERY_ENCODED]`: Raw query part, **will be** URL encoded again
     * * `[FRAGMENT]`: Raw fragment part
     * * `[#FRAGMENT]`: Raw fragment part, with `#` prefix
     * * `[FRAGMENT_ENCODED]`: Raw fragment part, **will be** URL encoded again
     * * `[URL_ENCODED]`: URL Encoded `url` itself
     * * `[URL_BASE64]`: Base64 Encoded `url` itself
     *
     * # Null values
     * `[PATH]`, `[/PATH]`, `[QUERY]`, `[?QUERY]`, `[FRAGMENT]`, `[#FRAGMENT]` will be empty when
     * it's null, values and base64-encoded will be string `"null"`.
     *
     * A valid format looks like
     *
     * `https://proxy.com/[SCHEME]/[AUTHORITY]/[PATH][?QUERY][#FRAGMENT]`,
     *
     * A request
     *
     * `https://example.com:8080/path?query=value#fragment`
     *
     * Will be transformed to
     *
     * `https://proxy.com/https/example.com%3A8080/path?query=value#fragment`
     */
    @Suppress("KDocUnresolvedReference")
    fun replaceUrl(url: HttpUrl, format: String): String {
        val sb = StringBuffer()
        var startIndex = 0
        while (startIndex != -1) {
            val find = format.findAnyOf(urlSupportedPatterns, startIndex) ?: break
            sb.append(format, startIndex, find.first)
            sb.append(
                when (find.second) {
                    "[SCHEME]" -> url.scheme
                    "[HOST]" -> url.host
                    "[PORT]" -> url.port
                    "[AUTHORITY]" -> url.authority()
                    "[PATH]" -> url.encodedPath.removePrefix("/")
                    "[/PATH]" -> url.encodedPath
                    "[PATH_ENCODED]" -> url.encodedPath.removePrefix("/").urlEncoded()
                    "[QUERY]" -> url.encodedQuery.orEmpty()
                    "[?QUERY]" -> url.encodedQuery?.prefix("?").orEmpty()
                    "[QUERY_ENCODED]" -> url.encodedQuery?.urlEncoded()
                    "[FRAGMENT]" -> url.encodedFragment.orEmpty()
                    "[#FRAGMENT]" -> url.encodedFragment?.prefix("#").orEmpty()
                    "[FRAGMENT_ENCODED]" -> url.encodedFragment?.urlEncoded()
                    "[URL_ENCODED]" -> url.toString().urlEncoded()
                    "[URL_BASE64]" -> Base64.encodeToString(
                        url.toString().toByteArray(Charsets.UTF_8),
                        Base64.URL_SAFE
                    )
                    else -> throw AssertionError()
                }
            )
            startIndex = find.first + find.second.length
        }
        sb.append(format, startIndex, format.length)
        return sb.toString()
    }

    private fun HttpUrl.authority(): String {
        val host = host
        val port = port
        if (port == HttpUrl.defaultPort(scheme)) return host
        return "$host%3A$port"
    }

    private fun String.urlEncoded() = URLEncoder.encode(this, "utf-8")

    private fun String.prefix(prefix: String) = prefix + this
}
