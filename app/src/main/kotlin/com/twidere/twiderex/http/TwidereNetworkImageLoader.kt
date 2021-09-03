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
package com.twidere.twiderex.http

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import coil.ImageLoader
import coil.bitmap.BitmapPool
import coil.memory.MemoryCache
import coil.request.DefaultRequestOptions
import coil.request.Disposable
import coil.request.ImageRequest
import coil.request.ImageResult
import com.twidere.services.http.authorization.OAuth1Authorization
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.cred.OAuthCredentials
import com.twidere.twiderex.model.enums.PlatformType
import okhttp3.Headers
import okhttp3.Request
import java.lang.Exception
import java.net.URL

class TwidereNetworkImageLoader(
    private val realImageLoader: ImageLoader,
    private val context: Context,
    private val account: AccountDetails?
) : ImageLoader {
    private val twitterTonApiHost = "ton.twitter.com"
    override val bitmapPool: BitmapPool
        get() = realImageLoader.bitmapPool
    override val defaults: DefaultRequestOptions
        get() = realImageLoader.defaults
    override val memoryCache: MemoryCache
        get() = realImageLoader.memoryCache

    override fun enqueue(request: ImageRequest): Disposable {
        return realImageLoader.enqueue(handleRequest(request))
    }

    override suspend fun execute(request: ImageRequest): ImageResult {
        return realImageLoader.execute(handleRequest(request))
    }

    override fun newBuilder(): ImageLoader.Builder {
        return ImageLoader.Builder(context)
    }

    override fun shutdown() {
        realImageLoader.shutdown()
    }

    private fun handleRequest(request: ImageRequest): ImageRequest {
        var data = request.data
        // ton.twitter.com must be retrieved via an authenticated
        if (data is String) data = Uri.parse(data)
        return if (data is Uri && twitterTonApiHost == data.host && account?.type == PlatformType.Twitter) {
            val auth = (account.credentials as OAuthCredentials).let {
                OAuth1Authorization(
                    consumerKey = it.consumer_key,
                    consumerSecret = it.consumer_secret,
                    accessToken = it.access_token,
                    accessSecret = it.access_token_secret,
                )
            }
            request.newBuilder(
                request.context
            ).headers(
                headers = Headers.headersOf(
                    "Authorization",
                    auth.getAuthorizationHeader(Request.Builder().url(URL(data.toString())).build())
                )
            ).build()
        } else {
            if (data is Uri && context.contentResolver.getType(data)?.startsWith("video") == true) {
                data = getThumbVideo(context = context, videoUri = data) ?: data
            }
            request.newBuilder(request.context)
                .data(data)
                .build()
        }
    }

    private fun getThumbVideo(context: Context, videoUri: Uri): Bitmap? {
        var bitmap: Bitmap? = null
        var mediaMetadataRetriever: MediaMetadataRetriever? = null
        try {
            mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(context, videoUri)
            bitmap = mediaMetadataRetriever.getFrameAtTime(
                1000,
                MediaMetadataRetriever.OPTION_CLOSEST_SYNC
            )
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaMetadataRetriever?.release()
        }
        return bitmap
    }
}
