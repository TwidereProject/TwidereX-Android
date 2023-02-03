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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.graphics.painter.Painter
import com.seiko.imageloader.ImageLoaderConfigBuilder
import com.seiko.imageloader.ImageRequestState
import com.seiko.imageloader.component.fetcher.KtorUrlFetcher
import com.seiko.imageloader.intercept.BlurInterceptor
import com.seiko.imageloader.model.ImageRequest
import com.seiko.imageloader.model.blur
import com.seiko.imageloader.rememberAsyncImagePainter
import com.twidere.services.http.authorization.Authorization
import com.twidere.services.http.config.HttpConfig
import com.twidere.services.http.proxy
import com.twidere.twiderex.component.foundation.NetworkImageState
import com.twidere.twiderex.component.image.ImageEffects
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import okhttp3.Request

@Composable
internal fun rememberNetworkImagePainter(
  data: Any,
  authorization: Authorization,
  httpConfig: HttpConfig,
  effects: ImageEffects,
  onImageStateChanged: (NetworkImageState) -> Unit
): Painter {
  val request = remember(data, effects.blur) {
    ImageRequest {
      data(data)
      effects.blur?.let {
        blur(it.blurRadius)
      }
      components {
        add(proxyKtorUrlFetcher(data, authorization, httpConfig))
      }
    }
  }
  val painter = rememberAsyncImagePainter(request)
  LaunchedEffect(painter) {
    snapshotFlow { painter.requestState }.collect {
      when (it) {
        ImageRequestState.Loading -> {
          onImageStateChanged(NetworkImageState.LOADING)
        }
        ImageRequestState.Success -> {
          onImageStateChanged(NetworkImageState.SUCCESS)
        }
        is ImageRequestState.Failure -> {
          onImageStateChanged(NetworkImageState.ERROR)
        }
      }
    }
  }
  return painter
}

fun ImageLoaderConfigBuilder.commonConfig() {
  interceptor {
    addInterceptor(BlurInterceptor())
  }
}

private fun proxyKtorUrlFetcher(
  data: Any,
  authorization: Authorization,
  httpConfig: HttpConfig,
) = KtorUrlFetcher.Factory {
  val engine = OkHttp.create {
    config {
      if (httpConfig.proxyConfig.enable &&
        httpConfig.proxyConfig.server.isNotEmpty()
      ) {
        proxy(httpConfig.proxyConfig)
      }
    }
  }
  HttpClient(engine) {
    defaultRequest {
      if (authorization.hasAuthorization) {
        header(
          "Authorization",
          authorization.getAuthorizationHeader(
            Request.Builder()
              .url(data.toString())
              .build()
          )
        )
      }
    }
  }
}
