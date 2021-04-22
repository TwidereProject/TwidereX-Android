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
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat

@Composable
fun BlurImage(
    @DrawableRes resource: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    blurRadius: Float = 25f
) {
    val context = LocalContext.current
    val painter = remember {
        VectorDrawableCompat.create(context.resources, resource, null)
            ?.toBitmap()?.let {
                applyBlurFilter(it, context, blurRadius)
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

private fun applyBlurFilter(bitmap: Bitmap, context: Context, blurRadius: Float): Bitmap {
    val result: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
    val renderScript: RenderScript = RenderScript.create(context)
    val tmpIn: Allocation = Allocation.createFromBitmap(renderScript, bitmap)
    val tmpOut: Allocation = Allocation.createFromBitmap(renderScript, result)
    val theIntrinsic: ScriptIntrinsicBlur =
        ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
    theIntrinsic.setRadius(blurRadius)
    theIntrinsic.setInput(tmpIn)
    theIntrinsic.forEach(tmpOut)

    tmpOut.copyTo(result)

    return result
}
