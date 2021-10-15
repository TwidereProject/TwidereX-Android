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
package com.twidere.twiderex.extensions

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.twidere.twiderex.kmp.file
import java.io.File

private const val PROVIDER_NAME = "com.twidere.twiderex.fileprovider"

// TODO Make it internal after migrate ui to common
val uriRex = Regex("[a-zA-z]+://.+")
fun String.toUri(context: Context): Uri {
    return try {
        if (!uriRex.matches(this)) throw Error()
        Uri.parse(this)
    } catch (e: Throwable) {
        file().fileProviderUri(context)
    }
}

fun File.fileProviderUri(context: Context): Uri {
    return FileProvider.getUriForFile(context, PROVIDER_NAME, this)
}
