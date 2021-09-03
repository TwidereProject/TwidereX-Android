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
package com.twidere.twiderex.component.foundation

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.LocalImageLoader
import coil.compose.rememberImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.util.CoilUtils
import com.twidere.twiderex.R
import com.twidere.twiderex.http.TwidereNetworkImageLoader
import com.twidere.twiderex.http.TwidereServiceFactory
import com.twidere.twiderex.preferences.LocalHttpConfig
import com.twidere.twiderex.ui.LocalActiveAccount

@OptIn(ExperimentalCoilApi::class)
@Composable
fun NetworkImage(
    data: Any,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    placeholder: @Composable (() -> Unit)? = null,
) {
    val painter = if (data is Painter) {
        data
    } else {
        rememberImagePainter(
            data = data,
            imageLoader = TwidereNetworkImageLoader(
                buildRealImageLoader(),
                LocalContext.current,
                LocalActiveAccount.current
            ),
            builder = {
                crossfade(true)
            },
        )
    }
    if (painter is ImagePainter && painter.state is ImagePainter.State.Loading) {
        placeholder?.invoke()
    }
    Image(
        painter = painter,
        modifier = modifier,
        contentScale = contentScale,
        contentDescription = stringResource(id = com.twidere.common.R.string.accessibility_common_network_image)
    )
}

@Composable
fun buildRealImageLoader(): ImageLoader {
    val context = LocalContext.current
    val httpConfig = LocalHttpConfig.current
    return (
        if (httpConfig.proxyConfig.enable &&
            httpConfig.proxyConfig.server.isNotEmpty()
        ) {
            LocalImageLoader.current
                .newBuilder()
                .callFactory(
                    TwidereServiceFactory.createHttpClientFactory()
                        .createHttpClientBuilder()
                        .cache(CoilUtils.createDefaultCache(context))
                        .build()
                )
                .build()
        } else {
            LocalImageLoader.current
        }
        ).newBuilder()
        .componentRegistry {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                add(ImageDecoderDecoder(context))
            } else {
                add(GifDecoder())
            }
        }
        .build()
}
