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
package com.twidere.twiderex.image

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Codec

internal class GifPainter(private val codec: Codec, private val parentScope: CoroutineScope) : Painter() {
    override val intrinsicSize: Size
        get() = Size(codec.width.toFloat(), codec.height.toFloat())
    private var frameIndex = mutableStateOf(0)
    private var rememberScope: CoroutineScope? = null
    private var bitmapCache: Bitmap? = null

    fun start() {
        if (rememberScope != null && rememberScope?.isActive == true) return
        rememberScope?.cancel()
        val context = parentScope.coroutineContext
        rememberScope = CoroutineScope(context + SupervisorJob(context[Job]))
        rememberScope?.launch {
            while (true) {
                for ((index, frame) in codec.framesInfo.withIndex()) {
                    frameIndex.value = index
                    delay(frame.duration.toLong())
                }
            }
        }
    }

    fun stop() {
        if (rememberScope?.isActive == true) rememberScope?.cancel()
    }

    override fun DrawScope.onDraw() {
        val bitmap = recycleBitmap(codec)
        codec.readPixels(bitmap, frameIndex.value)
        val intSize = IntSize(size.width.toInt(), size.height.toInt())
        drawImage(bitmap.asComposeImageBitmap(), dstSize = intSize)
    }

    private fun recycleBitmap(codec: Codec): Bitmap {
        return bitmapCache?.let {
            if (codec.width == bitmapCache?.width && codec.height == bitmapCache?.height) {
                it.apply { allocPixels(codec.imageInfo) }
            } else null
        } ?: Bitmap().apply { allocPixels(codec.imageInfo) }
            .also {
                bitmapCache = it
            }
    }
}
