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
package com.twidere.services.api.common

import com.twidere.services.api.mastodon.MastodonRequest2AssetPathConvertor
import com.twidere.services.api.twitter.TwitterRequest2AssetPathConvertor
import com.twidere.services.http.HttpClientFactory
import com.twidere.services.http.authorization.Authorization
import com.twidere.services.mastodon.MastodonService
import com.twidere.services.mastodon.api.MastodonResources
import com.twidere.services.twitter.TwitterService
import com.twidere.services.twitter.api.TwitterResources
import okhttp3.OkHttpClient

class MockClientFactory : HttpClientFactory {
    override fun createHttpClientBuilder(): OkHttpClient.Builder {
        TODO("Not yet implemented")
    }

    @Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
    override fun <T> createResources(clazz: Class<T>, baseUrl: String, authorization: Authorization, useCache: Boolean): T {
        return when (clazz) {
            MastodonResources::class.java -> mockRetrofit<MastodonResources>(
                "https://test.mastodon.com/",
                MastodonRequest2AssetPathConvertor()
            )
            TwitterResources::class.java -> mockRetrofit<TwitterResources>(
                "https://api.twitter.com/",
                TwitterRequest2AssetPathConvertor()
            )
            else -> throw NotImplementedError()
        } as T
    }
}

fun mockMastodonService(): MastodonService {
    return MastodonService(
        "", "",
        httpClientFactory = MockClientFactory()
    )
}

fun mockTwitterService(): TwitterService {
    return TwitterService(
        "", "", "", "",
        httpClientFactory = MockClientFactory()
    )
}
