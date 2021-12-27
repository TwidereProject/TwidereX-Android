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
package com.twidere.services.http.authorization

/*
* Copyright (C) 2015 Jake Wharton
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import okhttp3.Request
import okio.Buffer
import okio.ByteString
import java.net.URLEncoder
import java.security.SecureRandom
import java.sql.Timestamp
import java.util.Random
import java.util.TreeMap
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

private const val OAUTH_CONSUMER_KEY = "oauth_consumer_key"
private const val OAUTH_NONCE = "oauth_nonce"
private const val OAUTH_SIGNATURE = "oauth_signature"
private const val OAUTH_SIGNATURE_METHOD = "oauth_signature_method"
private const val OAUTH_SIGNATURE_METHOD_VALUE = "HMAC-SHA1"
private const val OAUTH_TIMESTAMP = "oauth_timestamp"
private const val OAUTH_ACCESS_TOKEN = "oauth_token"
private const val OAUTH_VERSION = "oauth_version"
private const val OAUTH_VERSION_VALUE = "1.0"

class OAuth1Authorization(
    private val consumerKey: String,
    private val consumerSecret: String,
    private val accessToken: String? = null,
    private val accessSecret: String? = null,
    private val random: Random = SecureRandom()
) : Authorization {
    private fun encodeUrl(value: String) =
        URLEncoder.encode(value, "UTF-8")

    override val hasAuthorization: Boolean
        get() = true

    override fun getAuthorizationHeader(request: Request): String {
        val nonce = ByteArray(32)
        random.nextBytes(nonce)
        val oauthNonce: String = ByteString.of(*nonce).base64().replace("\\W".toRegex(), "")
        val oauthTimestamp = Timestamp(System.currentTimeMillis()).time.toString().substring(0, 10)
        val consumerKeyValue = encodeUrl(consumerKey)
        val parameters = TreeMap<String, String>()
        parameters[OAUTH_CONSUMER_KEY] = consumerKeyValue
        accessToken?.let {
            parameters[OAUTH_ACCESS_TOKEN] = encodeUrl(it)
        }
        parameters[OAUTH_NONCE] = oauthNonce
        parameters[OAUTH_TIMESTAMP] = oauthTimestamp
        parameters[OAUTH_SIGNATURE_METHOD] = OAUTH_SIGNATURE_METHOD_VALUE
        parameters[OAUTH_VERSION] = OAUTH_VERSION_VALUE
        val url = request.url
        for (i in 0 until url.querySize) {
            parameters[encodeUrl(url.queryParameterName(i))] = url.queryParameterValue(i)?.let {
                encodeUrl(
                    it
                )
            }.toString()
        }
        val requestBody = request.body
        Buffer().use { body ->
            requestBody?.writeTo(body)
            if (requestBody != null && requestBody.contentLength() > 2) {
                while (!body.exhausted()) {
                    val keyEnd = body.indexOf('='.code.toByte())
                    if (keyEnd == -1L) {
                        break // throw new IllegalStateException("Key with no value: " + body.readUtf8());
                    }
                    val key = body.readUtf8(keyEnd)
                    body.skip(1) // Equals.
                    val valueEnd = body.indexOf('&'.code.toByte())
                    val value = if (valueEnd == -1L) body.readUtf8() else body.readUtf8(valueEnd)
                    if (valueEnd != -1L) {
                        body.skip(1) // Ampersand.
                    }
                    parameters[key] = value
                }
            }
        }
        return Buffer().use { base ->
            val method = request.method
            base.writeUtf8(method)
            base.writeByte('&'.code)
            base.writeUtf8(encodeUrl(request.url.newBuilder().query(null).build().toString()))
            base.writeByte('&'.code)
            var first = true
            for ((key, value) in parameters) {
                if (!first) {
                    base.writeUtf8(encodeUrl("&"))
                }
                first = false
                base.writeUtf8(encodeUrl(key))
                base.writeUtf8(encodeUrl("="))
                base.writeUtf8(encodeUrl(value.replace("+", "%20")))
            }
            val signingKey = encodeUrl(consumerSecret) + "&" + encodeUrl(
                accessSecret ?: ""
            )
            val keySpec = SecretKeySpec(signingKey.toByteArray(), "HmacSHA1")
            val mac = Mac.getInstance("HmacSHA1")
            mac.init(keySpec)
            val result = mac.doFinal(base.readByteArray())
            val signature: String = ByteString.of(*result).base64()
            (
                "OAuth $OAUTH_CONSUMER_KEY=\"$consumerKeyValue\", $OAUTH_NONCE=\"$oauthNonce\", $OAUTH_SIGNATURE=\"${
                encodeUrl(
                    signature
                )
                }\", $OAUTH_SIGNATURE_METHOD=\"$OAUTH_SIGNATURE_METHOD_VALUE\", $OAUTH_TIMESTAMP=\"$oauthTimestamp\", ${
                (
                    if (accessToken != null) "$OAUTH_ACCESS_TOKEN=\"${
                    encodeUrl(
                        accessToken
                    )
                    }\"," else ""
                    )
                } $OAUTH_VERSION=\"$OAUTH_VERSION_VALUE\""
                )
        }
    }
}
