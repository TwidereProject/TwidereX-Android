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
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object FileProviderHelper {
    private const val providerName = "com.twidere.twiderex.fileprovider"
    private const val shareDir = "shares/"
    private const val mediaDir = "medias/"

    fun getUriFromShares(mediaFileName: String, context: Context): Uri {
        return getUri(
            fileName = mediaFileName,
            dir = shareDir,
            context = context
        )
    }

    fun getUriFromMedias(mediaFileName: String, context: Context): Uri {
        return getUri(
            fileName = mediaFileName,
            dir = mediaDir,
            context = context
        )
    }

    private fun getUri(fileName: String, dir: String, context: Context): Uri {
        val dirFile = File(context.externalCacheDir, dir).apply {
            if (!exists()) mkdirs()
        }
        val file = File(dirFile, fileName).apply {
            if (!exists()) createNewFile()
        }
        return FileProvider.getUriForFile(context, providerName, file)
    }
}
