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

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import com.twidere.services.http.authorization.EmptyAuthorization
import com.twidere.twiderex.MR
import com.twidere.twiderex.component.ImageBlur
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.kmp.rememberNetworkImagePainter
import com.twidere.twiderex.preferences.LocalHttpConfig
import com.twidere.twiderex.twitterTonApiHost

@Composable
fun NetworkImage(
    data: Any,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    blur: ImageBlur? = null,
    placeholder: @Composable (() -> Unit)? = null,
) {
    val state = remember {
        mutableStateOf(NetworkImageState.LOADING)
    }
    val painter = if (data is Painter) {
        data
    } else {
        val httpConfig = LocalHttpConfig.current
        val auth = if (data is Uri && twitterTonApiHost == data.host) {
            TODO("Waiting for LocalActiveAccount")
            // (account.credentials as OAuthCredentials).let {
            //     OAuth1Authorization(
            //         consumerKey = it.consumer_key,
            //         consumerSecret = it.consumer_secret,
            //         accessToken = it.access_token,
            //         accessSecret = it.access_token_secret,
            //     )
            // }
        } else {
            EmptyAuthorization()
        }
        rememberNetworkImagePainter(
            data = data,
            httpConfig = httpConfig,
            authorization = auth,
            blur = blur,
            onImageStateChanged = {
                state.value = it
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
