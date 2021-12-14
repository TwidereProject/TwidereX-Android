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
package com.twidere.twiderex.image

import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toPainter
import com.twidere.twiderex.component.foundation.NetworkImageState
import com.twidere.twiderex.component.image.ImageEffects
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.jetbrains.skia.Codec
import org.jetbrains.skia.Data
import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.imageio.ImageIO

/**
 * current support http/https/file/filepath only
 */
internal class ImagePainter(
    private val request: Any,
    private val parentScope: CoroutineScope,
    private val imageCache: ImageCache,
    private val imageEffects: ImageEffects,
    private val httpConnection: (URL) -> HttpURLConnection = { it.openConnection() as HttpURLConnection },
    private val onImageStateChanged: (NetworkImageState) -> Unit
) : Painter(), RememberObserver {
    private var painter = mutableStateOf<Painter?>(null)

    override val intrinsicSize: Size
        get() = painter.value?.intrinsicSize ?: Size.Unspecified
    private var alpha: Float = 1f
    private var colorFilter: ColorFilter? = null
    private var rememberScope: CoroutineScope? = null

    override fun applyAlpha(alpha: Float): Boolean {
        this.alpha = alpha
        return true
    }

    override fun applyColorFilter(colorFilter: ColorFilter?): Boolean {
        this.colorFilter = colorFilter
        return true
    }

    override fun DrawScope.onDraw() {
        painter.value?.apply {
            draw(size, alpha, colorFilter)
            if (this is GifPainter) {
                start()
            }
        }
    }

    override fun onAbandoned() = onForgotten()

    override fun onForgotten() {
        rememberScope?.cancel()
        rememberScope = null
        painter.value?.let {
            if (it is GifPainter) {
                it.stop()
            }
        }
    }

    override fun onRemembered() {
        rememberScope?.cancel()
        val context = parentScope.coroutineContext
        rememberScope = CoroutineScope(parentScope.coroutineContext + SupervisorJob(context[Job]))
        onImageStateChanged(NetworkImageState.LOADING)
        rememberScope?.launch {
            try {
                execute()
                onImageStateChanged(NetworkImageState.SUCCESS)
            } catch (e: Throwable) {
                onImageStateChanged(NetworkImageState.ERROR)
            }
        }
    }

    private fun generatePainter(inputStream: InputStream, mimeType: String) {
        var error: Throwable? = null
        try {
            if (mimeType == "image/gif") {
                painter.value = GifPainter(Codec.makeFromData(Data.makeFromBytes(inputStream.readAllBytes())), parentScope)
            } else {
                ImageIO.read(inputStream)?.let { image ->
                    imageEffects.blur?.let {
                        ImageEffectsFilter.applyBlurFilter(image, it.blurRadius.toInt(), it.bitmapScale)
                    } ?: image
                }?.let {
                    painter.value = it.toPainter()
                }
            }
        } catch (e: Throwable) {
            error = e
        } finally {
            try {
                inputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (error != null) throw error
    }

    private fun fileRequest(file: File) {
        try {
            val connection = file.toURI().toURL().openConnection()
            val mimeType = connection.contentType
            val input = file.inputStream()
            generatePainter(input, mimeType)
        } catch (e: Throwable) {
            throw e
        }
    }

    private suspend fun networkRequest(url: URL) {
        var connection: HttpURLConnection? = null
        var error: Throwable? = null
        try {
            var mimeType = ""
            val input = imageCache.fetch(url.toString())?.let {
                mimeType = it.toURI().toURL().openConnection().contentType
                it.inputStream()
            } ?: httpConnection(url).let {
                connection = it
                it.setRequestProperty("Accept", "image/*")
                it.connect()
                mimeType = it.contentType
                imageCache.store(url.toString(), it.inputStream)?.inputStream() ?: it.inputStream
            }
            generatePainter(
                inputStream = input,
                mimeType = mimeType
            )
        } catch (e: Throwable) {
            error = e
        } finally {
            try {
                connection?.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (error != null) throw error
    }

    private suspend fun execute() {
        when (request) {
            is URL, is String -> {
                request.toString().also {
                }.request()
            }

            is File -> {
                fileRequest(request)
            }
            else -> {
                throw NotImplementedError()
            }
        }
    }

    private suspend fun String.request() {
        try {
            when {
                isHttpRequest() -> networkRequest(URL(this))
                isFileRequest() -> fileRequest(File(this))
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun String.isHttpRequest() = startsWith("http")

    private fun String.isFileRequest() = File(this).exists()
}
