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
@file:Suppress("DEPRECATION")
// for migration: https://developer.android.com/guide/topics/renderscript/migrate
package com.twidere.twiderex.component.foundation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.bitmap.BitmapPool
import coil.compose.rememberImagePainter
import coil.memory.MemoryCache
import coil.request.DefaultRequestOptions
import coil.request.Disposable
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.request.SuccessResult
import com.twidere.twiderex.http.TwidereNetworkImageLoader
import com.twidere.twiderex.ui.LocalActiveAccount

@Composable
fun BlurImage(
    @DrawableRes resource: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    blurRadius: Float = 25f,
    bitmapScale: Float = 1f
) {
    val context = LocalContext.current
    val painter = remember {
        VectorDrawableCompat.create(context.resources, resource, null)
            ?.toBitmap()?.let {
                applyBlurFilter(it, context, blurRadius, bitmapScale)
            }?.let {
                BitmapPainter(it.asImageBitmap())
            }
    }
    Image(
        painter = painter ?: painterResource(id = resource),
        contentDescription = contentDescription,
        modifier = modifier,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter
    )
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun NetworkBlurImage(
    data: Any,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    blurRadius: Float = 12f,
    bitmapScale: Float = 0.5f,
    placeholder: @Composable (() -> Unit)? = null,
) {
    val context = LocalContext.current
    val accountDetails = LocalActiveAccount.current
    val painter = rememberImagePainter(
        data = data,
        imageLoader = BlurImageLoader(
            context,
            blurRadius,
            bitmapScale,
            TwidereNetworkImageLoader(
                realImageLoader = buildRealImageLoader(),
                context = context,
                account = accountDetails
            )
        )
    )
    NetworkImage(
        data = if (blurRadius != 0f || bitmapScale != 1f) painter else data,
        modifier = modifier,
        contentScale = contentScale,
        placeholder = placeholder
    )
}

private fun applyBlurFilter(src: Bitmap, context: Context, blurRadius: Float, bitmapScale: Float): Bitmap {
    val rs = RenderScript.create(context.applicationContext)

    val matrix = Matrix().apply {
        postScale(bitmapScale, bitmapScale)
    }
    // scale bitmap first then blur
    val scaleBitmap = if (bitmapScale == 1f) {
        src
    } else {
        Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
    }.copy(Bitmap.Config.ARGB_8888, true)

    val input = Allocation.createFromBitmap(rs, scaleBitmap)
    val output = Allocation.createTyped(rs, input.type)
    val script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
    script.setRadius(blurRadius)
    script.setInput(input)
    script.forEach(output)
    output.copyTo(scaleBitmap)
    return scaleBitmap
}

class BlurImageLoader(
    private val context: Context,
    private val blurRadius: Float,
    private val bitmapScale: Float,
    private val realPainter: ImageLoader
) : ImageLoader {
    override val bitmapPool: BitmapPool
        get() = realPainter.bitmapPool
    override val defaults: DefaultRequestOptions
        get() = realPainter.defaults
    override val memoryCache: MemoryCache
        get() = realPainter.memoryCache

    override fun enqueue(request: ImageRequest): Disposable {
        return realPainter.enqueue(request)
    }

    override suspend fun execute(request: ImageRequest): ImageResult {
        val result = realPainter.execute(request)
        if (result !is SuccessResult) return result
        return result.drawable.let {
            applyBlurFilter(it.toBitmap(), context, blurRadius, bitmapScale)
        }.let {
            SuccessResult(it.toDrawable(context.resources), request, result.metadata)
        }
    }

    override fun shutdown() {
        realPainter.shutdown()
    }

    override fun newBuilder() = ImageLoader.Builder(context)
}
