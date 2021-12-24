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
package com.twidere.twiderex.http

import com.twidere.services.gif.GifService
import com.twidere.services.gif.giphy.GiphyService
import com.twidere.services.http.HttpClientFactory
import com.twidere.services.http.config.HttpConfigClientFactory
import com.twidere.services.mastodon.MastodonService
import com.twidere.services.microblog.MicroBlogService
import com.twidere.services.twitter.TwitterService
import com.twidere.twiderex.BuildConfig
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.cred.Credentials
import com.twidere.twiderex.model.cred.OAuth2Credentials
import com.twidere.twiderex.model.cred.OAuthCredentials
import com.twidere.twiderex.model.enums.PlatformType

class TwidereServiceFactory(private val configProvider: TwidereHttpConfigProvider) {

    companion object {
        private var instance: TwidereServiceFactory? = null

        fun initiate(configProvider: TwidereHttpConfigProvider) {
            instance = TwidereServiceFactory(configProvider)
        }

        fun createApiService(type: PlatformType, credentials: Credentials, accountKey: MicroBlogKey): MicroBlogService {
            return instance?.let {
                when (type) {
                    PlatformType.Twitter -> {
                        credentials.let {
                            it as OAuthCredentials
                        }.let {
                            TwitterService(
                                consumer_key = it.consumer_key,
                                consumer_secret = it.consumer_secret,
                                access_token = it.access_token,
                                access_token_secret = it.access_token_secret,
                                httpClientFactory = createHttpClientFactory(),
                                accountId = accountKey.id
                            )
                        }
                    }
                    PlatformType.StatusNet -> TODO()
                    PlatformType.Fanfou -> TODO()
                    PlatformType.Mastodon ->
                        credentials.let {
                            it as OAuth2Credentials
                        }.let {
                            MastodonService(
                                accountKey.host,
                                it.access_token,
                                httpClientFactory = createHttpClientFactory()
                            )
                        }
                }
            } ?: throw Error("Factory needs to be initiate")
        }

        fun createHttpClientFactory(): HttpClientFactory {
            return instance?.let {
                HttpConfigClientFactory(it.configProvider)
            } ?: throw Error("Factory needs to be initiate")
        }

        fun createGifService(): GifService {
            return instance?.let {
                GiphyService(
                    apiKey = BuildConfig.GIPHYKEY,
                    httpClientFactory = createHttpClientFactory()
                )
            } ?: throw Error("Factory needs to be initiate")
        }
    }
}
