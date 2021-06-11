/*
 *  Twidere X
 *
 *  Copyright (C) 2020-2021 Tlaster <tlaster@outlook.com>
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

import com.twidere.services.http.MicroBlogHttpException
import com.twidere.services.nitter.model.ConversationTimeline
import com.twidere.services.utils.await
import moe.tlaster.hson.Hson
import okhttp3.OkHttpClient
import okhttp3.Request

class NitterService(
    private val host: String,
) {
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
        return OkHttpClient
            .Builder()
            .addNetworkInterceptor {
                it.proceed(it.request()).also {
                    if (it.code != 200) {
                        throw MicroBlogHttpException(it.code)
                    }
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
            ?.let {
                Hson.deserializeKData(it)
            }
    }
}
