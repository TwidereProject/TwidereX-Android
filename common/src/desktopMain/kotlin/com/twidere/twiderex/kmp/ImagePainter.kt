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
package com.twidere.twiderex.kmp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.painter.Painter
import com.twidere.services.http.authorization.Authorization
import com.twidere.services.http.config.HttpConfig
import com.twidere.services.proxy.ProxyConfig
import com.twidere.services.proxy.ReverseProxyHandler
import com.twidere.twiderex.component.foundation.NetworkImageState
import com.twidere.twiderex.component.image.ImageEffects
import com.twidere.twiderex.image.ImageCacheImpl
import com.twidere.twiderex.image.ImagePainter
import kotlinx.coroutines.Dispatchers
import okhttp3.Credentials
import okhttp3.HttpUrl
import okhttp3.Request
import java.io.IOException
import java.net.HttpURLConnection
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.URL

@Composable
internal actual fun rememberNetworkImagePainter(
    data: Any,
    authorization: Authorization,
    httpConfig: HttpConfig,
    effects: ImageEffects,
    cacheDir: String,
    onImageStateChanged: (NetworkImageState) -> Unit
): Painter {
    val scope = rememberCoroutineScope { Dispatchers.IO }
    return remember(data, effects) {
        ImagePainter(
            data,
            scope,
            imageCache = ImageCacheImpl.create(
                cacheDir = cacheDir,
            ),
            imageEffects = effects,
            httpConnection = {
                generateConnection(
                    httpConfig = httpConfig,
                    authorization = authorization,
                    url = it
                )
            },
            onImageStateChanged = onImageStateChanged
        )
    }
}

private fun generateConnection(httpConfig: HttpConfig, authorization: Authorization, url: URL): HttpURLConnection {
    val proxyConfig = httpConfig.proxyConfig
    val connection = if (proxyConfig.enable) {
        when (proxyConfig.type) {
            ProxyConfig.Type.HTTP -> {
                val proxy = if (proxyConfig.port !in (0..65535)) {
                    Proxy.NO_PROXY
                } else {
                    val address = InetSocketAddress.createUnresolved(
                        proxyConfig.server,
                        proxyConfig.port
                    )
                    Proxy(Proxy.Type.HTTP, address)
                }
                url.openConnection(proxy)
            }
            ProxyConfig.Type.REVERSE -> {
                val con = HttpUrl.get(url)?.let {
                    try {
                        URL(ReverseProxyHandler.replaceUrl(it, proxyConfig.server))
                    } catch (e: Throwable) {
                        throw IOException("Invalid reverse proxy format")
                    }
                }?.openConnection() ?: url.openConnection()
                if (proxyConfig.userName.isNotEmpty() && proxyConfig.password.isNotEmpty()) {
                    val credential = Credentials.basic(
                        proxyConfig.userName,
                        proxyConfig.password
                    )
                    con.setRequestProperty("Proxy-Authorization", credential)
                }
                con
            }
        }
    } else url.openConnection()
    if (authorization.hasAuthorization) {
        connection.setRequestProperty("Authorization", authorization.getAuthorizationHeader(Request.Builder().url(url).build()))
    }
    return connection as HttpURLConnection
}
