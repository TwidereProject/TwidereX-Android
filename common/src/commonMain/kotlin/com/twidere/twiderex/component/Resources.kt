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
package com.twidere.twiderex.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.twidere.twiderex.kmp.ResLoader
import dev.icerock.moko.resources.FileResource
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.StringResource

@Composable
fun stringResource(res: StringResource, vararg formatArgs: Any): String {
    return ResLoader.get.getString(res, *formatArgs)
}

@Composable
fun stringResource(res: StringResource): String {
    return ResLoader.get.getString(res)
}

/**
 * res: FileResource:svg, ImageResource
 */
@Composable
fun painterResource(res: Any): Painter {
    return when (res) {
        is FileResource -> ResLoader.get.getSvg(res)
        is ImageResource -> ResLoader.get.getImage(res)
        else -> throw NotImplementedError()
    }
}
