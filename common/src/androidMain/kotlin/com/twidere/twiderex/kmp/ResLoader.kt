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
package com.twidere.twiderex.kmp

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import coil.compose.LocalImageLoader
import coil.compose.rememberImagePainter
import coil.decode.SvgDecoder
import com.twidere.twiderex.component.painterResource
import dev.icerock.moko.resources.FileResource
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.StringResource

actual class ResLoader(
    private val context: Context,
) {
    actual fun getString(
        res: StringResource,
        vararg args: Any
    ): String {
        return context.getString(res.resourceId, *args)
    }

    @Composable
    actual fun getSvg(res: FileResource): Painter {
        val data = "android.resource://${context.packageName}/raw/${context.resources.getResourceEntryName(res.rawResId)}"
        return rememberImagePainter(
            data,
            LocalImageLoader.current
                .newBuilder().componentRegistry { add(SvgDecoder(context)) }.build()
        )
    }

    @Composable
    actual fun getImage(res: ImageResource): Painter {
        return painterResource(res.drawableResId)
    }
}
