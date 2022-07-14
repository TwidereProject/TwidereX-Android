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
package com.twidere.twiderex.component.foundation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import com.twidere.services.http.authorization.EmptyAuthorization
import com.twidere.services.http.authorization.OAuth1Authorization
import com.twidere.twiderex.MR
import com.twidere.twiderex.component.image.ImageEffects
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.kmp.StorageProvider
import com.twidere.twiderex.kmp.rememberNetworkImagePainter
import com.twidere.twiderex.model.cred.OAuthCredentials
import com.twidere.twiderex.preferences.LocalHttpConfig
import com.twidere.twiderex.twitterTonApiHost
import com.twidere.twiderex.ui.LocalActiveAccount
import java.net.MalformedURLException
import java.net.URL

@Composable
fun NetworkImage(
    data: Any,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    effects: ImageEffects.Builder.() -> Unit = { crossFade(true) },
    placeholder: @Composable (() -> Unit)? = null,
    zoomable: Boolean = false
) {
    val state = remember {
        mutableStateOf(NetworkImageState.LOADING)
    }
    val painter = if (data is Painter) {
        data
    } else {
        val httpConfig = LocalHttpConfig.current
        val account = LocalActiveAccount.current
        val auth = try {
            val url = URL(data.toString())
            if (url.host == twitterTonApiHost) {
                account?.let {
                    (it.credentials as OAuthCredentials).let { oauth ->
                        OAuth1Authorization(
                            consumerKey = oauth.consumer_key,
                            consumerSecret = oauth.consumer_secret,
                            accessToken = oauth.access_token,
                            accessSecret = oauth.access_token_secret,
                        )
                    }
                } ?: EmptyAuthorization()
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
            cacheDir = get<StorageProvider>().mediaCacheDir,
            onImageStateChanged = {
                if (state.value == NetworkImageState.LOADING) state.value = it
            }
        )
    }

    Box {
        val size = painter.intrinsicSize
        Image(
            painter = painter,
            modifier = if (zoomable && size != Size.Unspecified) Modifier.aspectRatio(size.width / size.height).then(modifier) else modifier,
            contentScale = contentScale,
            contentDescription = stringResource(MR.strings.accessibility_common_network_image)
        )

        if (state.value == NetworkImageState.LOADING) {
            placeholder?.invoke()
        }
    }
}

internal enum class NetworkImageState {
    LOADING,
    SUCCESS,
    ERROR
}
