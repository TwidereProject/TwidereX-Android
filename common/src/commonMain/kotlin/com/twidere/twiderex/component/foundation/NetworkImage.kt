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

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import com.twidere.services.http.authorization.EmptyAuthorization
import com.twidere.twiderex.MR
import com.twidere.twiderex.component.image.ImageEffects
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.kmp.rememberNetworkImagePainter
import com.twidere.twiderex.preferences.LocalHttpConfig
import com.twidere.twiderex.twitterTonApiHost
import java.net.MalformedURLException
import java.net.URL

@Composable
fun NetworkImage(
    data: Any,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    effects: ImageEffects.Builder.() -> Unit = { crossFade(true) },
    placeholder: @Composable (() -> Unit)? = null,
) {
    val state = remember {
        mutableStateOf(NetworkImageState.LOADING)
    }
    val painter = if (data is Painter) {
        data
    } else {
        val httpConfig = LocalHttpConfig.current
        val auth = try {
            val url = URL(data.toString())
            if (url.host == twitterTonApiHost) {
                // (account.credentials as OAuthCredentials).let {
                //     OAuth1Authorization(
                //         consumerKey = it.consumer_key,
                //         consumerSecret = it.consumer_secret,
                //         accessToken = it.access_token,
                //         accessSecret = it.access_token_secret,
                //     )
                // }
                TODO("Waiting for LocalActiveAccount")
            } else {
                EmptyAuthorization()
            }
        } catch (e: MalformedURLException) {
            EmptyAuthorization()
        }
        rememberNetworkImagePainter(
            data = data,
            httpConfig = httpConfig,
            authorization = auth,
            effects = ImageEffects.Builder().apply(effects).build(),
            onImageStateChanged = {
                if (state.value == NetworkImageState.LOADING) state.value = it
            }
        )
    }
    if (state.value == NetworkImageState.LOADING) {
        placeholder?.invoke()
    }
    Image(
        painter = painter,
        modifier = modifier,
        contentScale = contentScale,
        contentDescription = stringResource(MR.strings.accessibility_common_network_image)
    )
}

internal enum class NetworkImageState {
    LOADING,
    SUCCESS,
    ERROR
}
