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
import coil.compose.rememberImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.size.OriginalSize
import coil.transform.BlurTransformation
import coil.util.CoilUtils
import com.twidere.services.http.authorization.Authorization
import com.twidere.services.http.config.HttpConfig
import com.twidere.twiderex.component.foundation.NetworkImageState
import com.twidere.twiderex.component.image.ImageEffects
import com.twidere.twiderex.http.TwidereServiceFactory
import com.twidere.twiderex.preferences.LocalHttpConfig
import okhttp3.Cache
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

            override fun onError(request: ImageRequest, throwable: Throwable) {
                onImageStateChanged(NetworkImageState.ERROR)
            }

            override fun onSuccess(request: ImageRequest, metadata: ImageResult.Metadata) {
                onImageStateChanged(NetworkImageState.SUCCESS)
            }
        }
    }
    return rememberImagePainter(
        data = data,
        imageLoader = buildImageLoader(cacheDir),
        builder = {
            size(OriginalSize)
            crossfade(effects.crossFade)
            if (effects.blur != null) {
                transformations(
                    BlurTransformation(
                        context = context,
                        radius = effects.blur.blurRadius,
                        sampling = effects.blur.bitmapScale
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
                callFactory(
                    TwidereServiceFactory.createHttpClientFactory()
                        .createHttpClientBuilder()
                        .cache(Cache(File(cacheDir), CoilUtils.createDefaultCache(context).maxSize()))
                        .build()
                )
            }
        }.componentRegistry {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                add(ImageDecoderDecoder(context))
            } else {
                add(GifDecoder())
            }
        }
        .build()
}
