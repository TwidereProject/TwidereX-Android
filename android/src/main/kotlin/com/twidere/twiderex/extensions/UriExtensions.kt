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
package com.twidere.twiderex.extensions

import android.content.Context
import android.net.Uri
import com.twidere.twiderex.model.enums.MediaType

fun Uri.mediaType(context: Context): MediaType {
    val mimeType = context.contentResolver.getType(this) ?: ""
    return when {
        mimeType.startsWith("video") -> MediaType.video
        mimeType == "image/gif" -> MediaType.animated_gif
        mimeType.startsWith("image") -> MediaType.photo
        mimeType.startsWith("audio") -> MediaType.audio
        else -> MediaType.other
    }
}
