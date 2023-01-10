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

import com.seiko.imageloader.intercept.Interceptor
import com.seiko.imageloader.request.ComposeImageResult
import com.seiko.imageloader.request.ImageResult
import com.twidere.twiderex.component.image.ImageEffects
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.FilterTileMode
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageFilter
import org.jetbrains.skia.Paint

actual class BlurInterceptor actual constructor(private val effects: ImageEffects) : Interceptor {
  override suspend fun intercept(chain: Interceptor.Chain): ImageResult {
    val result = chain.proceed(chain.request)
    val blur = effects.blur
    if (blur != null && result is ComposeImageResult) {
      return result.copy(
        image = applyBlurFilter(result.image, blur.blurRadius.toFloat()),
      )
    }
    return result
  }

  private fun applyBlurFilter(bitmap: Bitmap, radius: Float): Bitmap {
    val result = Bitmap().apply {
      allocN32Pixels(bitmap.width, bitmap.height)
    }
    val blur = Paint().apply {
      imageFilter = ImageFilter.makeBlur(radius, radius, FilterTileMode.CLAMP)
    }
    val canvas = Canvas(result)
    canvas.saveLayer(null, blur)
    canvas.drawImageRect(Image.makeFromBitmap(bitmap), bitmap.bounds.toRect())
    canvas.restore()
    canvas.readPixels(result, 0, 0)
    canvas.close()
    return result
  }
}
