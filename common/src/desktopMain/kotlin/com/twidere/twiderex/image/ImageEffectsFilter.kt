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

import java.awt.image.BufferedImage
import java.awt.image.ConvolveOp
import java.awt.image.Kernel

object ImageEffectsFilter {
    fun applyBlurFilter(bitmap: BufferedImage, radius: Int, scale: Float): BufferedImage {
        val scaledBitmap = applyPixelFilter(bitmap, scale)
        var result = BufferedImage(scaledBitmap.getWidth(), scaledBitmap.getHeight(), scaledBitmap.type)

        val graphics = result.getGraphics()
        graphics.drawImage(scaledBitmap, 0, 0, null)
        graphics.dispose()

        val weight: Float = 1.0f / (radius * radius)
        val matrix = FloatArray(radius * radius)

        for (i in matrix.indices) {
            matrix[i] = weight
        }

        val kernel = Kernel(radius, radius, matrix)
        val op = ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null)
        result = op.filter(result, null)

        return result.getSubimage(
            radius,
            radius,
            result.width - radius * 2,
            result.height - radius * 2
        )
    }

    fun applyPixelFilter(bitmap: BufferedImage, scale: Float): BufferedImage {
        val w: Int = bitmap.width
        val h: Int = bitmap.height
        var result = scaleBitmapAspectRatio(bitmap, (w * scale).toInt(), (h * scale).toInt())
        result = scaleBitmapAspectRatio(result, w, h)

        return result
    }

    private fun scaleBitmapAspectRatio(
        bitmap: BufferedImage,
        width: Int,
        height: Int
    ): BufferedImage {
        val boundW: Float = width.toFloat()
        val boundH: Float = height.toFloat()

        val ratioX: Float = boundW / bitmap.width
        val ratioY: Float = boundH / bitmap.height
        val ratio: Float = if (ratioX < ratioY) ratioX else ratioY

        val resultH = (bitmap.height * ratio).toInt()
        val resultW = (bitmap.width * ratio).toInt()

        val result = BufferedImage(resultW, resultH, BufferedImage.TYPE_INT_ARGB)
        val graphics = result.createGraphics()
        graphics.drawImage(bitmap, 0, 0, resultW, resultH, null)
        graphics.dispose()

        return result
    }
}
