package com.twidere.services.http.authorization

import okhttp3.Request
import okhttp3.RequestBody
import okio.Buffer
import okio.ByteString
import java.net.URLEncoder
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

private const val OAUTH_CONSUMER_KEY = "oauth_consumer_key"
private const val OAUTH_NONCE = "oauth_nonce"
private const val OAUTH_SIGNATURE = "oauth_signature"
private const val OAUTH_SIGNATURE_METHOD = "oauth_signature_method"
private const val OAUTH_SIGNATURE_METHOD_VALUE = "HMAC-SHA1"
private const val OAUTH_TIMESTAMP = "oauth_timestamp"
private const val OAUTH_TOKEN = "oauth_token"
private const val OAUTH_VERSION = "oauth_version"
private const val OAUTH_VERSION_VALUE = "1.0"
private val baseKeys = arrayListOf(
    OAUTH_CONSUMER_KEY,
    OAUTH_NONCE,
    OAUTH_SIGNATURE,
    OAUTH_SIGNATURE_METHOD,
    OAUTH_TIMESTAMP,
    OAUTH_TOKEN,
    OAUTH_VERSION,
)

class OAuthAuthorization(
    val consumerKey: String,
    val consumerSecret: String,
    val accessToken: String? = null,
    val accessSecret: String? = null,
) : Authorization {
    override val hasAuthorization = true

    override fun getAuthorizationHeader(request: Request): String {
        val nonce: String = UUID.randomUUID().toString()
        val timestamp: Long = System.currentTimeMillis() / 1000L

        val parameters = hashMapOf(
            OAUTH_CONSUMER_KEY to consumerKey,
            OAUTH_NONCE to nonce,
            OAUTH_SIGNATURE_METHOD to OAUTH_SIGNATURE_METHOD_VALUE,
            OAUTH_TIMESTAMP to timestamp.toString(),
            OAUTH_VERSION to OAUTH_VERSION_VALUE
        )
        accessToken?.let { parameters[OAUTH_TOKEN] = it }

        val url = request.url
        for (i in 0 until url.querySize) {
            url.queryParameterValue(i)?.let {
                parameters[url.queryParameterName(i)] = it
            }
        }

        request.body?.let { body ->
            body.asString().split('&')
                .takeIf { it.isNotEmpty() }
                ?.map { it -> it.split('=', limit = 2) }
                ?.filter { it ->
                    (it.size == 2).also { hasTwoParts ->
                        if (!hasTwoParts) throw IllegalStateException(
                            "Key with no value: ${
                                it.getOrNull(
                                    0
                                )
                            }"
                        )
                    }
                }
                ?.associate { it ->
                    val (key, value) = it
                    key to value
                }
                ?.also { it -> parameters.putAll(it) }
        }

        val method = request.method.encodeUtf8()
        val baseUrl = request.url.newBuilder().query(null).build().toString().encodeUtf8()
        val signingKey = "${consumerSecret.encodeUtf8()}&${
            accessSecret?.encodeUtf8()
                ?: ""
        }"
        val params = parameters.encodeForSignature()
        val dataToSign = "$method&$baseUrl&$params"
        parameters[OAUTH_SIGNATURE] = sign(signingKey, dataToSign).encodeUtf8()

        return "OAuth ${parameters.toHeaderFormat()}"
    }

    private fun RequestBody.asString() = Buffer().run {
        writeTo(this)
        readUtf8().replace("+", "%2B")
    }

    private fun sign(key: String, data: String): String {
        val secretKey = SecretKeySpec(key.toByteArray(), "HmacSHA1")
        val macResult = Mac.getInstance("HmacSHA1").run {
            init(secretKey)
            doFinal(data.toByteArray())
        }
        return ByteString.of(*macResult).base64()
    }

    private fun HashMap<String, String>.toHeaderFormat() =
        filter { it.key in baseKeys }
            .toList()
            .sortedBy { (key, _) -> key }
            .toMap()
            .map { "${it.key}=\"${it.value}\"" }
            .joinToString(", ")


    private fun HashMap<String, String>.encodeForSignature() =
        toList()
            .sortedBy { (key, _) -> key }
            .toMap()
            .map { "${it.key}=${it.value}" }
            .joinToString("&")
            .encodeUtf8()

    private fun String.encodeUtf8() = URLEncoder.encode(this, "UTF-8").replace("+", "%2B")
}