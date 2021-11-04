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
package com.twidere.twiderex.component.foundation.platform

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.twidere.twiderex.component.painterResource
import dev.icerock.moko.resources.FileResource

@Composable
actual fun PlatformBlurImage(
    resource: FileResource,
    contentDescription: String?,
    modifier: Modifier,
    alignment: Alignment,
    contentScale: ContentScale,
    alpha: Float,
    colorFilter: ColorFilter?,
    blurRadius: Float,
    bitmapScale: Float,
) {
    val context = LocalContext.current
    val painter = remember {
        VectorDrawableCompat.create(context.resources, resource.rawResId, null)
            ?.toBitmap()?.let {
                applyBlurFilter(it, context, blurRadius, bitmapScale)
            }?.let {
                BitmapPainter(it.asImageBitmap())
            }
    }
    Image(
        painter = painter ?: painterResource(res = resource),
        contentDescription = contentDescription,
        modifier = modifier,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter
    )
}

private fun applyBlurFilter(
    src: Bitmap,
    context: Context,
    blurRadius: Float,
    bitmapScale: Float
): Bitmap {
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
