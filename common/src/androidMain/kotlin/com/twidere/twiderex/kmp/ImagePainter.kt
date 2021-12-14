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
package com.twidere.twiderex.kmp

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.compose.LocalImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.disk.DiskCache
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.size.Size
import com.twidere.services.http.authorization.Authorization
import com.twidere.services.http.config.HttpConfig
import com.twidere.twiderex.component.foundation.NetworkImageState
import com.twidere.twiderex.component.image.ImageEffects
import com.twidere.twiderex.http.TwidereServiceFactory
import com.twidere.twiderex.preferences.LocalHttpConfig
import com.twidere.twiderex.utils.BlurTransformation
import okhttp3.Request
import java.io.File
import java.net.URL

@OptIn(coil.annotation.ExperimentalCoilApi::class)
@Composable
internal actual fun rememberNetworkImagePainter(
    data: Any,
    authorization: Authorization,
    httpConfig: HttpConfig,
    effects: ImageEffects,
    cacheDir: String,
    onImageStateChanged: (NetworkImageState) -> Unit
): Painter {
    val context = LocalContext.current
    val listener = remember {
        object : ImageRequest.Listener {
            override fun onStart(request: ImageRequest) {
                onImageStateChanged(NetworkImageState.LOADING)
            }

            override fun onError(request: ImageRequest, result: ErrorResult) {
                onImageStateChanged(NetworkImageState.ERROR)
            }

            override fun onSuccess(request: ImageRequest, result: SuccessResult) {
                onImageStateChanged(NetworkImageState.SUCCESS)
            }

            override fun onCancel(request: ImageRequest) {
                onImageStateChanged(NetworkImageState.ERROR)
            }
        }
    }
    return rememberAsyncImagePainter(
        model = ImageRequest
            .Builder(context)
            .data(data)
            .apply {
                size(Size.ORIGINAL)
                crossfade(effects.crossFade)
                if (effects.blur != null) {
                    transformations(
                        BlurTransformation(
                            context = context,
                            radius = effects.blur.blurRadius,
                        )
                    )
                }
                listener(listener)
                if (authorization.hasAuthorization) {
                    addHeader(
                        "Authorization",
                        authorization.getAuthorizationHeader(
                            Request.Builder()
                                .url(URL(data.toString()))
                                .build()
                        )
                    )
                }
            }
            .build(),
        imageLoader = buildImageLoader(cacheDir)
    )
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun buildImageLoader(cacheDir: String): ImageLoader {
    val context = LocalContext.current
    val httpConfig = LocalHttpConfig.current
    return LocalImageLoader.current
        .newBuilder()
        .apply {
            if (httpConfig.proxyConfig.enable &&
                httpConfig.proxyConfig.server.isNotEmpty()
            ) {
                callFactory {
                    TwidereServiceFactory.createHttpClientFactory()
                        .createHttpClientBuilder()
                        .build()
                }
                diskCache {
                    DiskCache.Builder(context)
                        .directory(File(cacheDir))
                        .build()
                }
            }
        }.components {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()
}
