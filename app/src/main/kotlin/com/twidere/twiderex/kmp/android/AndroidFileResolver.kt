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
package com.twidere.twiderex.kmp.android

import android.content.ContentResolver
import android.net.Uri
import com.twidere.twiderex.kmp.FileResolver
import java.io.InputStream
import java.io.OutputStream

class AndroidFileResolver(private val contentResolver: ContentResolver) : FileResolver {
    override fun getMimeType(file: String): String? {
        return contentResolver.getType(Uri.parse(file))
    }

    override fun getFileSize(file: String): Long? {
        return contentResolver.openFileDescriptor(Uri.parse(file), "r")?.statSize
    }

    override fun openInputStream(file: String): InputStream? {
        return contentResolver.openInputStream(Uri.parse(file))
    }

    override fun openOutputStream(file: String): OutputStream? {
        return contentResolver.openOutputStream(Uri.parse(file))
    }
}
