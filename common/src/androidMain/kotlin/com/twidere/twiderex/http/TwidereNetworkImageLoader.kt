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
import android.graphics.drawable.Drawable
import android.view.View
import coil.ImageLoader
import coil.bitmap.BitmapPool
import coil.memory.MemoryCache
import coil.request.DefaultRequestOptions
import coil.request.Disposable
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.target.Target
import coil.target.ViewTarget
import com.twidere.services.http.authorization.Authorization
import com.twidere.twiderex.component.foundation.NetworkImageState
import okhttp3.Headers
import okhttp3.Request
import java.net.URL

internal class TwidereNetworkImageLoader(
    private val realImageLoader: ImageLoader,
    private val context: Context,
    private val authorization: Authorization,
    private val onImageStateChanged: (NetworkImageState) -> Unit = {}
) : ImageLoader {
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
        val data = request.data
        request.newBuilder(request.context)
            .apply {
                if (authorization.hasAuthorization) {
                    headers(
                        headers = Headers.headersOf(
                            "Authorization",
                            authorization.getAuthorizationHeader(Request.Builder().url(URL(data.toString())).build())
                        )
                    )
                }
            }
            .target(
                if (request.target is ViewTarget<*>) {
                    ViewTargetWrapper(request.target as ViewTarget<*>, onImageStateChanged)
                } else {
                    TargetWrapper(request.target, onImageStateChanged)
                }
            )
            .build()
        return if (authorization.hasAuthorization) {
            request.newBuilder(
                request.context
            ).headers(
                headers = Headers.headersOf(
                    "Authorization",
                    authorization.getAuthorizationHeader(Request.Builder().url(URL(data.toString())).build())
                )
            ).build()
        } else {
            request.newBuilder(request.context)
                .data(data)
                .build()
        }
    }

    private open class TargetWrapper(
        private val target: Target?,
        private val onImageStateChanged: (NetworkImageState) -> Unit = {}
    ) : Target {
        override fun onStart(placeholder: Drawable?) {
            super.onStart(placeholder)
            target?.onStart(placeholder)
            onImageStateChanged.invoke(NetworkImageState.LOADING)
        }

        override fun onError(error: Drawable?) {
            super.onError(error)
            target?.onError(error)
            onImageStateChanged.invoke(NetworkImageState.ERROR)
        }

        override fun onSuccess(result: Drawable) {
            super.onSuccess(result)
            target?.onSuccess(result)
            onImageStateChanged.invoke(NetworkImageState.SUCCESS)
        }
    }

    private class ViewTargetWrapper<T : View>(
        private val target: ViewTarget<T>,
        onImageStateChanged: (NetworkImageState) -> Unit = {}
    ) : TargetWrapper(target, onImageStateChanged), ViewTarget<T> {
        override val view: T
            get() = target.view
    }
}
