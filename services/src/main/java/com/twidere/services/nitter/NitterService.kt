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
package com.twidere.services.nitter

import com.twidere.services.http.HttpClientFactory
import com.twidere.services.http.MicroBlogHttpException
import com.twidere.services.nitter.model.ConversationTimeline
import com.twidere.services.nitter.model.Profile
import com.twidere.services.nitter.model.TweetNotFound
import com.twidere.services.utils.DEBUG
import com.twidere.services.utils.await
import moe.tlaster.hson.Hson
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor

class NitterService(
    private val host: String,
    private val httpClientFactory: HttpClientFactory,
) {
    suspend fun verifyInstance(screenName: String) {
        val target = "$host/$screenName"
        if (request<Profile>(target) == null) throw TweetNotFoundException()
    }

    suspend fun conversation(
        screenName: String,
        statusId: String,
        cursor: String? = null,
    ): ConversationTimeline? {
        val target = "$host/$screenName/status/$statusId".let {
            if (cursor != null) {
                it + cursor
            } else {
                it
            }
        }
        return request(target)
    }

    private suspend inline fun <reified T> request(target: String): T? {
        return httpClientFactory.createHttpClientBuilder()
            .addNetworkInterceptor {
                it.proceed(it.request()).also { response ->
                    if (response.code != 200) {
                        throw MicroBlogHttpException(response.code)
                    }
                }
            }.apply {
                if (DEBUG) {
                    addInterceptor(
                        HttpLoggingInterceptor().apply {
                            setLevel(HttpLoggingInterceptor.Level.BODY)
                        }
                    )
                }
            }
            .build()
            .newCall(
                Request
                    .Builder()
                    .url(target)
                    .get()
                    .build()
            )
            .await()
            .body
            ?.string()
            ?.also {
                val notFound = Hson.deserializeKData<TweetNotFound>(it)
                if (!notFound.notFound.isNullOrEmpty()) {
                    throw TweetNotFoundException()
                }
            }
            ?.let {
                Hson.deserializeKData(it)
            }
    }
}
