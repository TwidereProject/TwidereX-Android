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
package com.twidere.twiderex.utils

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.RequiresApi
import coil.size.Size
import coil.transform.Transformation
import com.google.android.renderscript.Toolkit

@RequiresApi(18)
class BlurTransformation @JvmOverloads constructor(
    private val context: Context,
    private val radius: Int = DEFAULT_RADIUS,
) : Transformation {

    init {
        require(radius in 0..25) { "radius must be in [0, 25]." }
    }

    override val cacheKey: String
        get() = "${BlurTransformation::class.java.name}-$radius"

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        return Toolkit.blur(input, radius)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is BlurTransformation &&
            context == other.context &&
            radius == other.radius
    }

    override fun hashCode(): Int {
        var result = context.hashCode()
        result = 31 * result + radius.hashCode()
        return result
    }

    override fun toString(): String {
        return "BlurTransformation(context=$context, radius=$radius)"
    }

    private companion object {
        private const val DEFAULT_RADIUS = 10
    }
}
